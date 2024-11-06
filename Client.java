import java.io.*;
import java.net.*;

public class Client {
    Node node;

    public Client(Node node) {
        this.node = node;
        synchronized (node) {
            connectChannels(node);
        }
    }

    public void connectChannels(Node node) {
        System.out.println("[CLIENT] Making channel array...");
        for (int i = 0; i < node.totalNodes; i++) {
            if (i == node.id)
                continue;
            String host = node.getHost(i);
            int port = node.getPort(i);
            try {
                Socket client = new Socket();
                client.connect(new InetSocketAddress(host, port));
                client.setKeepAlive(true);
                node.idToChannelMap.put(node.hostToId_PortMap.get(host).get(0), client);
                System.out.println("[CLIENT] Connected to " + host + ":" + port);
            } catch (IOException error) {
                System.out.println("[CLIENT] Unable to connect to " + host + ":" + port);
            }
        }
    }

    public void enterCS() {
        // Start critical section
        synchronized (node) {
            node.under_cs = true;
            node.exp.recordStart();
        }
        // Check for keys until has all the keys
        System.out.println("[CLIENT]  Checking if all the keys are present or not");
        boolean send_req = true;
        while (!checkKeys(send_req)) {
            send_req = false;
        }
        // Entering the CS
        System.out.println("[CLIENT]  All Keys are present. Entering the CS");
        executeCS();
    }

    /** Function to perform critical section */
    public void executeCS() {
        try {
            // Saving the state to file.
            node.writeState();
            // int wait = 1 + (int) (Math.random() * (node.executionTime));
            // Thread.sleep(wait);
            Thread.sleep(node.executionTime);
            node.exp.recordEnd();
            leaveCS();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void leaveCS() {
        // Critical Section is done.
        synchronized (node) {
            node.clock = Math.max(node.clock, node.pendingClock) + 1;
            node.under_cs = false;
            node.pending_req = false;
        }

        // Sending the keys to all the neighbours
        node.pendingRequest.forEach((key, value) -> {
            if (value != null) {
                node.client.sendKey(value, false);
            }
        });
        // Sent all the keys...clearing the queue
        synchronized (node) {
            node.pendingRequest.clear();
        }

    }

    /** Function to request key from respective process */
    public void requestKey(int nodeId) {
        // Send Msg to Specific Channel.
        Socket channel = node.idToChannelMap.get(nodeId);
        Message req_msg = new Message(MessageType.REQUEST, node.id, node.clock, -1);
        try {
            OutputStream outStream = channel.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outStream);

            byte[] msgBytes = req_msg.toMessageBytes();
            dataOut.writeInt(msgBytes.length);
            dataOut.write(msgBytes);
            dataOut.flush();
            node.exp.totalMessages += 1;
            System.out.println("[CLIENT] Sent request to node-" + nodeId + " for key.");
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    /** Function to send the key to the respective process */
    public void sendKey(Message msg, boolean needBack) {
        if (node.keys.contains(msg.id)) {
            synchronized (node) {
                try {
                    node.keys.remove(Integer.valueOf(msg.id));
                    // Asking for key back if required
                    Message reply_msg = new Message(needBack ? MessageType.BOTH : MessageType.REPLY, node.id,
                            node.clock,
                            node.id);
                    Socket channel = node.idToChannelMap.get(msg.id);

                    OutputStream outStream = channel.getOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(outStream);

                    byte[] msgBytes = reply_msg.toMessageBytes();
                    dataOut.writeInt(msgBytes.length);
                    dataOut.write(msgBytes);
                    dataOut.flush();

                    System.out.println("[CLIENT] Key sent to node-" + msg.id);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        } else {
            System.out.println("[CLIENT] Error.. can not send the key if not present");
        }
    }

    /**
     * Function to check if all keys for entering critical section is present
     */
    public boolean checkKeys(boolean send_req) {
        boolean hasAllKeys = true;
        synchronized (node) {
            for (int i = 0; i < node.totalNodes; i++) {
                if (i == node.id)
                    continue;
                if (!node.keys.contains(i)) {
                    if (hasAllKeys && node.pending_req == false) {
                        synchronized (node) {
                            node.clock += 1;
                            node.pending_req = true;
                        }
                    }
                    if (send_req)
                        requestKey(i);
                    hasAllKeys = false;
                }
            }
        }
        return hasAllKeys;
    }

    // ---
    public void init() {
        Thread client = new Thread(() -> {
            System.out.println("[CLIENT] Starting...");
            try {
                while (node.requestSent < node.maxRequest) {
                    if (node.pending_req || node.under_cs)
                        continue;
                    try {
                        // int wait = 1 + (int) (Math.random() * (node.requestDelay));
                        // Thread.sleep(wait);
                        Thread.sleep(node.requestDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    enterCS();
                    synchronized (node) {
                        node.requestSent += 1;
                    }
                    System.out.println("[CLIENT]  Request for CS #" + node.requestSent + " is completed");
                }

                node.exp.write();
                System.out.println("[CLIENT]  All request for CS has been sent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.start();
    }
}
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
        for (int i = 1; i <= node.totalNodes; i++) {
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
        }
        executeCS();
    }

    /** Function to perform critical section */
    public void executeCS() {
        try {
            Thread.sleep(node.executionTime);
            leaveCS();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void leaveCS() {
        synchronized (node) {
            node.under_cs = false;
        }
    }

    /** Function to request key from respective process */
    public void requestKey(int nodeId) {
        // Send Msg to Specific Channel.
        Socket channel = node.idToChannelMap.get(nodeId);
        Message req_msg = new Message(MessageType.REQUEST, node.id, 0, -1);
        try {
            OutputStream outStream = channel.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outStream);

            byte[] msgBytes = req_msg.toMessageBytes();
            dataOut.writeInt(msgBytes.length);
            dataOut.write(msgBytes);
            dataOut.flush();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    /** Function to send the key to the respective process */
    public void sendKey(Message msg, boolean needBack) {
        if (node.keys.contains(msg.key)) {
            synchronized (node) {
                try {
                    while (node.keys.contains(msg.key)) {
                        node.keys.remove(msg.key);
                    }

                    Message reply_msg = new Message(needBack ? MessageType.BOTH : MessageType.REPLY, node.id, 0,
                            node.id);
                    Socket channel = node.idToChannelMap.get(msg.id);

                    OutputStream outStream = channel.getOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(outStream);

                    byte[] msgBytes = reply_msg.toMessageBytes();
                    dataOut.writeInt(msgBytes.length);
                    dataOut.write(msgBytes);
                    dataOut.flush();

                    System.out.println("[CLIENT]: Key sent to Node-" + msg.id);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        } else {
            System.out.println("[CLIENT]: Error.. cant send the key if not present");
        }
    }

    /**
     * Function to check if all keys for entering critical section is present
     */
    public boolean checkKeys() {
        boolean hasAllKeys = true;
        synchronized (node) {
            for (int i = 1; i <= node.totalNodes; i++) {
                if (i == node.id)
                    continue;
                if (!node.keys.contains(i)) {
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
                    try {
                        Thread.sleep(node.requestDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    enterCS();
                    synchronized (node) {
                        node.requestSent += 1;
                    }
                }
                System.out.println("[CLIENT]: All request for CS has been sent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.start();
    }
}
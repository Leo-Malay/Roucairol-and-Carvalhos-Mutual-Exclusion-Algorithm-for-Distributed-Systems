import java.util.*;
import java.io.*;
import java.net.*;

public class Client {

    List<Socket> channelList;
    Node node;

    public Client(Node node) {
        this.node = node;
        synchronized (node) {
            this.channelList = connectChannels(node);
        }
    }

    public List<Socket> connectChannels(Node node) {
        System.out.println("[CLIENT] Making channel array...");
        List<Socket> channelList = new ArrayList<>();
        for (int i = 1; i <= node.totalNodes; i++)
        {
            if (i == node.id) continue;
            String host = node.getHost(i);
            int port = node.getPort(i);
            try {
                Socket client = new Socket();
                client.connect(new InetSocketAddress(host, port));
                client.setKeepAlive(true);
                channelList.add(client);
                node.idToChannelMap.put(node.hostToId_PortMap.get(host).get(0), client);
                System.out.println("[CLIENT] Connected to " + host + ":" + port);
            } catch (IOException error) {
                System.out.println("[CLIENT] Unable to connect to " + host + ":" + port);
            }
        }
        return channelList;
    }

    public void mapProtocol() {
        while (true) {
            if (node.msgSent >= node.maxNumber) {
                System.out.println("[CLIENT] Node state (ACTIVE -> PASSIVE) permanently...");
                node.state = false;
                sendCustomEnd();
                node.pem_passive = true;
                break;
            }

            if (node.state == true) {
                Random random = new Random();
                int count = random.nextInt(node.maxPerActive - node.minPerActive + 1) + node.minPerActive;
                synchronized (node) {
                    sendBulkMsg(count);
                }
            } else {
                try {
                    System.out.println("[CLIENT] Node state (ACTIVE -> PASSIVE) after sending " + node.msgSent + "/"
                            + node.maxNumber + " messages");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendBulkMsg(int count) {

        // System.out.println(String.format("Sending %d messages to neighbours...",
        // count));
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            if (node.msgSent >= node.maxNumber) {
                node.state = false;
                break;
            }
            int randomNumber = random.nextInt(this.channelList.size());
            int destination = node.neighbours.get(node.id).get(randomNumber);
            // System.out.println("Sent a message to " + destination);
            node.sndClk.set(destination, node.sndClk.get(destination) + 1);
            Socket channel = channelList.get(randomNumber);
            String messageString = "Hello " + node.name + " (" + node.msgSent + 1 + "/" + node.maxNumber + ")";
            Message msg = new Message(node.id, node.clock, messageString);
            Client.sendMsg(msg, channel, node);

            try {
                Thread.sleep(node.minSendDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        node.changeState();

    }

    public static void sendMsg(Message msg, Socket channel, Node node) {
        try {
            OutputStream outStream = channel.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outStream);

            byte[] msgBytes = msg.toMessageBytes();
            dataOut.writeInt(msgBytes.length);
            dataOut.write(msgBytes); // Send message
            dataOut.flush();

            if (msg.messageType == MessageType.APPLICATION) {
                int prevEntry = node.clock.get(node.id);
                node.clock.set(node.id, prevEntry + 1);
                node.msgSent++;
            }
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

// ---
    public static void csEnter() {}
    public static void executeCS() {}
    public static void csLeave() {}

    public static void requestKeys(int nodeId) {
        // Send Msg to Specific Channel.
        Socket channel = node.idToChannelMap.get(nodeId);
        try {
            OutputStream outStream = channel.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(outStream);

            byte[] msgBytes = msg.toMessageBytes();
            dataOut.writeInt(msgBytes.length);
            dataOut.write(msgBytes);
            dataOut.flush();
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public static boolean checkKeys() {
        boolean hasAllKeys = true;
        for (int i = 1; i <= node.totalNodes; i++) {
            if (i == node.id) continue;
            if (!node.keys.contains(i)) {
                requestKey(i);
                hasAllKeys = false;
            }
        }
        return hasAllKeys;
    }
// ---
    public void init() {
        Thread client = new Thread(() -> {
            System.out.println("[CLIENT] Starting...");
            try {
                if (node.id == 0) {
                    node.changeState();
                }
                System.out.println("[CLIENT] Init Map Protocol...");
                node.client.mapProtocol();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.start();
    }
}
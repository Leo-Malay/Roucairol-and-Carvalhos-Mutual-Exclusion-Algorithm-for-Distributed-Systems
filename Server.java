import java.io.*;
import java.net.*;

public class Server {
    int port;
    Node node;
    ServerSocket server;

    public Server(int port, Node node) {
        this.port = port;
        this.node = node;
    }

    public void handleMessage(Message msg) {
        if (msg.id != -1)
            System.out.println("[SERVER] Message received from Node " + msg.id);
        // Message Handler
        if (msg.msgType == MessageType.REQUEST) {
            // Handle incoming Request for key.
            if (node.under_cs) {
                // Adding to pending request.
                synchronized (node) {
                    node.pendingRequest.put(msg.id, msg);
                    node.pendingClock = Math.max(node.clock, msg.clock + 1);
                }
            } else if (node.pending_req) {
                // This is for when the request for CS is send
                if (node.clock > msg.clock) {
                    // Send the key
                    node.client.sendKey(msg, true);
                } else if (node.clock == msg.clock) {
                    if (node.id < msg.id) {
                        // Add to pending
                        synchronized (node) {
                            node.pendingRequest.put(msg.id, msg);
                        }
                    } else {
                        // Send the key
                        node.client.sendKey(msg, true);
                    }
                } else {
                    // Add to pending
                    synchronized (node) {
                        node.pendingRequest.put(msg.id, msg);
                    }
                }
            } else {
                // Sending the key
                node.client.sendKey(msg, false);
            }
        } else if (msg.msgType == MessageType.REPLY) {
            // Handle incoming Reply with key.
            if (!node.keys.contains(msg.key)) {
                System.out.println("[SERVER]: Key " + msg.key + " received from Node-" + msg.id);
                synchronized (node) {
                    node.keys.add(msg.key);
                    node.clock = Math.max(node.clock, msg.clock);
                }
            } else {
                System.out.println("[SERVER]: Key " + msg.key + " somehow alrready exists!!");
            }
        } else if (msg.msgType == MessageType.BOTH) {
            synchronized (node) {
                node.keys.add(msg.key);
                node.pendingRequest.put(msg.id, msg);
            }
        }

    }

    public void listen() {
        try {
            this.server = new ServerSocket(port);
            System.out.println("[SERVER] Started @ port: " + port);

            while (true) {
                Socket client = server.accept();
                // Start a new thread to handle the client connection
                Thread listener = new Thread(() -> {
                    try {
                        InputStream clientInputStream = client.getInputStream();
                        DataInputStream dataInputStream = new DataInputStream(clientInputStream);

                        while (!client.isClosed()) {
                            try {
                                // Reading Incoming Message.
                                int length = dataInputStream.readInt();
                                byte[] buffer = new byte[length];
                                dataInputStream.readFully(buffer);
                                Message msg = Message.fromByteArray(buffer);
                                synchronized (node) {
                                    handleMessage(msg);
                                }
                            } catch (EOFException e) {
                                System.out.println("[SERVER] Connection closed by client");
                                break;
                            } catch (IOException | ClassNotFoundException e) {
                                e.printStackTrace();
                                break;
                            }

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                listener.start();
            }
        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        Thread server = new Thread(() -> {
            System.out.println("[SERVER] Starting...");
            try {
                node.server.listen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        server.start();
    }
}

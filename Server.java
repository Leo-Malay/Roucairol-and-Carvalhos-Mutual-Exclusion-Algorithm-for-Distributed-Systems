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

        if (msg.messageType == MessageType.REQUEST) {
           // Handle incoming Request for key.
        } else if (msg.messageType == MessageType.REPLY) {
            // Handle incoming Reply with key.
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

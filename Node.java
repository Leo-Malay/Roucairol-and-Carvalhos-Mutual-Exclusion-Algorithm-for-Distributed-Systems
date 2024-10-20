import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.*;

public class Node {
    // Node
    int id;
    String name;
    String port;
    List<List<Integer>> neighbours = new ArrayList<>();
    // Config
    int totalNodes;
    int requestDelay;
    int executionTime;
    int maxRequest;
    // Variable
    int requestSent = 0;
    boolean under_cs = false;
    Vector<Integer> keys = new Vector<>();
    Vector<Integer> clock = new Vector<>();

    // Components
    Server server;
    Client client;

    // Helper
    Map<String, List<Integer>> hostToId_PortMap = new HashMap<>();
    Map<Integer, List<String>> idToHost_PortMap = new HashMap<>();
    Map<Integer, Socket> idToChannelMap = new HashMap<>();

    public Node(int id) {
        this.id = id;
    }

    public static void main(String[] args) {
        // Init Node
        Node node;
        // if (args.length > 0)
        // node = new Node(Integer.parseInt(args[0]));
        // else
        node = new Node(-1);
        // Parse the config file
        node.readConfig();
        // Init Vector Clock;
        node.initVectorClock();
        // Print details
        node.printNodeConfig();
        node.printNodeNeighbours();

        // Server
        node.server = new Server(node.getPort(), node);
        node.server.init();
        try {
            System.out.println("[SERVER] Loading... Wait 10s");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Client
        node.client = new Client(node);
        try {
            System.out.println("[CLIENT] Loading... Wait 10s");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node.client.init();

    }

    public void readConfig() {
        // Declring Variables
        String CONFIG_FILE_NAME = "aos-project2/config.txt";
        String line;
        int configLine = 0;
        String localHost = "";
        // Get Host Name
        try {
            localHost = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println(e);
        }

        // REGEX Pattern
        Pattern REGEX_PATTERN_CONFIG = Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

        try {
            // Creating a reader for config file
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_NAME));
            // Looping over the whole file, reading line by line
            while ((line = reader.readLine()) != null) {
                // Ignoring comments and empty line
                line = line.split("#")[0].trim();
                if (line.isEmpty())
                    continue;

                // Match for config file.
                Matcher configMatcher = REGEX_PATTERN_CONFIG.matcher(line);
                if (configMatcher.matches()) {
                    this.totalNodes = Integer.parseInt(configMatcher.group(1));
                    this.requestDelay = Integer.parseInt(configMatcher.group(2));
                    this.executionTime = Integer.parseInt(configMatcher.group(3));
                    this.maxRequest = Integer.parseInt(configMatcher.group(4));
                } else if (configLine <= totalNodes) {

                    // All this lines are in format [ XXXX XXXX XXXX ]
                    String[] nodeConf = line.split(" ");
                    // Extracting data for read node.
                    int node_Id = Integer.parseInt(nodeConf[0]);
                    // String node_Host = nodeConf[1];
                    String node_Host = nodeConf[1] + ".utdallas.edu";
                    int node_Port = Integer.parseInt(nodeConf[2]);
                    if (this.id == -1 && node_Host.equals(localHost)) {
                        this.id = node_Id;
                    }
                    List<String> valueA = new ArrayList<>();
                    valueA.add(node_Host);
                    valueA.add(String.valueOf(node_Port));
                    List<Integer> valueB = new ArrayList<>();
                    valueB.add(node_Id);
                    valueB.add(node_Port);

                    this.idToHost_PortMap.put(node_Id, valueA);
                    this.hostToId_PortMap.put(node_Host, valueB);
                }
                configLine += 1;
            }
            // Closing reader
            reader.close();
        } catch (

        IOException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return idToHost_PortMap.get(id).get(0);
    }

    public String getHost(int id) {
        return idToHost_PortMap.get(id).get(0);
    }

    public int getPort() {
        return Integer.parseInt(idToHost_PortMap.get(id).get(1));
    }

    public int getPort(int id) {
        return Integer.parseInt(idToHost_PortMap.get(id).get(1));
    }

    public void initVectorClock() {
        for (int i = 0; i < totalNodes; i++) {
            this.clock.add(0);
        }
    }

    /**
     * This function is for Entering the critical section.
     */
    public void enterCS(){}

    /**
     * This function is for Leaving the critical section.
     */
    public void leaveCS(){}

    /* ========== HELPER FUNCTIONS ========== */
    public void printNodeConfig() {
        System.out.println("============ Node Config ============");
        System.out.println("Node Id:          " + id);
        System.out.println("Node Host:        " + getHost());
        System.out.println("Node Port:        " + getPort());
        System.out.println("Total Nodes:      " + totalNodes);
        System.out.println("Request Delay:    " + requestDelay);
        System.out.println("Execution Time:   " + executionTime);
        System.out.println("Max # of Request: " + maxRequest);
        System.out.println("=====================================\n");
    }

    public void printNodeVectorClock() {
        int totalSent = 0, totalReceive = 0;
        System.out.println("========= Node Vector Clock =========");
        for (int i = 0; i < totalNodes; i++) {
            System.out.println("NodeId: " + i + " | Msg: " + clock.get(i));
        }
        System.out.println("Total Send: " + totalSent + " | Total Recieve: " + totalReceive + " | Diff: "
                + (totalSent - totalReceive));
        System.out.println("=====================================\n");
    }

}
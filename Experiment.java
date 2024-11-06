import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Experiment {
    public long totalMessages = 0;
    public long startTime = 0;
    public long avgTime = 0;
    public long endTime = 0;
    public int n, d, e, c, nodeId;
    public String fileName;
    public ArrayList<long[]> data;

    public Experiment(int nodeId, int n, int d, int e, int c) {
        this.fileName = "aos-project2/" + nodeId + "-out.txt";
        this.nodeId = nodeId;
        this.n = n;
        this.d = d;
        this.e = e;
        this.c = c;
        this.data = new ArrayList<>();
    }

    public void recordStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void recordEnd() {
        this.endTime = System.currentTimeMillis();
        this.avgTime += (this.endTime - this.startTime);
        this.startTime = 0;
        this.endTime = 0;
    }

    public void write() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true))) {
            // nodeId, n, d, e, c, avg_time, total_msg
            writer.write(this.nodeId + ", " + this.n + ", " + this.d + ", " + this.e + ", " + this.c + ", "
                    + (this.avgTime / this.c)
                    + ", " + this.totalMessages + "\n");
            this.startTime = 0;
            this.endTime = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Experiment {
    public long totalMessages = 0;
    public long startTime = 0;
    public long avgTime = 0;
    public long endTime = 0;
    public String fileName;
    public ArrayList<long[]> data;

    public Experiment(int nodeId) {
        fileName = nodeId + "-out.txt";
        data = new ArrayList<>();
    }

    public void recordStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void recordEnd() {
        this.endTime = System.currentTimeMillis();
        this.avgTime += (this.endTime - this.startTime)
        this.startTime = 0;
        this.endTime = 0;
    }

    public void write(int maxRequest) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true))) {
            writer.write("Avg Time:" + (this.avgTime / maxRequest) + " | Total Msg: " + this.totalMessages + "\n");
            this.startTime = 0;
            this.endTime = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

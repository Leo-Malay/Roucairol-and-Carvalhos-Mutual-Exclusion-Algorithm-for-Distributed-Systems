import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Experiment {
    public long totalMessages = 0;
    public long startTime = 0;
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
        this.data.add(new long[] { this.startTime, this.endTime, (this.endTime - this.startTime), this.totalMessages });
        this.totalMessages = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void write() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true))) {
            for (int i = 0; i < data.size(); i++) {
                writer.write(
                        data.get(i)[0] + " " + data.get(i)[1] + " " + data.get(i)[2] + " " + data.get(i)[3] + "\n");
            }
            this.totalMessages = 0;
            this.startTime = 0;
            this.endTime = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

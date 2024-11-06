import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {
    public int totalMessages = 0;
    public long startTime = 0;
    public long endTime = 0;
    public String fileName;

    public Experiment(int nodeId) {
        fileName = nodeId + "-out.txt";
    }

    public void recordStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void recordEnd() {
        this.endTime = System.currentTimeMillis();
    }

    public void write(int csCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fileName, true))) {
            writer.write(csCount + " " + this.startTime + " " + this.endTime + " " + (this.endTime - this.startTime)
                    + " " + this.totalMessages + "\n");
            this.totalMessages = 0;
            this.startTime = 0;
            this.endTime = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

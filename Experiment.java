import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Experiment {
    public int totalMessages = 0;
    public long startTime = 0;
    public long endTime = 0;
    private BufferedWriter writer;

    public Experiment(int nodeId) {
        try {
            writer = new BufferedWriter(new FileWriter("aos-project2/" + nodeId + "-out.txt", false));
        } catch (Exception e) {
            System.out.println("[EXPERIMENT]: Something went wrong in opening up");
        }
    }

    public void recordStart() {
        this.startTime = System.currentTimeMillis();
    }

    public void recordEnd() {
        this.endTime = System.currentTimeMillis();
    }

    public void write(int csCount) {
        try {
            writer.write(csCount + " " + this.startTime + " " + this.endTime + " " + (this.endTime - this.startTime)
                    + " " + this.totalMessages + "\n");
            this.totalMessages = 0;
            this.startTime = 0;
            this.endTime = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        if (this.totalMessages != 0) {
            write(-1);
        }

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

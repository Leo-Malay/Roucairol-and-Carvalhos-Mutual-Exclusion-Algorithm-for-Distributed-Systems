import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Tester {

    public static void main(String[] args) {
        String filename = "./aos-2.txt";
        test(filename);
    }

    public static void test(String filename) {
        try (BufferedReader fileBuffer = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean flag = false;
            Double prevValue = null;

            while ((line = fileBuffer.readLine()) != null) {
                String[] parts = line.split("\\s+");

                // Check if the line has at least three parts
                if (parts.length < 3) {
                    System.out.println("[ERROR]: Line Invalid");
                    continue;
                }

                try {
                    // Parse the second value (Y)
                    double currValue = Double.parseDouble(parts[1]);

                    if (prevValue != null) {
                        if (currValue <= prevValue) {
                            flag = true;
                            System.out.println("[ERROR]: Mutual Exclusion doesn't hold");
                        }
                    }
                    prevValue = currValue;

                } catch (NumberFormatException e) {
                    System.out.println("[ERROR]: Line Invalid");
                }
            }

            if (flag == false)
                System.out.println("[SUCCESS]: Mutual Exclusion Holds :)");

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}

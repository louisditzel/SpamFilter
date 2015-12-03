import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVWriter {

    public static void writeCsvFile(String fileName, HashMap<String, Float> vocabList, int numHamFiles,
                                    int numSpamFiles) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);

            fileWriter.append("TRAIN HAM\n");
            fileWriter.append(String.valueOf(numHamFiles));
            fileWriter.append("\n");


            fileWriter.append("TRAIN SPAM\n");

            fileWriter.append(String.valueOf(numSpamFiles));
            fileWriter.append("\n");


            fileWriter.append("TRAIN VOCAB\n");
            for (Map.Entry<String, Float> entry : vocabList.entrySet()) {
                fileWriter.append(entry.getKey() + " " + entry.getValue());
                fileWriter.append("\n");
            }

            fileWriter.append("END OF BANTER\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}

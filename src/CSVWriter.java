import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by louis on 10/12/2015.
 */
public class CSVWriter {

    public static void writeCSVFile(String fileName, ArrayList<Double> hamProbabilities,
                                    ArrayList<Double> spamProbabilities) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);

            fileWriter.append(String.valueOf(hamProbabilities));
            fileWriter.append("\n");
            fileWriter.append(String.valueOf(spamProbabilities));

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

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVWriter {

    public static void writeCsvFile(String fileName, HashMap<String, Integer> hamHashMap, int trainHamDataTotal, HashMap<String, Integer> spamHashMap, int trainSpamDataTotal, ArrayList<String> vocabList) {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fileName);

            Iterator it = hamHashMap.entrySet().iterator();

            fileWriter.append("TRAIN HAM\n");
            fileWriter.append(String.valueOf(trainHamDataTotal));
            fileWriter.append("\n");

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                fileWriter.append(pair.getKey() + " " + pair.getValue());
                fileWriter.append("\n");
                it.remove();
            }

            fileWriter.append("TRAIN SPAM\n");
            fileWriter.append(String.valueOf(trainSpamDataTotal));
            fileWriter.append("\n");

            it = spamHashMap.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                fileWriter.append(pair.getKey() + " " + pair.getValue());
                fileWriter.append("\n");
                it.remove();
            }

            fileWriter.append("TRAIN VOCAB\n");
            for (String word : vocabList) {
                fileWriter.append(word);
                fileWriter.append("\n");
            }

            fileWriter.append("END OF BANTER\n");

            System.out.println("CSV file was created successfully !!!");

        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }

        }
    }

}

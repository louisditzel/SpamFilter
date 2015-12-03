import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader {

    private static CSVReader instance = null;
    private HashMap<String, Float> trainVocabulary;
    private int  numHamFiles, numSpamFiles;

    protected CSVReader() {
        trainVocabulary = new HashMap<>();
        numHamFiles = 0;
        numSpamFiles = 0;
    }

    public static CSVReader getInstance() {
        if (instance == null)
            instance = new CSVReader();
        return instance;
    }

    public void readCSVFile(String fileName){
        BufferedReader fileReader = null;

        try {
            fileReader = new BufferedReader(new FileReader(fileName));
            String line = fileReader.readLine();

            if (!line.equals("TRAIN HAM")) {
                throw new IOException("File header incorrect\n");
            }

            line = fileReader.readLine();
            numHamFiles = Integer.valueOf(line);

            line = fileReader.readLine();

            if (!line.equals("TRAIN SPAM")) {
                throw new IOException("File header incorrect\n");
            }

            line = fileReader.readLine();
            numSpamFiles = Integer.valueOf(line);

            line = fileReader.readLine();

            if (!line.equals("TRAIN VOCAB")) {
                throw new IOException("File header incorrect\n");
            }

            //fill vocab list
            line = fileReader.readLine();
            while (!line.equals("END OF BANTER")) {
                String[] pair = line.split(" ");
                if (pair.length > 0) {
                    trainVocabulary.put(pair[0], Float.valueOf(pair[1]));
                }
                line = fileReader.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public HashMap<String, Float> getVocabList(){
        return trainVocabulary;
    }

    public int getNumHamFiles() {
        return numHamFiles;
    }

    public int getNumSpamFiles() {
        return numSpamFiles;
    }
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVReader {

    private static CSVReader instance = null;
    private HashMap<String, Integer> trainHamData, trainSpamData;
    private HashMap<String, Float> trainVocabulary;
    private int trainHamDataTotal, trainSpamDataTotal, numHamFiles, numSpamFiles;

    protected CSVReader() {
        trainHamData = new HashMap<>();
        trainSpamData = new HashMap<>();
        trainVocabulary = new HashMap<>();
        trainHamDataTotal = 0;
        trainSpamDataTotal = 0;
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
            trainHamDataTotal = Integer.valueOf(line);

            line = fileReader.readLine();
            numHamFiles = Integer.valueOf(line);

            line = fileReader.readLine();
            //Fill ham hashmap
            while (!line.equals("TRAIN SPAM")) {
                String[] pair = line.split(" ");
                if (pair.length > 0) {
                    trainHamData.put(pair[0], Integer.valueOf(pair[1]));
                }
                line = fileReader.readLine();
            }

            line = fileReader.readLine();
            trainSpamDataTotal = Integer.valueOf(line);

            line = fileReader.readLine();
            numSpamFiles = Integer.valueOf(line);

            line = fileReader.readLine();
            //fill spam hashmap
            while (!line.equals("TRAIN VOCAB")) {
                String[] pair = line.split(" ");
                if (pair.length > 0) {
                    trainSpamData.put(pair[0], Integer.valueOf(pair[1]));
                }
                line = fileReader.readLine();
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

    public HashMap<String, Integer> getSpamHash(){
        return trainSpamData;
    }

    public HashMap<String, Integer> getHamHash(){
        return trainHamData;
    }

    public HashMap<String, Float> getVocabList(){
        return trainVocabulary;
    }

    public int getTrainHamDataTotal(){
        return trainHamDataTotal;
    }

    public int getTrainSpamDataTotal(){
        return trainSpamDataTotal;
    }

    public int getNumHamFiles() {
        return numHamFiles;
    }

    public int getNumSpamFiles() {
        return numSpamFiles;
    }
}

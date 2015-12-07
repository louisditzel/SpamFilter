import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BanterReader {

    private static BanterReader instance = null;
    private HashMap<String, Double> trainVocabulary;
    private int  numHamFiles, numSpamFiles;

    protected BanterReader() {
        trainVocabulary = new HashMap<>();
        numHamFiles = 0;
        numSpamFiles = 0;
    }

    public static BanterReader getInstance() {
        if (instance == null)
            instance = new BanterReader();
        return instance;
    }

    public void readBanterFile(String fileName){
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
                    trainVocabulary.put(pair[0], Double.valueOf(pair[1]));
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

    public HashMap<String, Double> getVocabList(){
        return trainVocabulary;
    }

    public int getNumHamFiles() {
        return numHamFiles;
    }

    public int getNumSpamFiles() {
        return numSpamFiles;
    }
}

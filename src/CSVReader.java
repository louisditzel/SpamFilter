import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Oscar on 05/12/2015.
 */
public class CSVReader {
    private static CSVReader instance = null;
    private ArrayList<String> stopList;

    protected CSVReader() {
        stopList = new ArrayList<>();
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
            String[] words = line.split(", ");
            stopList = new ArrayList<>(Arrays.asList(words));
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

    public ArrayList<String> getStopList(){
        return stopList;
    }
}

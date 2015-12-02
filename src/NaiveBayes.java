import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by louis on 23/11/2015.
 */
public class NaiveBayes {

    public enum Class {Ham, Spam}

    private static NaiveBayes instance = null;
    private HashMap<String, Integer> trainHamData, trainSpamData, testWordData;
    private ArrayList<String> trainVocabulary;
    private int trainHamDataTotal, trainSpamDataTotal, numHamFiles, numSpamFiles;
    private double priorHam, priorSpam;

    protected NaiveBayes() {
        trainHamData = new HashMap<>();
        trainSpamData = new HashMap<>();
        testWordData = new HashMap<>();
        trainVocabulary = new ArrayList<>();
        trainHamDataTotal = 0;
        trainSpamDataTotal = 0;
        numHamFiles = 0;
        numSpamFiles = 0;
        priorHam = 0;
        priorSpam = 0;
    }

    public static NaiveBayes getInstance() {
        if (instance == null)
            instance = new NaiveBayes();
        return instance;
    }

    public void clearInstance() {
        trainHamData = new HashMap<>();
        trainSpamData = new HashMap<>();
        testWordData = new HashMap<>();
        trainVocabulary = new ArrayList<>();
        trainHamDataTotal = 0;
        trainSpamDataTotal = 0;
        numHamFiles = 0;
        numSpamFiles = 0;
        priorHam = 0;
        priorSpam = 0;
    }

    public void clearInstanceForTest() {
        testWordData = new HashMap<>();
    }

    public void train(File[] trainFiles) throws IOException {
        for (File trainFile : trainFiles) {
            if(trainFile.getName().startsWith("ham")) {
                numHamFiles++;
                addWordsFromFile(trainFile, trainHamData, true,  Class.Ham);
            } else if (trainFile.getName().startsWith("spam")) {
                numSpamFiles++;
                addWordsFromFile(trainFile, trainSpamData, true, Class.Spam);
            } else if (trainFile.getName().equals(".DS_Store")) {
            } else
                throw new IOException("Training filename does not start with either ham or spam");
        }
        setClassPriors(numHamFiles, numSpamFiles);

    }

    public void getDataFromCSV(String fileName) throws IOException{
        CSVReader reader = CSVReader.getInstance();
        reader.readCSVFile(fileName);
        trainHamData = reader.getHamHash();
        trainSpamData = reader.getSpamHash();
        trainVocabulary = reader.getVocabList();
        trainHamDataTotal = reader.getTrainHamDataTotal();
        trainSpamDataTotal = reader.getTrainSpamDataTotal();
        numHamFiles = reader.getNumHamFiles();
        numSpamFiles = reader.getNumSpamFiles();
    }

    public String test(File testFile) {
        addWordsFromFile(testFile, testWordData, false, null);
        setClassPriors(numHamFiles, numSpamFiles);

        double probability = 1;

        for (String word : trainVocabulary) {
            probability *= wordLikelihoodRatio(getProbabilityOfWordGivenClass(word, Class.Ham),
                    getProbabilityOfWordGivenClass(word, Class.Spam),
                    testWordData.getOrDefault(word, 0));
        }

        probability *= priorHam / priorSpam;

        return probability >= 1 ? "ham\n" : "spam\n";
    }

    public void printTrainingData() {
        System.out.println("Ham: ");
        for (Map.Entry<String, Integer> entry : trainHamData.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("\nSpam: ");
        for (Map.Entry<String, Integer> entry : trainSpamData.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("\nTest: ");
        for (Map.Entry<String, Integer> entry : testWordData.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public HashMap<String, Integer> getSpamHash(){
        return trainSpamData;
    }

    public HashMap<String, Integer> getHamHash(){
        return trainHamData;
    }

    public ArrayList<String> getVocabList(){
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

    private void setClassPriors(int numHam, int numSpam) {
        int total = numHam + numSpam;
        priorHam = (double) numHam / (double) total;
        priorSpam = (double) numSpam / (double) total;
    }

    private void addWordsFromFile(File trainFile, HashMap<String, Integer> data, boolean train, Class cl) {
        Scanner sc = null;
        try {
            sc = new Scanner(trainFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String[] words = sc.nextLine().trim().split(" ");
            for (String word : words) {
                if (word.isEmpty())
                    break;
                if (!data.containsKey(word))
                    data.put(word, 1);
                else
                    data.replace(word, data.get(word) + 1);
                if (train) {
                    if (cl == Class.Ham) trainHamDataTotal++;
                    else trainSpamDataTotal++;
                    if (!trainVocabulary.contains(word))
                        trainVocabulary.add(word);
                }
            }
        }
    }

    private double getProbabilityOfWordGivenClass(String word, Class cl) {
        double probability = 0;
        if (cl == Class.Ham) {
            probability = (double) (trainHamData.getOrDefault(word, 0) + 1) / (double) (trainHamDataTotal + trainVocabulary.size());
        } else {
            probability = (double) (trainSpamData.getOrDefault(word, 0) + 1) / (double) (trainSpamDataTotal + trainVocabulary.size());
        }
        return probability;
    }

    private double wordLikelihoodRatio (double hamProbability, double spamProbability, int wordCount) {
        return Math.pow((hamProbability/spamProbability), wordCount);
    }
}

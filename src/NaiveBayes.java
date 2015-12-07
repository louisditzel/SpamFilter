import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by louis on 23/11/2015.
 */
public class NaiveBayes {

    public enum Class {Ham, Spam}

    private static NaiveBayes instance = null;
    private HashMap<String, Integer> trainHamData, trainSpamData, testWordData;
    private HashMap<String, Float> trainVocabulary;
    private int trainHamDataTotal, trainSpamDataTotal, numHamFiles, numSpamFiles;
    private float priorHam, priorSpam;

    protected NaiveBayes() {
        trainHamData = new HashMap<>();
        trainSpamData = new HashMap<>();
        testWordData = new HashMap<>();
        trainVocabulary = new HashMap<>();
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
        trainHamData.clear();
        trainSpamData.clear();
        testWordData.clear();
        trainVocabulary.clear();
        trainHamDataTotal = 0;
        trainSpamDataTotal = 0;
        numHamFiles = 0;
        numSpamFiles = 0;
        priorHam = 0;
        priorSpam = 0;
    }

    public void clearInstanceForTest() {
        testWordData.clear();
    }

    public void train(DirectoryStream<Path> trainFiles) throws IOException {
        for (Path trainFile : trainFiles) {
            if(trainFile.getFileName().toString().startsWith("ham")) {
                numHamFiles++;
                addWordsFromFile(trainFile, trainHamData, true,  Class.Ham);
            } else if (trainFile.getFileName().toString().startsWith("spam")) {
                numSpamFiles++;
                addWordsFromFile(trainFile, trainSpamData, true, Class.Spam);
            } else if (trainFile.getFileName().toString().equals(".DS_Store")) {
            } else
                throw new IOException("Training filename (" + trainFile.getFileName() +
                        ") does not start with either ham or spam");
        }
        setClassPriors(numHamFiles, numSpamFiles);


        for (Map.Entry<String, Float> entry : trainVocabulary.entrySet()) {
            entry.setValue(wordLikelihoodRatio(getProbabilityOfWordGivenClass(entry.getKey(), Class.Ham),
                    getProbabilityOfWordGivenClass(entry.getKey(), Class.Spam)));
        }
    }

    public void getDataFromBanter(String fileName) throws IOException{
        BanterReader reader = BanterReader.getInstance();
        reader.readBanterFile(fileName);
//        trainHamData = reader.getHamHash();
//        trainSpamData = reader.getSpamHash();
        trainVocabulary = reader.getVocabList();
//        trainHamDataTotal = reader.getTrainHamDataTotal();
//        trainSpamDataTotal = reader.getTrainSpamDataTotal();
        numHamFiles = reader.getNumHamFiles();
        numSpamFiles = reader.getNumSpamFiles();
    }

    public String test(Path testFile) {
        addWordsFromFile(testFile, testWordData, false, null);
        setClassPriors(numHamFiles, numSpamFiles);

        double probability = 1;

        for (Map.Entry<String, Float> entry : trainVocabulary.entrySet()) {
            probability *= Math.pow((double) entry.getValue(), testWordData.getOrDefault(entry.getKey(), 0));
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

    private void setClassPriors(int numHam, int numSpam) {
        int total = numHam + numSpam;
        priorHam = (float) numHam / (float) total;
        priorSpam = (float) numSpam / (float) total;
    }

    private void addWordsFromFile(Path trainFile, HashMap<String, Integer> data, boolean train, Class cl) {

        String fileContents = "";
        try {
            fileContents = new String(Files.readAllBytes(trainFile), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CSVReader stopWordReader = CSVReader.getInstance();
        stopWordReader.readCSVFile("stopwords.csv");
        String[] words = preProcess(fileContents, stopWordReader.getStopList());
        words = stemWordList(words);
        for (String word : words) {
            word = word.toLowerCase();
            if (word.isEmpty())
                break;
            if (!data.containsKey(word))
                data.put(word, 1);
            else
                data.replace(word, data.get(word) + 1);
            if (train) {
                if (cl == Class.Ham) trainHamDataTotal++;
                else trainSpamDataTotal++;
                if (!trainVocabulary.containsKey(word))
                    trainVocabulary.put(word, 0f);
            }
        }
    }

    private float getProbabilityOfWordGivenClass(String word, Class cl) {
        float probability;
        if (cl == Class.Ham) {
            probability = (float) (trainHamData.getOrDefault(word, 0) + 1) / (float) (trainHamDataTotal + trainVocabulary.size());
        } else {
            probability = (float) (trainSpamData.getOrDefault(word, 0) + 1) / (float) (trainSpamDataTotal + trainVocabulary.size());
        }
        return probability;
    }

    private float wordLikelihoodRatio (float hamProbability, float spamProbability) {
        return (hamProbability/spamProbability);
    }

    private String[] preProcess(String fileContents, ArrayList<String> stopWordList) {
        fileContents = fileContents.replaceAll("Content-Disposition: attachment;[.]*------=_NextPart", "");
        String[] trimmedWords = fileContents.trim().split("[^a-zA-Z0-9_\\-'!$\\.]+");

        ArrayList<String> words = new ArrayList<>();
        for (String word : trimmedWords){
            if (word.matches("(\\d)*") || (word.length() < 2 && !word.matches("(a-zA-Z)*"))){
                continue;
            }
            if (!stopWordList.contains(word.toLowerCase())){
                words.add(word);
            }
        }
        return words.toArray(new String[words.size()]);
    }

    private String[] stemWordList(String[] wordList) {
        Stemmer s = new Stemmer();

        for (int i=0; i<wordList.length; i++){
            String word = wordList[i];
            char[] charList = word.toCharArray();

            boolean properWord = true;

            for (char ch : charList){
                if (!Character.isLetter(ch)) {
                    properWord = false;
                    break;
                }
                ch = Character.toLowerCase(ch);
                s.add(ch);
            }

            // stem first to clear the buffer in s even though we may not use it
            s.stem();
            if (!properWord) continue;
            String u = s.toString();
            wordList[i] = u;
        }

        return wordList;
    }
}

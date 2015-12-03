import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by louis on 02/12/2015.
 */
public class crossValidate {

    String inputDir = System.getProperty("user.dir") + "/train";
    ArrayList<File>[] files = new ArrayList[10];
    int totNumFiles = 0;

    @Test
    public void validate() {
        for(int i = 0; i < 10; i ++) {
            files[i] = new ArrayList<>();
        }
        setUpFiles();
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        int sumHam = 0;
        int sumSpam = 0;
        for (int i = 0; i < 10; i++) {
            naiveBayes.clearInstance();
            System.out.println("Index = " + i);
            trainFiles(i, naiveBayes);
            naiveBayes.clearInstance();
            int[] numIncorrect = testFiles(i, naiveBayes);
            System.out.println("Number incorrect ham = " + numIncorrect[0]);
            System.out.println("Number incorrect spam = " + numIncorrect[1]);
            sumHam += numIncorrect[0];
            sumSpam += numIncorrect[1];
        }
        double averageHam = (sumHam - 1) / 10.0;
        double averageSpam = sumSpam / 10.0;
        System.out.println("Total number files = " + (totNumFiles - 1));
        System.out.println("Total number incorrect ham = " + (sumHam - 1));
        System.out.println("Total number incorrect spam = " + sumSpam);
        System.out.println("Average number incorrect ham per iteration = " + averageHam);
        System.out.println("Average number incorrect spam per iteration = " + averageSpam);
    }

    private void setUpFiles() {
        File filesDirectory = new File(inputDir);
        File[] allFiles = filesDirectory.listFiles();
        totNumFiles = allFiles.length;
        for (int i = 0; i < totNumFiles; i++) {
            files[i % 10].add(allFiles[i]);
        }
    }

    private void trainFiles(int index, NaiveBayes naiveBayes) {
        System.out.println("Training files...");
        ArrayList<File> trainFiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i != index)
                trainFiles.addAll(files[i]);
        }
        File trainFilesArray[] = new File[trainFiles.size()];
        trainFilesArray = trainFiles.toArray(trainFilesArray);
        try {
            naiveBayes.train(trainFilesArray);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter.writeCsvFile("outputFile_crossvalid.banter", naiveBayes.getHamHash(), naiveBayes.getTrainHamDataTotal(),
                naiveBayes.getSpamHash(), naiveBayes.getTrainSpamDataTotal(), naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

    }

    private int[] testFiles(int index, NaiveBayes naiveBayes) {
        int numIncorrect[] = new int[2];
        numIncorrect[0] = 0;
        numIncorrect[1] = 0;
        System.out.println("Testing files...");
        ArrayList<File> testFiles = files[index];
        try {
            naiveBayes.getDataFromCSV("outputFile_crossvalid.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file : testFiles) {
            naiveBayes.clearInstanceForTest();
            String out = naiveBayes.test(file);
//            System.out.println(file.getName() + ": " + out);
            if (!file.getName().contains(out.trim()))
                switch (out.trim()) {
                    case "ham":
                        numIncorrect[1]++;
                        break;
                    case "spam":
                        numIncorrect[0]++;
                        break;
                    default:
                        System.out.println("Error: Not ham or spam");
                }
        }

        return numIncorrect;
    }

}

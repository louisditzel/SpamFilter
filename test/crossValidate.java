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

    @Test
    public void validate() {
        for(int i = 0; i < 10; i ++) {
            files[i] = new ArrayList<>();
        }
        setUpFiles();
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        for (int i = 0; i < 10; i++) {
            naiveBayes.clearInstance();
            System.out.println("Index = " + i);
            trainFiles(i, naiveBayes);
            naiveBayes.clearInstance();
            testFiles(i, naiveBayes);
        }
    }

    private void setUpFiles() {
        File filesDirectory = new File(inputDir);
        File[] allFiles = filesDirectory.listFiles();
        for (int i = 0; i < allFiles.length; i++) {
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

    private void testFiles(int index, NaiveBayes naiveBayes) {
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
            System.out.println(file.getName() + ": " + out);
        }
    }

}

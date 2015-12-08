import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by louis on 24/11/2015.
 */
public class testFilter {

    String inputDirSuccess = System.getProperty("user.dir") + "/sample2/train4";
    String inputFileSuccess = System.getProperty("user.dir") + "/sample2/test4/1.txt";
    String inputTestDirSuccess = System.getProperty("user.dir") + "/sample2/test4/";
    String inputFileFail = "";

    @Test
    public void testFiles() {
        Path trainDirectorySuccess = Paths.get(inputDirSuccess);
        Assert.assertTrue(Files.exists(trainDirectorySuccess));

        Path testFileSuccess = Paths.get(inputFileSuccess);
        Assert.assertTrue(Files.exists(testFileSuccess));

        Assert.assertFalse(Files.isDirectory(testFileSuccess));
    }

    @Test
    public void testSingleton() {
        NaiveBayes one = NaiveBayes.getInstance();
        NaiveBayes two = NaiveBayes.getInstance();

        Assert.assertEquals(true, one == two);
    }

    @Test
    public void testTrainingDataCreation() {
        Path trainDirectory = Paths.get(inputDirSuccess);
        NaiveBayes naiveBayes = NaiveBayes.getInstance();
        naiveBayes.clearInstance();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(trainDirectory)) {
            try {
                naiveBayes.train(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        HashMap<String, Integer> testTrainHamData = new HashMap<>();
        HashMap<String, Integer> testTrainSpamData = new HashMap<>();
        HashMap<String, Integer> trainHamData = naiveBayes.getHamHash();
        HashMap<String, Integer> trainSpamData = naiveBayes.getSpamHash();

        testTrainHamData.put("clai", 1);
        testTrainHamData.put("bob", 1);
        testTrainHamData.put("rolex", 1);
        testTrainHamData.put("alic", 1);
        testTrainHamData.put("elisabeth", 1);
        testTrainHamData.put("david", 1);

        testTrainSpamData.put("rolex", 5);

        Assert.assertTrue(testTrainHamData.size() == trainHamData.size());
        Assert.assertTrue(testTrainSpamData.size() == trainSpamData.size());

        for (Map.Entry<String, Integer> entry : testTrainHamData.entrySet()) {
            Assert.assertTrue(trainHamData.entrySet().contains(entry));
        }

        for (Map.Entry<String, Integer> entry : testTrainSpamData.entrySet()) {
            Assert.assertTrue(trainSpamData.entrySet().contains(entry));
        }

    }

    @Test
    public void testCSVCreation() {
        Path trainDirectory = Paths.get(inputDirSuccess);
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(trainDirectory)) {
            try {
                naiveBayes.train(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        HashMap<String, Float> trainVocabData = (HashMap<String, Float>) naiveBayes.getVocabList().clone();

        BanterWriter.writeBanterFile("outputFile_junit.banter",  naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

        naiveBayes.clearInstance();

        try {
            naiveBayes.getDataFromBanter("outputFile_junit.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, Double> csvVocabData = naiveBayes.getVocabList();


        Assert.assertTrue(trainVocabData.size() == csvVocabData.size());

        for (Map.Entry<String, Double> entry : csvVocabData.entrySet()) {
            Assert.assertTrue(csvVocabData.entrySet().contains(entry));
        }


    }

    @Test
    public void testTestOutput() {
        Path trainDirectory = Paths.get(inputDirSuccess);
        Path test1 = Paths.get(inputTestDirSuccess + "1.txt");
        Path test2 = Paths.get(inputTestDirSuccess + "2.txt");
        Path test3 = Paths.get(inputTestDirSuccess + "3.txt");
        NaiveBayes naiveBayes = NaiveBayes.getInstance();
        naiveBayes.clearInstance();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(trainDirectory)) {
            try {
                naiveBayes.train(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        BanterWriter.writeBanterFile("outputFile_junit.banter",  naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

        naiveBayes.clearInstance();


        try {
            naiveBayes.getDataFromBanter("outputFile_junit.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String expectedSpam = "spam\n";
        String expectedHam = "ham\n";
        String out = naiveBayes.test(test1);
        Assert.assertEquals(expectedSpam, out);
        naiveBayes.clearInstanceForTest();
        out = naiveBayes.test(test2);
        Assert.assertEquals(expectedHam, out);
        naiveBayes.clearInstanceForTest();
        out = naiveBayes.test(test3);
        Assert.assertEquals(expectedSpam, out);
    }
}

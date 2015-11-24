import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by louis on 24/11/2015.
 */
public class testFilter {

    String inputDirSuccess = "/Users/louis/Documents/Uni/Year3/ML/Spam/sample2/train4";
    String inputFileSuccess = "/Users/louis/Documents/Uni/Year3/ML/Spam/sample2/test4/1.txt";
    String inputFileFail = "";

    @Test
    public void testFiles() {
        File trainDirectorySuccess = new File(inputDirSuccess);
        Assert.assertTrue(trainDirectorySuccess.exists());

        File testFileSuccess = new File(inputFileSuccess);
        Assert.assertTrue(testFileSuccess.exists());

        Assert.assertFalse(testFileSuccess.isDirectory());

        File fileFail = new File(inputFileFail);
        Assert.assertFalse(fileFail.exists());
    }

    @Test
    public void testSingleton() {
        NaiveBayes one = NaiveBayes.getInstance();
        NaiveBayes two = NaiveBayes.getInstance();

        Assert.assertEquals(true, one == two);
    }

    @Test
    public void testTrainingDataCreation() {
        File trainDirectory = new File(inputDirSuccess);
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        try {
            naiveBayes.train(trainDirectory.listFiles());
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, Integer> testTrainHamData = new HashMap<>();
        HashMap<String, Integer> testTrainSpamData = new HashMap<>();
        HashMap<String, Integer> trainHamData = new HashMap<>(); //naiveBayes.getTrainHamData();
        HashMap<String, Integer> trainSpamData = new HashMap<>(); //naiveBayes.getTrainSpamData();

        testTrainHamData.put("Clay", 1);
        testTrainHamData.put("Bob", 1);
        testTrainHamData.put("Rolex", 1);
        testTrainHamData.put("Alice", 1);
        testTrainHamData.put("Elisabeth", 1);
        testTrainHamData.put("David", 1);

        testTrainSpamData.put("Rolex", 5);

        Assert.assertTrue(testTrainHamData.size() == trainHamData.size());
        Assert.assertTrue(testTrainSpamData.size() == trainSpamData.size());

        for (Map.Entry<String, Integer> entry : testTrainHamData.entrySet()) {
            Assert.assertTrue(trainHamData.entrySet().contains(entry));
        }

        for (Map.Entry<String, Integer> entry : testTrainSpamData.entrySet()) {
            Assert.assertTrue(trainSpamData.entrySet().contains(entry));
        }

    }
}

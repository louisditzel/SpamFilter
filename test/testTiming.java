import org.junit.Assert;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.IOException;

/**
 * Created by louis on 03/12/2015.
 */
public class testTiming {

    String inputDir = System.getProperty("user.dir") + "/train";
    String inputFile = System.getProperty("user.dir")+ "/sampletest/sampletest/1.txt";

    @Test
    public void testTraining() {
        final long startTimeTrain = System.currentTimeMillis();
        File trainDirectory = new File(inputDir);
        NaiveBayes naiveBayes = NaiveBayes.getInstance();
        naiveBayes.clearInstance();

        try {
            naiveBayes.train(trainDirectory.listFiles());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter.writeCsvFile("outputFile_timed.banter", naiveBayes.getHamHash(), naiveBayes.getTrainHamDataTotal(),
                naiveBayes.getSpamHash(), naiveBayes.getTrainSpamDataTotal(), naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

        final long endTimeTrain = System.currentTimeMillis();

        naiveBayes.clearInstance();

        System.out.println("Total training execution time: " + ((endTimeTrain - startTimeTrain)/1000.0) + "s");

        final long startTimeTest = System.currentTimeMillis();

        File test = new File(inputFile);

        try {
            naiveBayes.getDataFromCSV("outputFile_timed.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String expected = "ham\n";
        String out = naiveBayes.test(test);
        Assert.assertEquals(expected, out);

        final long endTimeTest = System.currentTimeMillis();

        System.out.println("Total testing execution time: " + ((endTimeTest - startTimeTest)/1000.0) + "s");
    }


}

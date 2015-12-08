import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;

/**
 * Created by louis on 03/12/2015.
 */
public class testTiming {

    String inputDir = System.getProperty("user.dir") + "/train";
    String inputFile = System.getProperty("user.dir")+ "/sampletest/sampletest/1.txt";

    @Test
    public void testTraining() {
        final long startTimeTrain = System.currentTimeMillis();
        Path trainDirectory = Paths.get(inputDir);
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

        BanterWriter.writeBanterFile("outputFile_timed.banter",  naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

        final long endTimeTrain = System.currentTimeMillis();

        naiveBayes.clearInstance();

        System.out.println("Total training execution time: " + ((endTimeTrain - startTimeTrain)/1000.0) + "s");

        final long startTimeTest = System.currentTimeMillis();

        Path test = Paths.get(inputFile);

        try {
            naiveBayes.getDataFromBanter("outputFile_timed.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String expected = "ham\n";
        String out = naiveBayes.test(test);

        final long endTimeTest = System.currentTimeMillis();

        System.out.println("Total testing execution time: " + ((endTimeTest - startTimeTest)/1000.0) + "s");

        Assert.assertEquals(expected, out);

    }


}

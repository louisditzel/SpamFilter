import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Created by louis on 03/12/2015.
 */
public class testTiming {

    String inputDir = System.getProperty("user.dir") + "/train";
    String inputFile = System.getProperty("user.dir")+ "/sampletest/sampletest/1.txt";

    @Test
    public void time() {
        testTraining();
        Path testDirectory = Paths.get(inputDir);
        ArrayList<Double> hamProbabilities = new ArrayList<>();
        ArrayList<Double> spamProbabilities = new ArrayList<>();
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        final long startTimeTest = System.currentTimeMillis();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testDirectory)) {
            for (Path test : stream) {
                double prob = testTesting(test);
                if (test.getFileName().toString().contains("ham"))
                    hamProbabilities.add(prob);
                else
                    spamProbabilities.add(prob);
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        final long endTimeTest = System.currentTimeMillis();

        System.out.println("Total testing execution time: " + ((endTimeTest - startTimeTest)/1000.0) + "s");

        CSVWriter.writeCSVFile("wordprobs.csv", hamProbabilities, spamProbabilities);
    }

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

        BanterWriter.writeBanterFile("outputFile_timed.banter", naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

        final long endTimeTrain = System.currentTimeMillis();

        System.out.println("Total training execution time: " + ((endTimeTrain - startTimeTrain)/1000.0) + "s");

    }

    public double testTesting(Path inputFile) {
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        naiveBayes.clearInstance();

        try {
            naiveBayes.getDataFromBanter("outputFile_timed.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        naiveBayes.test(inputFile);
        return naiveBayes.getTestProbability();

    }


}

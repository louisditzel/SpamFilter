import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class filter {

    public static void main(String[] args) {

        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        switch (args[0]) {
            case "-train":
                File trainDirectory = new File(args[1]);
                train(trainDirectory, naiveBayes);
                break;
            case "-test":
                File testFile = new File(args[1]);
                test(testFile, naiveBayes);
                break;
            default:
                testFile = new File(args[0]);
                test(testFile, naiveBayes);
                break;
        }
    }

    public static void train(File trainDirectory, NaiveBayes naiveBayes) {
        try {
            naiveBayes.train(trainDirectory.listFiles());
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter.writeCsvFile("outputFile.banter", naiveBayes.getHamHash(), naiveBayes.getTrainHamDataTotal(),
                naiveBayes.getSpamHash(), naiveBayes.getTrainSpamDataTotal(), naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());
    }

    public static void test(File testFile, NaiveBayes naiveBayes) {
        try {
            naiveBayes.getDataFromCSV("outputFile.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(naiveBayes.test(testFile));
    }

    public static void printFiles(File trainDirectory, File testFile) {
        for(File f: trainDirectory.listFiles()) {
            System.out.println("Training file: " + f.getName());
        }
        System.out.println("Test file: " + testFile.getName());
    }
}

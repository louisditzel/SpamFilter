import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class filter {

    public static void main(String[] args) {

        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        switch (args[0]) {
            case "-train":
                File trainDirectory = new File(args[1]);

                try {
                    naiveBayes.train(trainDirectory.listFiles());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CSVWriter.writeCsvFile("outputFile.banter", naiveBayes.getHamHash(), naiveBayes.getTrainHamDataTotal(),
                        naiveBayes.getSpamHash(), naiveBayes.getTrainSpamDataTotal(), naiveBayes.getVocabList());
                break;
            case "-test":
                File testFile = new File(args[1]);
                try {
                    naiveBayes.getDataFromCSV("outputFile.banter");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print(naiveBayes.test(testFile));
                break;
            default:
                testFile = new File(args[0]);
                try {
                    naiveBayes.getDataFromCSV("outputFile.banter");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print(naiveBayes.test(testFile));
                break;
        }


    }

    public static void printFiles(File trainDirectory, File testFile) {
        for(File f: trainDirectory.listFiles()) {
            System.out.println("Training file: " + f.getName());
        }
        System.out.println("Test file: " + testFile.getName());
    }
}

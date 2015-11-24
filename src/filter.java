import java.io.File;
import java.io.IOException;

public class filter {

    public static void main(String[] args) {
        File trainDirectory = new File(args[0]);
        trainDirectory.mkdir();
        File testFile = new File(args[1]);
//        printFiles(trainDirectory,testFile);
        NaiveBayes naiveBayes = NaiveBayes.getInstance();
        try {
            //naiveBayes.train(trainDirectory.listFiles());
            naiveBayes.getDataFromCSV("outputFile.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(naiveBayes.test(testFile));
//        naiveBayes.printTrainingData();

        //CSVWriter.writeCsvFile("outputFile.banter", naiveBayes.getHamHash(), naiveBayes.getTrainHamDataTotal(), naiveBayes.getSpamHash(), naiveBayes.getTrainSpamDataTotal(), naiveBayes.getVocabList());
    }

    public static void printFiles(File trainDirectory, File testFile) {
        for(File f: trainDirectory.listFiles()) {
            System.out.println("Training file: " + f.getName());
        }
        System.out.println("Test file: " + testFile.getName());
    }
}

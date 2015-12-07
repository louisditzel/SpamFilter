import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

public class filter {

    public static void main(String[] args) {

        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        switch (args[0]) {
            case "-train":
                Path trainDirectory = Paths.get(args[1]);
                train(trainDirectory, naiveBayes);
                break;
            case "-test":
                Path testFile = Paths.get(args[1]);
                test(testFile, naiveBayes);
                break;
            default:
                testFile = Paths.get(args[0]);
                test(testFile, naiveBayes);
                break;
        }
    }

    public static void train(Path trainDirectory, NaiveBayes naiveBayes) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(trainDirectory)) {
            try {
                naiveBayes.train(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

        BanterWriter.writeBanterFile("outputFile.banter",  naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());
    }

    public static void test(Path testFile, NaiveBayes naiveBayes) {
        try {
            naiveBayes.getDataFromBanter("outputFile.banter");
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

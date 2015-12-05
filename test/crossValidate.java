import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by louis on 02/12/2015.
 */
public class crossValidate {

    String inputDir = System.getProperty("user.dir") + "/train";
    String outputDir = System.getProperty("user.dir") + "/crossvalidate";
    Path[] outputDirectories = new Path[10];
    int totNumFiles = 0;

    @Test
    public void validate() {
        setUpFiles();
        NaiveBayes naiveBayes = NaiveBayes.getInstance();

        int sumHam = 0;
        int sumSpam = 0;
        for (int i = 0; i < 10; i++) {
            naiveBayes.clearInstance();
            System.out.println("Index = " + i);
            trainFiles(i, naiveBayes);
            naiveBayes.clearInstance();
            int[] numIncorrect = testFiles(i, naiveBayes);
            System.out.println("Number incorrect ham = " + numIncorrect[0]);
            System.out.println("Number incorrect spam = " + numIncorrect[1]);
            sumHam += numIncorrect[0];
            sumSpam += numIncorrect[1];
        }
        double averageHam = (sumHam - 1) / 10.0;
        double averageSpam = sumSpam / 10.0;
        System.out.println("Total number files = " + (totNumFiles - 1));
        System.out.println("Total number incorrect ham = " + (sumHam - 1));
        System.out.println("Total number incorrect spam = " + sumSpam);
        System.out.println("Average number incorrect ham per iteration = " + averageHam);
        System.out.println("Average number incorrect spam per iteration = " + averageSpam);
    }

    private void setUpFiles() {

        Path inputDirectory = Paths.get(inputDir);
        Path outputRoot = Paths.get(outputDir);

        if (Files.exists(outputRoot)) {
            try {
                deleteDirectory(outputRoot);
            } catch (IOException e) {
            }
        }
        try {
            Files.createDirectory(outputRoot);
        } catch (IOException e) {
        }

        for (int i = 0; i < 10; i++) {
            outputDirectories[i] = Paths.get(outputDir + "/" + i);
            try {
                Files.createDirectory(outputDirectories[i]);
            } catch (IOException e) {
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDirectory)) {
            int i = 0;
            for (Path file : stream) {
                int bucket = ThreadLocalRandom.current().nextInt(0, 10);
                Files.copy(file, Paths.get(outputDir + "/" + bucket + "/" + file.getFileName().toString()),
                        StandardCopyOption.REPLACE_EXISTING);
                i++;
            }
            totNumFiles = i;
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println(e);
        }

    }

    private void trainFiles(int index, NaiveBayes naiveBayes) {
        System.out.println("Training files...");


        Path crossValidTrainDir = Paths.get(outputDir + "/train");
        if (Files.exists(crossValidTrainDir)) {
            try {
                deleteDirectory(crossValidTrainDir);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

        try {
            Files.createDirectory(crossValidTrainDir);
        } catch (IOException e) {
            System.err.println(e);
        }


        for (int i = 0; i < 10; i++) {
            if (i != index) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputDirectories[i])) {
                    for (Path file : stream) {
                        Files.copy(file, Paths.get(outputDir + "/train/" + file.getFileName().toString()));
                    }
                } catch (IOException | DirectoryIteratorException e) {
                    System.err.println(e);
                }
            }
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(crossValidTrainDir)) {
            try {
                naiveBayes.train(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        BanterWriter.writeBanterFile("outputFile_crossvalid.banter", naiveBayes.getVocabList(),
                naiveBayes.getNumHamFiles(), naiveBayes.getNumSpamFiles());

    }

    private int[] testFiles(int index, NaiveBayes naiveBayes) {
        int numIncorrect[] = new int[2];
        numIncorrect[0] = 0;
        numIncorrect[1] = 0;
        System.out.println("Testing files...");
        try {
            naiveBayes.getDataFromBanter("outputFile_crossvalid.banter");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputDirectories[index])) {
            for (Path file : stream) {
                naiveBayes.clearInstanceForTest();
                String out = naiveBayes.test(file);
                if (!file.getFileName().toString().contains(out.trim()))
                    switch (out.trim()) {
                        case "ham":
                            numIncorrect[1]++;
                            break;
                        case "spam":
                            numIncorrect[0]++;
                            break;
                        default:
                            System.out.println("Error: Not ham or spam");
                    }
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        return numIncorrect;
    }

    private void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

}

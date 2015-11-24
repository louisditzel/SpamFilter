package com.ml.spam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by louis on 23/11/2015.
 */
public class NaiveBayes {

    public enum Class {Ham, Spam}

    private static NaiveBayes instance = null;
    private HashMap<String, Integer> trainHamData, trainSpamData;
    private ArrayList<String> trainVocabulary;
    private int trainHamDataTotal, trainSpamDataTotal;
    private double probabilityHam, probabilitySpam;

    protected NaiveBayes() {
        trainHamData = new HashMap<>();
        trainSpamData = new HashMap<>();
        trainVocabulary = new ArrayList<>();
        trainHamDataTotal = 0;
        trainSpamDataTotal = 0;
        probabilityHam = 0;
        probabilitySpam = 0;
    }

    public static NaiveBayes getInstance() {
        if (instance == null)
            instance = new NaiveBayes();
        return instance;
    }

    public void train(File[] trainFiles) throws IOException {
        int numHam = 0;
        int numSpam = 0;
        for (File trainFile : trainFiles) {
            if(trainFile.getName().startsWith("ham")) {
                numHam++;
                addWordsFromFile(trainFile, trainHamData, Class.Ham);
            } else if (trainFile.getName().startsWith("spam")) {
                numSpam++;
                addWordsFromFile(trainFile, trainSpamData, Class.Spam);
            } else if (trainFile.getName().equals(".DS_Store")) {
            } else
                throw new IOException("Training filename does not start with either ham or spam");
        }
        setClassProbabilities(numHam, numSpam);
    }

    public String test(File testFile) {
        Scanner sc = null;
        double pHam = probabilityHam;
        double pSpam = probabilitySpam;
        System.out.println("pSpam: " + pSpam);
        try {
            sc = new Scanner(testFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String[] words = sc.nextLine().trim().split(" ");
            for (String word : words) {
                pHam *= getProbabilityOfWordGivenClass(word, Class.Ham);
                pSpam *= getProbabilityOfWordGivenClass(word, Class.Spam);
                System.out.println("pSpam: " + pSpam);
            }
        }
        String result = pHam >= pSpam ? "ham\n" : "spam\n";
        System.out.println(Math.log(pHam) - Math.log(pSpam));
        return result;
    }

    public void printTrainingData() {
        System.out.println("Ham: ");
        for (Map.Entry<String, Integer> entry : trainHamData.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println("\nSpam: ");
        for (Map.Entry<String, Integer> entry : trainSpamData.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    private void setClassProbabilities(int numHam, int numSpam) {
        int total = numHam + numSpam;
        probabilityHam = (double) numHam / (double) total;
        probabilitySpam = (double) numSpam / (double) total;
    }

    private void addWordsFromFile(File trainFile, HashMap<String, Integer> trainData, Class cl) {
        Scanner sc = null;
        try {
            sc = new Scanner(trainFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String[] words = sc.nextLine().trim().split(" ");
            for (String word : words) {
                if (word.isEmpty())
                    break;
                if (!trainData.containsKey(word))
                    trainData.put(word, 1);
                else
                    trainData.replace(word, trainData.get(word) + 1);
                if (cl == Class.Ham) trainHamDataTotal++;
                else trainSpamDataTotal++;
                if (!trainVocabulary.contains(word))
                    trainVocabulary.add(word);
            }
        }
    }

    private double getProbabilityOfWordGivenClass(String word, Class cl) {
        double probability = 0;
        if (cl == Class.Ham) {
            probability = (double) (trainHamData.getOrDefault(word, 0) + 1) / (double) (trainHamDataTotal + trainVocabulary.size());
        } else {
            probability = (double) (trainSpamData.getOrDefault(word, 0) + 1) / (double) (trainSpamDataTotal + trainVocabulary.size());
            System.out.println("word: " + word);
            System.out.println("numerator: " + (trainSpamData.getOrDefault(word, 0) + 1));
            System.out.println("denonimator: " + (trainSpamDataTotal + trainVocabulary.size()));
            System.out.println("prob: " + probability);
        }
        return probability;
    }
}

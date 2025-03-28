package com.comicboys.project;

import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.io.MappingsFileReader;
import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.io.TranslationFileManager;
import com.comicboys.project.io.XMLGenerator;

import java.util.Map;
import java.util.Random;


public class Main {
    public static Random random = new Random();
    public static void main(String[] args) {
        // creating config file object
        ConfigurationFile config = new ConfigurationFile();

        // read tsv file with specified number of lines
        int rows = 4;
        System.out.println("\nReading first " + rows + " rows");
        MappingsFileReader myReader = new MappingsFileReader(rows);
        // get mappings data structure
        Mappings mappings = myReader.getMappings();
        // using mappings to create vignette generator
        TranslationFileManager.TranslationGenerator translationGenerator = new TranslationFileManager.TranslationGenerator(config, mappings);

        XMLGenerator xmlGenerator = new XMLGenerator(mappings);

        // printing mappings data for reference
        System.out.println(mappings);
        System.out.println();
        // find match given certain word (finds FIRST row where this word appears)
        // returns a hashmap with key-value pairs for leftPose,combinedText, etc.
        String word1 = "to bow";
        System.out.println("\nFinding word: " + word1);
        System.out.println(mappings.findMatch(word1));
        word1 = "";
        System.out.println("\nFinding word: " + word1);
        System.out.println(mappings.findMatch(word1));
        word1 = "poo";
        System.out.println("\nFinding word: " + word1);
        System.out.println(mappings.findMatch(word1));
        System.out.println();

        // generate translations for words
        System.out.println("\nGenerating translations...");
        translationGenerator.generateTranslations();
        System.out.println();
        // Load and print translations
        Map<String, String> translations = translationGenerator.getTranslations();


        System.out.println("\nPrinting translations for combinedText and leftText (2nd and 3rd columns):\n");
        translations.forEach((source, target) -> System.out.println(source + " -> " + target));


       // StringEntry exampleEntry = new StringEntry();
       // System.out.println(xmlGenerator.generateXML(exampleEntry));

    }
}
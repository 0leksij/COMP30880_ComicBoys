package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.client.Mappings;
import com.comicboys.project.client.TSVReader;
import com.comicboys.project.client.VignetteGenerator;
import com.comicboys.project.config.ConfigurationFile;

import java.util.Map;
import java.util.Random;


public class Main {
    public static Random random = new Random();
    public static void main(String[] args) {
        // creating config file object
        ConfigurationFile config = new ConfigurationFile();

        // read tsv file with specified number of lines
        TSVReader myReader = new TSVReader(5);
        // get mappings data structure
        Mappings mappings = myReader.getMappings();
        // using mappings to create vignette generator
        VignetteGenerator vignetteGenerator = new VignetteGenerator(config, mappings);

        // printing mappings data for reference
        System.out.println(mappings);
        System.out.println();
        // find match given certain word (finds FIRST row where this word appears)
        // returns a hashmap with key-value pairs for leftPose,combinedText, etc.
        System.out.println(mappings.findMatch("to bow"));
        System.out.println();

        // generate translations for words
        vignetteGenerator.generateTranslations();
        System.out.println();
        // Load and print translations
        Map<String, String> translations = vignetteGenerator.getTranslations();

        translations.forEach((source, target) -> System.out.println(source + " -> " + target));




    }
}
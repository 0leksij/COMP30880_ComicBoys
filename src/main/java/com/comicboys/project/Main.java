package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.io.*;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class Main {
    public static Random random = new Random();
    public static void main(String[] args) throws InterruptedException {
        // creating config file object
        ConfigurationFile config = new ConfigurationFile();
        APIClient client = new APIClient(config);

        // read tsv file with specified number of lines
        int rows = 1000;
        System.out.println("\n------------------------------");
        System.out.println("\n-----[ PREVIOUS SPRINTS ]-----");
        System.out.println("\n------------------------------");
        System.out.println("\nReading first " + rows + " rows");
        MappingsFileReader myReader = new MappingsFileReader(rows);
        // get mappings data structure
        Mappings mappings = myReader.getMappings();
        // using mappings to create vignette generator
        TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);

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
        translationGenerator.generateTranslations(mappings.getAllTextFragments());
        System.out.println();
        // Load and print translations
        Map<String, String> translations = translationGenerator.getTranslations();

        System.out.println("\nPrinting translations for combinedText and leftText (2nd and 3rd columns):\n");
        translations.forEach((source, target) -> System.out.println(source + " -> " + target));

        XMLGenerator generator = new XMLGenerator(mappings);
        String filePath = "assets/mappings/generated_comic.xml";
        generator.generateXML(0, filePath);

        String blueprintPath = "assets/blueprint/";
        TextBlueprint blueprint = new TextBlueprint(blueprintPath + "specification.xml");
        System.out.println("\nSpeech balloons from file: ");
        System.out.println(blueprint.getSpeechBalloons());
        System.out.println("\nTranslations in filepath: " + blueprintPath);


        System.out.println("\n------------------------------");
        System.out.println("\n------[ CURRENT SPRINT ]------");
        System.out.println("\n------------------------------");


        XMLTranslator translator = new XMLTranslator(config, client, mappings, "story");
        translator.translateXML("sample_story2.xml");



        String storyPath = "assets/story/specification_short.xml";
        StoryBlueprint blueprintStory = new StoryBlueprint(storyPath);

        StoryGenerator sg = new StoryGenerator(client);
        blueprintStory.writeStory(sg, "sample_story2.xml");

    }
}
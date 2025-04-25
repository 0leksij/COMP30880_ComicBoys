package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.io.xml.XMLGenerator;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Random;


public class Main {
    public static Random random = new Random();
    private static String[] LESSON_SCHEDULE;
    private static final Document doc = XMLFileManager.createFile("assets/mappings/test/base.xml");
    public static void main(String[] args) {
        // creating config file object
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsFileReader = new MappingsFileReader();
        Mappings mappings = mappingsFileReader.getMappings();
        APIClient client = new APIClient(config);
        TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);
        XMLGenerator xmlGenerator = new XMLGenerator(mappings, translationGenerator);
        final String testPath = "assets/mappings/test/test.xml";
        final String storyPath = "assets/story/english-to-italian-story.xml";
        final String conjugationPath = "assets/blueprint/english-to-italian-conjugation-lesson.xml";

        System.out.println(config.getProperty("LESSON_SCHEDULE"));
        LESSON_SCHEDULE = config.getProperty("LESSON_SCHEDULE").split(",");
        for (int i = 0; i < LESSON_SCHEDULE.length; i++) {
            final String currentLesson = LESSON_SCHEDULE[i];
            // different possible lessons user can choose
            switch (currentLesson){
                case "left" -> generateLeftTextScene(xmlGenerator, testPath);
                case "whole", "combined" -> generateCombinedTextScene(xmlGenerator, testPath);
                case "conjugation" -> appendScene(conjugationPath);
                case "story" -> appendScene(storyPath);
                default -> System.out.printf("\nLesson %s is not a valid lesson\n", currentLesson);
            }
        }

        XMLFileManager.saveXMLToFile(doc,testPath);
    }
    private static void generateLeftTextScene(XMLGenerator xmlGenerator, String filePath) {
        NodeList scenes = xmlGenerator.generateLeftTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of left text scene");
            return;
        }
        appendScenes(scenes, filePath);
    }
    private static void generateCombinedTextScene(XMLGenerator xmlGenerator, String filePath) {
        NodeList scenes = xmlGenerator.generateCombinedTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of whole text scene");
            return;
        }
        appendScenes(scenes, filePath);
    }

    private static void appendScene(String filePath){
        Document inputDoc = XMLFileManager.loadXMLFromFile(filePath);
        Node scene = XMLFileManager.extractRandomSceneElement(inputDoc);
        XMLFileManager.appendScenes(doc,scene);
    }



    private static void appendScenes(NodeList scenes, String filePath) {
        XMLFileManager.appendScenes(doc, scenes);
    }
}
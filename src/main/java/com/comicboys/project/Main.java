package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.io.xml.XMLGenerator;
import com.comicboys.project.utility.LessonScheduler;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;

import java.util.Random;

public class Main {

    public static Random random = new Random();
    private static String[] LESSON_SCHEDULE;
    private static final String OPENING_SCENES_PATH = "assets/opening_scenes/opening.xml";
    private static final String TEST_PATH = "assets/mappings/test/test.xml";

    public static void main(String[] args) {

        // Load basic configuration and mappings
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsFileReader = new MappingsFileReader();
        Mappings mappings = mappingsFileReader.getMappings();
        APIClient client = new APIClient(config);
        TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);
        XMLGenerator xmlGenerator = new XMLGenerator(mappings, translationGenerator);

        // Create the output document
        Document doc = XMLFileManager.createFile("assets/mappings/test/base.xml");

        // Prepare lesson scheduling
        LESSON_SCHEDULE = config.getProperty("LESSON_SCHEDULE").split(",");
        LessonScheduler scheduler = new LessonScheduler(doc, xmlGenerator, LESSON_SCHEDULE);

        // Add opening scenes
//        scheduler.addOpeningScenes(OPENING_SCENES_PATH);

        // Generate lesson scenes
        String sourceLang = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        String targetLang = config.getProperty("TARGET_LANGUAGE").toLowerCase();
        String storyPath = "assets/story/" + sourceLang + "-to-" + targetLang + "-story.xml";
        String conjugationPath = "assets/conjugations/" + sourceLang + "-to-" + targetLang + "-conjugation.xml";

        scheduler.runLessons(TEST_PATH, storyPath, conjugationPath);

        // Save everything into the final XML
        XMLFileManager.saveXMLToFile(doc, TEST_PATH);

        System.out.println("Generation complete! XML saved to: " + TEST_PATH);
    }
}

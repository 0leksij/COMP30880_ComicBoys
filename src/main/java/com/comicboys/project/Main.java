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

    public static void main(String[] args) {
        // Creating config and core components
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsFileReader = new MappingsFileReader();
        Mappings mappings = mappingsFileReader.getMappings();
        APIClient client = new APIClient(config);
        TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);
        XMLGenerator xmlGenerator = new XMLGenerator(mappings, translationGenerator);

        String sourceLang = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        String targetLang = config.getProperty("TARGET_LANGUAGE").toLowerCase();
        final String testPath = "assets/mappings/test/test.xml";
        final String storyPath = "assets/story/" + sourceLang + "-to-" + targetLang + "-story.xml";
        final String conjugationPath = "assets/conjugations/" + sourceLang + "-to-" + targetLang + "-conjugation.xml";

        // Preparing the lesson schedule
        String[] lessonSchedule = config.getProperty("LESSON_SCHEDULE").split(",");
        Document doc = XMLFileManager.createFile("assets/mappings/test/base.xml");

        LessonScheduler scheduler = new LessonScheduler(doc, xmlGenerator, lessonSchedule);
        scheduler.runLessons(testPath, storyPath, conjugationPath);

        // Saving the final result
        XMLFileManager.saveXMLToFile(doc, testPath);
    }
}

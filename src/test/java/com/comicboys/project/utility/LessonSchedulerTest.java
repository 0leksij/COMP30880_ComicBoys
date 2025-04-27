package com.comicboys.project.utility;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.io.xml.XMLGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.print.Doc;

import static org.junit.jupiter.api.Assertions.*;

class LessonSchedulerTest {
    ConfigurationFile config;
    @BeforeEach
    void setUp() {
        config = new ConfigurationFile();
    }
    @Test
    void testLessonSchedulerLeftTextOnly() {
        config.setProperty("LESSON_SCHEDULE", "left");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        NodeList sceneNodes = doc.getElementsByTagName("scene");
        Node scene = null;
        int numberOfScenes = 0;
        for (int i = 0; i < sceneNodes.getLength(); i++) {
            scene = sceneNodes.item(i);
            if (scene.getNodeType() == Node.ELEMENT_NODE) {
                numberOfScenes++;
            }
        }
        assertNotNull(scene);
        // expect 2 scenes, one being the scene at start of comic, followed by
        // any number of lesson scenes (in our case here just a left text scene)
        assertEquals(2, numberOfScenes);

        NodeList panelNodes = ((Element) scene).getElementsByTagName("panel");
        // there should be a total of AT LEAST 4 or 6 panels since
        // left text can either be one or two characters, you will have
        // scene introduction panel, followed by
        //  3 panels if one character (no text, text, translation)
        //  5 panels if two characters (no text, text, no translation, translation, together)

        int numberOfPanels = 0;
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                numberOfPanels++;
            }
        }
        // check if either 4 or 6 panels
        assertTrue(
                numberOfPanels == 4 || numberOfPanels == 6
        );
    }

    @Test
    void testLessonSchedulerCombinedTextOnly() {
        config.setProperty("LESSON_SCHEDULE", "combined");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        NodeList sceneNodes = doc.getElementsByTagName("scene");
        Node scene = null;
        int numberOfScenes = 0;
        for (int i = 0; i < sceneNodes.getLength(); i++) {
            scene = sceneNodes.item(i);
            if (scene.getNodeType() == Node.ELEMENT_NODE) {
                numberOfScenes++;
            }
        }
        assertNotNull(scene);
        // expect 2 scenes, one being the scene at start of comic, followed by
        // any number of lesson scenes (in our case here just a left text scene)
        assertEquals(2, numberOfScenes);

        NodeList panelNodes = ((Element) scene).getElementsByTagName("panel");
        // there should be a total of EXACTLY 6 panels since
        // combined text can either be one or two characters, you will have
        // scene introduction panel, followed by
        //  5 panels if two characters (no text, text, no translation, translation, together)

        int numberOfPanels = 0;
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                numberOfPanels++;
            }
        }
        // check if 6 panels (for 2 characters)
        assertEquals(6, numberOfPanels);
    }
}

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
        assertEquals(2, numberOfScenes);

        NodeList panelNodes = ((Element) scene).getElementsByTagName("panel");
        int numberOfPanels = 0;
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                numberOfPanels++;
            }
        }
        assertTrue(numberOfPanels == 4 || numberOfPanels == 6);
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
        assertEquals(2, numberOfScenes);

        NodeList panelNodes = ((Element) scene).getElementsByTagName("panel");
        int numberOfPanels = 0;
        for (int i = 0; i < panelNodes.getLength(); i++) {
            Node panel = panelNodes.item(i);
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                numberOfPanels++;
            }
        }
        assertEquals(6, numberOfPanels);
    }

    @Test
    void testLessonSchedulerStoryOnly() {
        config.setProperty("LESSON_SCHEDULE", "story");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);

        NodeList sceneNodes = doc.getElementsByTagName("scene");
        assertEquals(2, sceneNodes.getLength());

        // Verify the story scene has panels
        Node storyScene = sceneNodes.item(1);
        NodeList panelNodes = ((Element) storyScene).getElementsByTagName("panel");
        assertTrue(panelNodes.getLength() > 0);
    }

    @Test
    void testLessonSchedulerConjugationOnly() {
        config.setProperty("LESSON_SCHEDULE", "conjugation");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);

        NodeList sceneNodes = doc.getElementsByTagName("scene");
        assertEquals(2, sceneNodes.getLength());

        // Verify the conjugation scene has panels
        Node conjugationScene = sceneNodes.item(1);
        NodeList panelNodes = ((Element) conjugationScene).getElementsByTagName("panel");
        assertTrue(panelNodes.getLength() > 0);
    }

    @Test
    void testLessonSchedulerMultipleLessons() {
        config.setProperty("LESSON_SCHEDULE", "left,combined,story,conjugation");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);

        // Should have opening scene + 4 lesson scenes
        NodeList sceneNodes = doc.getElementsByTagName("scene");
        assertEquals(5, sceneNodes.getLength());
    }

    @Test
    void testOpeningScene() {
        config.setProperty("LESSON_SCHEDULE", "left");
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);

        NodeList sceneNodes = doc.getElementsByTagName("scene");
        Node openingScene = sceneNodes.item(0);
        assertNotNull(openingScene);

        // Verify opening scene has exactly 1 panel
        NodeList panelNodes = ((Element) openingScene).getElementsByTagName("panel");
        assertEquals(1, panelNodes.getLength());

        // Verify the panel contains both Alfie and Betty
        Element panel = (Element) panelNodes.item(0);
        NodeList figures = panel.getElementsByTagName("figure");
        assertEquals(2, figures.getLength());

        boolean hasAlfie = false;
        boolean hasBetty = false;
        for (int i = 0; i < figures.getLength(); i++) {
            Element figure = (Element) figures.item(i);
            String name = figure.getElementsByTagName("name").item(0).getTextContent();
            if (name.equals("Alfie")) hasAlfie = true;
            if (name.equals("Betty")) hasBetty = true;
        }
        assertTrue(hasAlfie);
        assertTrue(hasBetty);
    }

    @Test
    void testSceneIntroPanels() {
        config.setProperty("LESSON_SCHEDULE", "left,left,left"); // 3 left text scenes
        String filePath = "assets/lessons/test/test.xml";
        LessonScheduler.generateCompleteLesson(config, filePath);
        Document doc = XMLFileManager.loadXMLFromFile(filePath);

        NodeList sceneNodes = doc.getElementsByTagName("scene");
        assertEquals(4, sceneNodes.getLength()); // opening + 3 lessons

        // Check scene numbers in the intro panels
        for (int i = 1; i < sceneNodes.getLength(); i++) {
            Node scene = sceneNodes.item(i);
            Node firstPanel = ((Element) scene).getElementsByTagName("panel").item(0);
            Node belowElement = ((Element) firstPanel).getElementsByTagName("below").item(0);
            String sceneText = belowElement.getTextContent();
            assertEquals("Scene " + (i-1), sceneText); // scenes are 0-indexed after opening
        }
    }

    @Test
    void testNumberToWord() {
        assertEquals("zero", LessonScheduler.numberToWord(0));
        assertEquals("one", LessonScheduler.numberToWord(1));
        assertEquals("five", LessonScheduler.numberToWord(5));
        assertEquals("nine", LessonScheduler.numberToWord(9));

        assertThrows(IllegalArgumentException.class, () -> {
            LessonScheduler.numberToWord(-1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            LessonScheduler.numberToWord(10);
        });
    }
}
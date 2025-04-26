package com.comicboys.project.utility;

import com.comicboys.project.io.xml.XMLGenerator;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LessonScheduler {

    private final Document doc;
    private final XMLGenerator xmlGenerator;
    private final String[] lessonSchedule;
    private int sceneCounter = 0; // Tracks total scenes

    public LessonScheduler(Document doc, XMLGenerator xmlGenerator, String[] lessonSchedule) {
        this.doc = doc;
        this.xmlGenerator = xmlGenerator;
        this.lessonSchedule = lessonSchedule;
    }

    public void runLessons(String testPath, String storyPath, String conjugationPath) {
        for (final String currentLesson : lessonSchedule) {
            // Insert intro panel at the start of each lesson
            insertIntroPanel(sceneCounter);

            switch (currentLesson) {
                case "left" -> generateLeftTextScene(testPath);
                case "whole", "combined" -> generateCombinedTextScene(testPath);
                case "conjugation" -> appendScene(conjugationPath);
                case "story" -> appendScene(storyPath);
                default -> System.out.printf("\nLesson %s is not a valid lesson\n", currentLesson);
            }
        }
    }

    private void insertIntroPanel(int sceneNumber) {
        Element panel = doc.createElement("panel");

        Element below = doc.createElement("below");
        below.setTextContent("Scene " + sceneNumber);
        panel.appendChild(below);

        Element border = doc.createElement("border");
        border.setTextContent("white");
        panel.appendChild(border);

        XMLFileManager.appendScenes(doc, panel);

        sceneCounter++; // increment after intro panel
    }

    private void generateLeftTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateLeftTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of left text scene");
            return;
        }
        XMLFileManager.appendScenes(doc, scenes);
        sceneCounter += scenes.getLength();
    }

    private void generateCombinedTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateCombinedTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of whole text scene");
            return;
        }
        XMLFileManager.appendScenes(doc, scenes);
        sceneCounter += scenes.getLength();
    }

    private void appendScene(String filePath) {
        Document inputDoc = XMLFileManager.loadXMLFromFile(filePath);
        Node scene = XMLFileManager.extractRandomSceneElement(inputDoc);
        XMLFileManager.appendScenes(doc, scene);
        sceneCounter++;
    }
}

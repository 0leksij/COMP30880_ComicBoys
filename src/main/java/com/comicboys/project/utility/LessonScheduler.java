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

            switch (currentLesson) {
                case "left" -> generateLeftTextScene(testPath);
                case "whole", "combined" -> generateCombinedTextScene(testPath);
                case "conjugation" -> generateConjugationScene(conjugationPath);
                case "story" -> generateStoryScene(storyPath);
                default -> System.out.printf("\nLesson %s is not a valid lesson\n", currentLesson);
            }
        }
    }

    private Node getIntroPanel(int sceneNumber) {
        Element panel = doc.createElement("panel");

        Element below = doc.createElement("below");
        below.setTextContent("Scene " + sceneNumber);
        panel.appendChild(below);

        Element border = doc.createElement("border");
        border.setTextContent("white");
        panel.appendChild(border);

        sceneCounter++;
        return panel;
    }

    private void generateLeftTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateLeftTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of left text scene");
            return;
        }
        generateTextScene(scenes);
    }

    private void generateCombinedTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateCombinedTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of whole text scene");
            return;
        }
        generateTextScene(scenes);
    }

    private void generateTextScene(NodeList sceneElements) {
        Node scene;
        for (int i = 0; i < sceneElements.getLength(); i++) {
            Node currentScene = sceneElements.item(i);
            if (currentScene.getNodeType() == Node.ELEMENT_NODE) {
                scene = currentScene;
                Node sceneIntroPanel = getIntroPanel(sceneCounter);
                XMLFileManager.insertFirstChild(scene, sceneIntroPanel);
                XMLFileManager.appendScenes(doc, scene);
                break;
            }
        }
    }

    private void generateConjugationScene(String filePath) {
        Document inputDoc = XMLFileManager.loadXMLFromFile(filePath);
        Node scene = XMLFileManager.extractRandomSceneElement(inputDoc);
        appendScene(scene);
    }
    private void generateStoryScene(String filePath) {
        Document inputDoc = XMLFileManager.loadXMLFromFile(filePath);
        Node scene = XMLFileManager.extractRandomSceneElement(inputDoc);
        // the stories have scene introductions as first panel, so need to remove first
        XMLFileManager.removeFirstChild(scene);
        appendScene(scene);
    }

    private void appendScene(Node scene) {
        Node sceneIntroPanel = getIntroPanel(sceneCounter);
        XMLFileManager.insertFirstChild(scene, sceneIntroPanel);
        XMLFileManager.appendScenes(doc, scene);
    }
}

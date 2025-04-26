package com.comicboys.project.utility;

import com.comicboys.project.io.xml.XMLGenerator;
import org.w3c.dom.*;

public class LessonScheduler {

    private final Document doc;
    private final XMLGenerator xmlGenerator;
    private final String[] lessonSchedule;
    private int currentSceneNumber = 0; // Keep track of scene numbering

    public LessonScheduler(Document doc, XMLGenerator xmlGenerator, String[] lessonSchedule) {
        this.doc = doc;
        this.xmlGenerator = xmlGenerator;
        this.lessonSchedule = lessonSchedule;
        ensureFiguresExist();
    }

    private void ensureFiguresExist() {
        NodeList figuresList = doc.getElementsByTagName("figures");
        if (figuresList.getLength() == 0 || !figuresList.item(0).hasChildNodes()) {
            initializeDefaultFigures();
        }
    }

    private void initializeDefaultFigures() {
        Element figuresElement = (Element) doc.getElementsByTagName("figures").item(0);
        if (figuresElement == null) {
            figuresElement = doc.createElement("figures");
            doc.getDocumentElement().insertBefore(figuresElement,
                    doc.getElementsByTagName("scenes").item(0));
        }

        // Clear any existing figures
        while (figuresElement.hasChildNodes()) {
            figuresElement.removeChild(figuresElement.getFirstChild());
        }

        // Add default figures
        addFigure("Alfie", "male", "light brown", "dark brown", "red");
        addFigure("Betty", "female", null, null, null);
        addFigure("Gemma", "female", "olive", "black", null);
    }

    private void addFigure(String id, String appearance, String skin, String hair, String lips) {
        Element figuresElement = (Element) doc.getElementsByTagName("figures").item(0);
        Element figure = doc.createElement("figure");

        addTextElement(figure, "id", id);
        addTextElement(figure, "name", id);
        addTextElement(figure, "appearance", appearance);

        if (skin != null) addTextElement(figure, "skin", skin);
        if (hair != null) addTextElement(figure, "hair", hair);
        if (lips != null) addTextElement(figure, "lips", lips);

        addTextElement(figure, "facing", "right");
        figuresElement.appendChild(figure);
    }

    private void addTextElement(Element parent, String tagName, String textContent) {
        Element element = doc.createElement(tagName);
        element.appendChild(doc.createTextNode(textContent));
        parent.appendChild(element);
    }

    public void runLessons(String testPath, String storyPath, String conjugationPath) {
        for (final String currentLesson : lessonSchedule) {
            switch (currentLesson) {
                case "left" -> generateLeftTextScene(testPath);
                case "whole", "combined" -> generateCombinedTextScene(testPath);
                case "conjugation" -> appendScene(conjugationPath);
                case "story" -> appendScene(storyPath);
                default -> System.out.printf("\nLesson %s is not a valid lesson\n", currentLesson);
            }
        }
    }

    public void addOpeningScenes(String openingScenesPath) {
        Document openingDoc = XMLFileManager.loadXMLFromFile(openingScenesPath);
        NodeList openingScenes = openingDoc.getElementsByTagName("scene");

        for (int i = 0; i < openingScenes.getLength(); i++) {
            Node importedScene = doc.importNode(openingScenes.item(i), true);

            if (importedScene.getNodeType() == Node.ELEMENT_NODE) {
                Element sceneElement = (Element) importedScene;
                // Set or override the scene number
                sceneElement.setAttribute("number", String.valueOf(currentSceneNumber++));
            }

            XMLFileManager.appendScenes(doc, importedScene);
        }
    }

    private void generateLeftTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateLeftTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of left text scene");
            return;
        }
        XMLFileManager.appendScenes(doc, scenes);
    }

    private void generateCombinedTextScene(String filePath) {
        NodeList scenes = xmlGenerator.generateCombinedTextXML(filePath);
        if (scenes == null) {
            System.out.println("Unsuccessful generation of whole text scene");
            return;
        }
        XMLFileManager.appendScenes(doc, scenes);
    }

    private void appendScene(String filePath) {
        Document inputDoc = XMLFileManager.loadXMLFromFile(filePath);
        Node scene = XMLFileManager.extractRandomSceneElement(inputDoc);

        if (scene.getNodeType() == Node.ELEMENT_NODE) {
            Element sceneElement = (Element) scene;
            sceneElement.setAttribute("number", String.valueOf(currentSceneNumber++));
        }

        XMLFileManager.appendScenes(doc, scene);
    }
}

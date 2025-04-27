package com.comicboys.project.utility;

import com.comicboys.project.audio.AudioGenerator;
import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.io.conjugation.ConjugationGenerator;
import com.comicboys.project.io.story.StoryGenerator;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.io.xml.XMLAudioInserter;
import com.comicboys.project.io.xml.XMLGenerator;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Map;
import java.util.Objects;

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
    /* Static Factory Method:
     *  every time this method is run, a new instance of LessonScheduler is created, once this method is done,
     *  that instance is discarded
     *  very similar to singleton, there is only at most one instance of LessonScheduler at any given time,
     *  but in our case that instance is re-created each time generateCompleteLesson is called again, and it
     *  only lasts the lifetime of the method call, where after it is discarded by Java GC (garbage collector)
     */
    // method that specifies target path (for testing)
    public static void generateCompleteLesson(ConfigurationFile config, String targetPath) {
        try {
            // 1. Initialize components and ensure conjugations and stories exist
            MappingsFileReader mappingsFileReader = new MappingsFileReader();
            Mappings mappings = mappingsFileReader.getMappings();
            APIClient client = new APIClient(config);
            TranslationGenerator translationGenerator = new TranslationGenerator(config, client, mappings);
            XMLGenerator xmlGenerator = new XMLGenerator(mappings, translationGenerator);
            ConjugationGenerator conjugationGenerator = new ConjugationGenerator();
            StoryGenerator storyGenerator = new StoryGenerator(client);

            String sourceLang = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
            String targetLang = config.getProperty("TARGET_LANGUAGE").toLowerCase();

            String storyPath = "assets/story/" + sourceLang + "-to-" + targetLang + "-story.xml";
            String conjugationPath = "assets/conjugations/" + sourceLang + "-to-" + targetLang + "-conjugation.xml";

            File f;

            f = new File(conjugationPath);
            if (!f.exists()) conjugationGenerator.generateConjugationLesson();

            f = new File(storyPath);
            if (!f.exists()) storyGenerator.generateStoryXML();


            Document doc = XMLFileManager.createFile("assets/mappings/test/base.xml");
            String[] lessonSchedule = config.getProperty("LESSON_SCHEDULE").split(",");

            // 2. Generate and combine all lesson types
            LessonScheduler scheduler = new LessonScheduler(doc, xmlGenerator, lessonSchedule);
            scheduler.runLessons(targetPath, storyPath, conjugationPath);

            // 3. Add opening scene
            scheduler.addOpeningScene();

            // 4. Save final result (this one is the no audio version)
            XMLFileManager.saveXMLToFile(doc, targetPath);

            System.out.println("Lesson generation completed successfully!");


            // 5. Generate audio (if specified) from our no audio output file
            if (Objects.equals(config.getProperty("GENERATE_AUDIO").toLowerCase(), "true")) {
                AudioGenerator audioGenerator = new AudioGenerator(config);
                audioGenerator.generateAudioFromXML(doc);

                // 6. Insert generated audio into our new output file (this one has audio)
                String fileName = sourceLang + "-to-" + targetLang + "-full-lesson-with-audio.xml";
                XMLAudioInserter audioFile = new XMLAudioInserter(targetPath, audioGenerator.getMap(), fileName);
                audioFile.insertAudio();

                System.out.println("Lesson with audio generation completed successfully!");
            }


        } catch (Exception e) {
            System.err.println("Error generating complete lesson: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // method that defaults target path
    public static void generateCompleteLesson(ConfigurationFile config) {
        try {
            String sourceLang = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
            String targetLang = config.getProperty("TARGET_LANGUAGE").toLowerCase();
            String targetPath = "assets/lessons/" + sourceLang + "-to-" + targetLang + "-full-lesson.xml";
            generateCompleteLesson(config, targetPath);
        } catch (Exception e) {
            System.err.println("Error generating complete lesson: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addOpeningScene() {
        try {
            // Create opening scene
            Element openingScene = doc.createElement("scene");

            // Create special opening panel
            Node openingPanel = createOpeningPanel();
            openingScene.appendChild(openingPanel);

            // Insert the opening scene at the beginning
            NodeList scenesList = doc.getElementsByTagName("scenes");
            if (scenesList.getLength() > 0) {
                Node scenes = scenesList.item(0);
                Node firstChild = scenes.getFirstChild();
                scenes.insertBefore(openingScene, firstChild);
            }
        } catch (Exception e) {
            System.err.println("Error adding opening scene: " + e.getMessage());
        }
    }

    private Node createOpeningPanel() {
        Element panel = doc.createElement("panel");

        // Create middle panel with both characters
        Element middle = doc.createElement("middle");

        // Add Alfie
        Element alfieFigure = doc.createElement("figure");
        addCharacterElement(doc, alfieFigure, "Alfie", "male", "waving", "right", true);
        middle.appendChild(alfieFigure);

        // Add Betty
        Element bettyFigure = doc.createElement("figure");
        addCharacterElement(doc, bettyFigure, "Betty", "female", "waving", "left", false);
        middle.appendChild(bettyFigure);

        panel.appendChild(middle);

        // Add border
        Element border = doc.createElement("border");
        border.setTextContent("white");
        panel.appendChild(border);

        return panel;
    }

    private Node getIntroPanel(int sceneNumber) {
        Element panel = doc.createElement("panel");

        // For scene numbers less than 10, create a middle panel with Betty
        if (sceneNumber < 10) {
            Element middle = doc.createElement("middle");
            Element figure = doc.createElement("figure");

            // Add Betty's details
            addCharacterElement(doc, figure, "Betty", "female", numberToWord(sceneNumber), "right", false);

            middle.appendChild(figure);
            panel.appendChild(middle);
        }
        // For scene numbers 10 or higher, create left and right panels with Alfie and Betty
        else {
            // Left panel with Alfie
            int leftDigit = sceneNumber / 10;
            int rightDigit = sceneNumber % 10;

            Element left = doc.createElement("left");
            Element leftFigure = doc.createElement("figure");
            addCharacterElement(doc, leftFigure, "Alfie", "male", numberToWord(leftDigit), "right", true);
            leftFigure.appendChild(createElementWithText(doc, "horizontal", "more"));
            left.appendChild(leftFigure);
            panel.appendChild(left);

            // Right panel with Betty
            Element right = doc.createElement("right");
            Element rightFigure = doc.createElement("figure");
            addCharacterElement(doc, rightFigure, "Betty", "female",  numberToWord(rightDigit) + "flipped", "left", false);
            rightFigure.appendChild(createElementWithText(doc, "horizontal", "less"));
            right.appendChild(rightFigure);
            panel.appendChild(right);
        }

        // Add the scene number below
        Element below = doc.createElement("below");
        below.setTextContent("Scene " + sceneNumber);
        panel.appendChild(below);

        // Add the border
        Element border = doc.createElement("border");
        border.setTextContent("white");
        panel.appendChild(border);

        sceneCounter++;
        return panel;
    }

    // Helper method to add character elements
    private void addCharacterElement(Document doc, Element figure, String id,
                                     String appearance, String pose,
                                     String facing, boolean isAlfie) {
        figure.appendChild(createElementWithText(doc, "id", id));
        figure.appendChild(createElementWithText(doc, "name", id));
        figure.appendChild(createElementWithText(doc, "appearance", appearance));

        if (isAlfie) {
            figure.appendChild(createElementWithText(doc, "skin", "light brown"));
            figure.appendChild(createElementWithText(doc, "hair", "dark brown"));
            figure.appendChild(createElementWithText(doc, "lips", "red"));
        }

        figure.appendChild(createElementWithText(doc, "pose", pose));
        figure.appendChild(createElementWithText(doc, "facing", facing));
    }

    // Helper method to create an element with text content
    private Element createElementWithText(Document doc, String tagName, String text) {
        Element element = doc.createElement(tagName);
        element.setTextContent(text);
        return element;
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

    public static String numberToWord(int num) {
        return switch (num) {
            case 0 -> "zero";
            case 1 -> "one";
            case 2 -> "two";
            case 3 -> "three";
            case 4 -> "four";
            case 5 -> "five";
            case 6 -> "six";
            case 7 -> "seven";
            case 8 -> "eight";
            case 9 -> "nine";
            default -> throw new IllegalArgumentException("Number must be between 0 and 9");
        };
    }
}

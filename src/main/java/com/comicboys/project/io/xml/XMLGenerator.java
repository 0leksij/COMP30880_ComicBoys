package com.comicboys.project.io.xml;

import com.comicboys.project.Main;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.io.translate.TranslationGenerator;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;

public class XMLGenerator {

    private final Mappings mappings;
    private final TranslationGenerator translationGenerator;
    private String consistentBackground = null;


    public XMLGenerator(Mappings mappings, TranslationGenerator translationGenerator) {
        this.mappings = mappings;
        this.translationGenerator = translationGenerator;
    }

    public NodeList generateLeftTextXML(String filePath) {
        int randomIndex;
        StringEntry selectedRow;
        do {
            randomIndex = Main.random.nextInt(0, mappings.size());
            selectedRow = mappings.getEntries().get(randomIndex).toStringEntry();
        } while (selectedRow.getLeftText().isEmpty());
        return generateXML(randomIndex, filePath);
    }
    public NodeList generateCombinedTextXML(String filePath) {
        int randomIndex;
        StringEntry selectedRow;
        do {
            randomIndex = Main.random.nextInt(0, mappings.size());
            selectedRow = mappings.getEntries().get(randomIndex).toStringEntry();
        } while (selectedRow.getCombinedText().isEmpty());
        return generateXML(randomIndex, filePath);
    }

    public NodeList generateXML(int rowIndex, String filePath) {
        StringEntry selectedRow = mappings.getEntries().get(rowIndex).toStringEntry();

        consistentBackground = selectedRow.getBackgrounds() != null ? selectedRow.getBackgrounds() : "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element comicElement = doc.createElement("comic");
            doc.appendChild(comicElement);
            Element scenesElement = doc.createElement("scenes");
            comicElement.appendChild(scenesElement);

            // generate one scene from this row
            int numScenes = 1;

            for (int i = 0; i < numScenes; i++) {
                Element sceneElement = doc.createElement("scene");
                scenesElement.appendChild(sceneElement);
                String randomCombinedText = selectedRow.getCombinedText();

                if (randomCombinedText == null || randomCombinedText.replace(" ", "").isEmpty()) {
                    createSingleCharacterPanels(doc, sceneElement, selectedRow);
                }
                else {
                    createMultipleCharacterPanels(doc, sceneElement, selectedRow);
                }
            }

            // get all <scene> elements (NOT <scenes>)
            NodeList scenes = doc.getElementsByTagName("scene");

            // Save to file
            if (!XMLFileManager.saveXMLToFile(doc, filePath)) {
                throw new Exception("ERROR in XMLGenerator class: Failed to save file!");
            }
            // return new scenes
            return scenes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    void createSingleCharacterPanels(Document doc, Element sceneElement, StringEntry selectedRow) {
        String leftPose = selectedRow.getLeftPose();
        String leftText = selectedRow.getLeftText();
        sceneElement.appendChild(createPanelWithLeftCharacter(doc, leftPose));
        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, leftText, leftPose));
        translationGenerator.generateTranslations(leftText);
        String translatedText = translationGenerator.getTranslations().get(leftText);
        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, translatedText, leftPose));
    }

    void createMultipleCharacterPanels(Document doc, Element sceneElement, StringEntry selectedRow) {
        String leftPose = selectedRow.getLeftPose();
        String rightPose = selectedRow.getRightPose();
        // if right pose is empty, just use left pose
        if (rightPose.isEmpty()) { rightPose = leftPose; }
        sceneElement.appendChild(createPanelWithLeftCharacter(doc, leftPose));

        String randomCombinedText = selectedRow.getCombinedText();

        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, randomCombinedText, leftPose));

        sceneElement.appendChild(createPanelWithRightCharacter(doc, rightPose));

        translationGenerator.generateTranslations(randomCombinedText);
        String translatedText = translationGenerator.getTranslations().get(randomCombinedText);
        sceneElement.appendChild(createPanelWithRightCharacterAndBalloon(doc, translatedText, rightPose));

        sceneElement.appendChild(createPanelWithBothCharacters(doc, randomCombinedText, translatedText, leftPose, rightPose));
    }

    private Element createPanelWithLeftCharacter(Document doc, String pose) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createLeftCharacterElement(doc, pose));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithLeftCharacterAndBalloon(Document doc, String text, String pose) {
        Element panel = doc.createElement("panel");
        Element left = createLeftCharacterElement(doc, pose);
        if (!text.isEmpty()) {
            createBalloon(doc, left, text);  // Add balloon to left element
        }
        panel.appendChild(left);
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithRightCharacter(Document doc, String pose) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createRightCharacterElement(doc, pose));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithRightCharacterAndBalloon(Document doc, String text, String pose) {
        Element panel = doc.createElement("panel");
        Element right = createRightCharacterElement(doc, pose);
        if (!text.isEmpty()) {
            createBalloon(doc, right, text);  // Add balloon to right element
        }
        panel.appendChild(right);
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithBothCharacters(Document doc, String leftText, String rightText,
                                                  String leftPose, String rightPose) {
        Element panel = doc.createElement("panel");

        // Create left character with balloon
        Element left = createLeftCharacterElement(doc, leftPose);
        if (!leftText.isEmpty()) {
            createBalloon(doc, left, leftText);
        }
        panel.appendChild(left);

        // Create right character with balloon
        Element right = createRightCharacterElement(doc, rightPose);
        if (!rightText.isEmpty()) {
            createBalloon(doc, right, rightText);
        }
        panel.appendChild(right);

        // Add setting
        panel.appendChild(createBackgroundSettingElement(doc));

        return panel;
    }

    private Element createLeftCharacterElement(Document doc, String pose) {
        Element left = doc.createElement("left");
        Element leftFigure = doc.createElement("figure");

        // Add character details
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode("Alfie"));
        leftFigure.appendChild(id);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode("Alfie"));
        leftFigure.appendChild(name);

        Element appearance = doc.createElement("appearance");
        appearance.appendChild(doc.createTextNode("male"));
        leftFigure.appendChild(appearance);

        Element skin = doc.createElement("skin");
        skin.appendChild(doc.createTextNode("light brown"));
        leftFigure.appendChild(skin);

        Element hair = doc.createElement("hair");
        hair.appendChild(doc.createTextNode("dark brown"));
        leftFigure.appendChild(hair);

        Element lips = doc.createElement("lips");
        lips.appendChild(doc.createTextNode("red"));
        leftFigure.appendChild(lips);

        // Add pose and facing
        String leftPose = pose != null ? getValidPose(pose) : "default";
        Element leftPoseElement = doc.createElement("pose");
        leftPoseElement.appendChild(doc.createTextNode(leftPose));
        leftFigure.appendChild(leftPoseElement);

        Element leftFacing = doc.createElement("facing");
        leftFacing.appendChild(doc.createTextNode("right"));
        leftFigure.appendChild(leftFacing);

        left.appendChild(leftFigure);
        return left;
    }

    private Element createRightCharacterElement(Document doc, String pose) {
        Element right = doc.createElement("right");
        Element rightFigure = doc.createElement("figure");

        // Add character details
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode("Betty"));
        rightFigure.appendChild(id);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode("Betty"));
        rightFigure.appendChild(name);

        Element appearance = doc.createElement("appearance");
        appearance.appendChild(doc.createTextNode("female"));
        rightFigure.appendChild(appearance);

        // Add pose and facing
        String rightPose = pose != null ? getValidPose(pose) : "default";
        Element rightPoseElement = doc.createElement("pose");
        rightPoseElement.appendChild(doc.createTextNode(rightPose));
        rightFigure.appendChild(rightPoseElement);

        Element rightFacing = doc.createElement("facing");
        rightFacing.appendChild(doc.createTextNode("left"));
        rightFigure.appendChild(rightFacing);

        right.appendChild(rightFigure);
        return right;
    }

    void createBalloon(Document doc, Element parentElement, String text) {
        if (!text.isEmpty()) {
            Element balloon = doc.createElement("balloon");
            balloon.setAttribute("status", "speech");
            Element content = doc.createElement("content");
            content.appendChild(doc.createTextNode(text));
            balloon.appendChild(content);
            parentElement.appendChild(balloon);
        }
    }

    Element createBackgroundSettingElement(Document doc) {
        Element setting = doc.createElement("setting");
        // Use empty string if consistentBackground is null
        String background = consistentBackground != null ? consistentBackground : "";
        setting.appendChild(doc.createTextNode(background));
        return setting;
    }

    String getValidPose(String pose) {
        if (mappings.isEmpty()) return pose;
        return mappings.getAllLeftPoses().contains(pose) ? pose : getRandomPose();
    }

    private String getRandomPose() {
        List<String> poses = mappings.getAllLeftPoses();
        return poses.get(Main.random.nextInt(poses.size()));
    }
}

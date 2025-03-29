package com.comicboys.project.io;

import com.comicboys.project.Main;
import com.comicboys.project.data.ListEntry;
import com.comicboys.project.data.Mappings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.List;

public class XMLGenerator {

    private final Mappings mappings;
    private String consistentBackground = null;


    public XMLGenerator(Mappings mappings) {
        this.mappings = mappings;
    }

    public String generateXML(int rowIndex, String filePath) {
        ListEntry selectedRow = mappings.getEntries().get(rowIndex);

        List<String> backgrounds = selectedRow.getBackgrounds();
        consistentBackground = backgrounds.get(Main.random.nextInt(backgrounds.size()));

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element comicElement = doc.createElement("comic");
            doc.appendChild(comicElement);

            for (int i = 0; i < 5; i++) {
                Element sceneElement = doc.createElement("scene");
                comicElement.appendChild(sceneElement);

                if (selectedRow.getCombinedText() == null || selectedRow.getCombinedText().isEmpty()) {
                    createSingleCharacterPanels(doc, sceneElement, selectedRow);
                }
                else {
                    createMultipleCharacterPanels(doc, sceneElement, selectedRow);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            String xmlContent = writer.toString();

            // Save to file
            saveXMLToFile(xmlContent, filePath);
            return xmlContent;
        } catch (Exception e) {
            e.printStackTrace();
            return "<error>XML Generation Failed</error>";
        }
    }

    void saveXMLToFile(String xmlContent, String filePath) {
        try {
            File file = new File(filePath);

            // Ensure the directory exists
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            writer.write(xmlContent);
            writer.close();

            System.out.println("XML saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving XML file.");
        }
    }

    void createSingleCharacterPanels(Document doc, Element sceneElement, ListEntry selectedRow) {
        sceneElement.appendChild(createPanelWithLeftCharacter(doc, selectedRow));
        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, selectedRow.getLeftText().getFirst()));
        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, "translation (" + selectedRow.getLeftText().getFirst() + ")"));
    }

    void createMultipleCharacterPanels(Document doc, Element sceneElement, ListEntry selectedRow) {
        sceneElement.appendChild(createPanelWithLeftCharacter(doc, selectedRow));

        String randomCombinedText = selectedRow.getCombinedText().get(Main.random.nextInt(selectedRow.getCombinedText().size()));
        sceneElement.appendChild(createPanelWithLeftCharacterAndBalloon(doc, randomCombinedText));

        sceneElement.appendChild(createPanelWithRightCharacter(doc, selectedRow));

        String translationText = "translation (" + selectedRow.getLeftText().getFirst() + ")";
        sceneElement.appendChild(createPanelWithRightCharacterAndBalloon(doc, translationText));

        sceneElement.appendChild(createPanelWithBothCharacters(doc, randomCombinedText, translationText));
    }

    private Element createPanelWithLeftCharacter(Document doc, ListEntry selectedRow) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createLeftCharacterElement(doc, selectedRow));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithLeftCharacterAndBalloon(Document doc, String text) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createLeftCharacterElement(doc, null));
        panel.appendChild(createBalloon(doc, "left_balloon", text));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithRightCharacter(Document doc, ListEntry selectedRow) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createRightCharacterElement(doc, selectedRow));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithRightCharacterAndBalloon(Document doc, String text) {
        Element panel = doc.createElement("panel");
        panel.appendChild(createRightCharacterElement(doc, null));
        panel.appendChild(createBalloon(doc, "right_balloon", text));
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createPanelWithBothCharacters(Document doc, String leftText, String rightText) {
        Element panel = doc.createElement("panel");
        Element both = doc.createElement("both");

        both.appendChild(createLeftCharacterElement(doc, null));
        both.appendChild(createRightCharacterElement(doc, null));
        both.appendChild(createBalloon(doc, "left_balloon", leftText));
        both.appendChild(createBalloon(doc, "right_balloon", rightText));

        panel.appendChild(both);
        panel.appendChild(createBackgroundSettingElement(doc));
        return panel;
    }

    private Element createLeftCharacterElement(Document doc, ListEntry selectedRow) {
        Element left = doc.createElement("left");
        Element leftFigure = doc.createElement("figure");
        String leftPose = selectedRow != null ? getValidPose(selectedRow.getLeftPose().getFirst()) : "default";
        Element leftPoseElement = doc.createElement("pose");
        leftPoseElement.appendChild(doc.createTextNode(leftPose));
        leftFigure.appendChild(leftPoseElement);
        Element leftFacing = doc.createElement("facing");
        leftFacing.appendChild(doc.createTextNode("right"));
        leftFigure.appendChild(leftFacing);
        left.appendChild(leftFigure);
        return left;
    }

    private Element createRightCharacterElement(Document doc, ListEntry selectedRow) {
        Element right = doc.createElement("right");
        Element rightFigure = doc.createElement("figure");
        String rightPose = selectedRow != null ? getValidPose(selectedRow.getRightPose().getFirst()) : "default";
        Element rightPoseElement = doc.createElement("pose");
        rightPoseElement.appendChild(doc.createTextNode(rightPose));
        rightFigure.appendChild(rightPoseElement);
        Element rightFacing = doc.createElement("facing");
        rightFacing.appendChild(doc.createTextNode("left"));
        rightFigure.appendChild(rightFacing);
        right.appendChild(rightFigure);
        return right;
    }

    private Element createBalloon(Document doc, String type, String text) {
        Element balloon = doc.createElement(type);
        balloon.setAttribute("status", "speech");
        Element content = doc.createElement("content");
        content.appendChild(doc.createTextNode(text));
        balloon.appendChild(content);
        return balloon;
    }

    private Element createBackgroundSettingElement(Document doc) {
        Element setting = doc.createElement("setting");
        setting.appendChild(doc.createTextNode(consistentBackground));
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

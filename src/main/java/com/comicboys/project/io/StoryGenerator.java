package com.comicboys.project.io;


import com.comicboys.project.data.NumberedList;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class StoryGenerator {


    /// //////////////// Oleksiis Story Generator

    public StoryGenerator() {

    }

    public NumberedList generateSceneStory(Node sceneNode) {
        List<String> descriptions = new ArrayList<>();
        NodeList panelNodes = ((Element) sceneNode).getElementsByTagName("panel");

        for (int i = 0; i < panelNodes.getLength(); i++) {
            Element panel = (Element) panelNodes.item(i);
            StringBuilder sb = new StringBuilder();

            // Use <above> instead of <setting> if available
            String location = getTextContent(panel, "above");
            if (location == null || location.isEmpty()) location = getTextContent(panel, "setting");
            location = (location != null && !location.isEmpty()) ? " in the " + location : "";

            // Use text from <balloon> instead of <pose> if available
            boolean hasBalloon = panel.getElementsByTagName("balloon").getLength() > 0;
            Element balloon = hasBalloon ? (Element) panel.getElementsByTagName("balloon").item(0) : null;
            List<Element> figureElements = getAllFiguresFromPanel(panel);

            for (Element figure : figureElements) {
                String name = getTextContent(figure, "name");
                String pose = getTextContent(figure, "pose");

                if (name == null) continue;
                sb.append(name);

                if (hasBalloon && balloon != null) {
                    String balloonText = getTextContent(balloon, "content");

                    if (balloonText != null && !balloonText.isEmpty()) {
                        sb.append(" is ").append(balloonText);
                        if (!location.isEmpty()) sb.append(location);
                    }
                } else if (pose != null && !pose.isEmpty()) {
                    sb.append(" is ").append(pose);
                    if (!location.isEmpty()) sb.append(location);
                }
            }
            sb.append(".");

            // Append <below> text if it exists
            String below = getTextContent(panel, "below");
            if (below != null && !below.isEmpty()) sb.append(" ").append(below);
            descriptions.add(sb.toString().trim());
        }
        return new NumberedList(descriptions);
    }

    private List<Element> getAllFiguresFromPanel(Element panel) {
        List<Element> figures = new ArrayList<>();

        // Middle, Left, Right sections may each contain <figure>
        for (String section : new String[]{"middle", "left", "right"}) {
            NodeList sections = panel.getElementsByTagName(section);
            for (int i = 0; i < sections.getLength(); i++) {
                Node sectionNode = sections.item(i);
                if (sectionNode.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList figureNodes = ((Element) sectionNode).getElementsByTagName("figure");
                    for (int j = 0; j < figureNodes.getLength(); j++) {
                        Node figureNode = figureNodes.item(j);
                        if (figureNode.getNodeType() == Node.ELEMENT_NODE) {
                            figures.add((Element) figureNode);
                        }
                    }
                }
            }
        }

        return figures;
    }

    private String getTextContent(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() > 0) return nodes.item(0).getTextContent().trim();
        return null;
    }


    /// //////////////// Reys Story Generator
    private ConfigurationFile config;
    private String filePath;
    private Document xmlDocument;

    public StoryGenerator(ConfigurationFile config) {
        this.config = config;
        this.filePath = "assets/story/";
    }

    public void loadXmlDocument(String xmlFilePath) {
        this.xmlDocument = XMLFileManager.loadXMLFromFile(xmlFilePath);
    }

    public String getCharactersByPanel(int sceneIndex) {
        StringBuilder result = new StringBuilder();

        if (xmlDocument == null) {
            return "No XML document loaded. Call loadXmlDocument() first.";
        }

        NodeList scenes = xmlDocument.getElementsByTagName("scene");
        if (sceneIndex >= scenes.getLength()) {
            return "Scene index out of bounds.";
        }

        Element scene = (Element) scenes.item(sceneIndex);
        NodeList panels = scene.getElementsByTagName("panel");

        for (int panelIndex = 0; panelIndex < panels.getLength(); panelIndex++) {
            Element panel = (Element) panels.item(panelIndex);
            result.append((panelIndex + 1)).append(". ");

            List<String> characters = findCharactersInPanel(panel);
            formatCharacterEntries(result, characters);

            // Add newline unless it's the last panel
            if (panelIndex < panels.getLength() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    private List<String> findCharactersInPanel(Element panel) {
        List<String> characters = new ArrayList<>();
        String[] positions = {"middle", "left", "right", "above", "below"};

        for (String position : positions) {
            NodeList positionElements = panel.getElementsByTagName(position);
            if (positionElements.getLength() > 0) {
                Element positionElement = (Element) positionElements.item(0);
                extractCharactersFromPosition(positionElement, characters);
            }
        }
        return characters;
    }

    private void extractCharactersFromPosition(Element positionElement, List<String> characters) {
        NodeList figures = positionElement.getElementsByTagName("figure");
        for (int i = 0; i < figures.getLength(); i++) {
            Element figure = (Element) figures.item(i);
            String characterName = getCharacterName(figure);
            if (characterName != null && !characters.contains(characterName)) {
                characters.add(characterName + "__");
            }
        }
    }

    private String getCharacterName(Element figure) {
        String name = getTextContentFromTag(figure, "name");
        if (name.isEmpty()) {
            name = getTextContentFromTag(figure, "id");
        }
        return name.isEmpty() ? null : name;
    }

    private String getTextContentFromTag(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent().trim() : "";
    }

    private void formatCharacterEntries(StringBuilder builder, List<String> characters) {
        for (int i = 0; i < characters.size(); i++) {
            if (i > 0) {
                builder.append("\t");
            }
            builder.append(characters.get(i));
        }
    }

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        StoryGenerator sg = new StoryGenerator(config);

        // Example usage:
        sg.loadXmlDocument("assets/story/specification.xml");
        String panelCharacters = sg.getCharactersByPanel(107);

        // Print the results
        System.out.println(panelCharacters);
    }
}
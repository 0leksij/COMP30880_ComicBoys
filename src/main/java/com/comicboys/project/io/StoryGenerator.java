package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class StoryGenerator {

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
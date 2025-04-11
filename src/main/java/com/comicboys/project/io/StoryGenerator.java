package com.comicboys.project.io;


import com.comicboys.project.client.APIClient;
import com.comicboys.project.client.APIResponse;
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

    public String generateSceneStory(int sceneIndex) {
        StringBuilder result = new StringBuilder();

        if (xmlDocument == null) {
            return "No XML document loaded. Call loadXmlDocument() first.";
        }

        NodeList scenes = xmlDocument.getElementsByTagName("scene");
        if (sceneIndex >= scenes.getLength()) {
            return "Scene index out of bounds.";
        }

        Element scene = (Element) scenes.item(sceneIndex);
        NodeList panelNodes = scene.getElementsByTagName("panel");

        for (int i = 1; i < panelNodes.getLength(); i++) {
            Element panel = (Element) panelNodes.item(i);
            StringBuilder panelDescription = new StringBuilder();

            // Get location from <above> or <setting>
            String location = getTextContent(panel, "above");
            if (location == null || location.isEmpty()) location = getTextContent(panel, "setting");
            location = (location != null && !location.isEmpty()) ? " in the " + location : "";

            List<Element> figureElements = getAllFiguresFromPanel(panel);
            List<String> characterDescriptions = new ArrayList<>();

            for (Element figure : figureElements) {
                String name = getTextContent(figure, "name");
                if (name == null) continue;

                StringBuilder charDescription = new StringBuilder(name);

                // Get character-specific balloon if exists
                Element balloon = getBalloonForFigure(panel, figure);
                String pose = getTextContent(figure, "pose");

                if (balloon != null) {
                    String balloonText = getTextContent(balloon, "content");
                    if (balloonText != null && !balloonText.isEmpty()) {
                        charDescription.append(" is ").append(balloonText).append(location);
                    }
                } else if (pose != null && !pose.isEmpty()) {
                    charDescription.append(" is ").append(pose).append(location);
                }

                characterDescriptions.add(charDescription.toString());
            }

            // Combine character descriptions
            if (!characterDescriptions.isEmpty()) {
                panelDescription.append(String.join(" ", characterDescriptions));
                panelDescription.append(".");
            }

            // Append <below> text if it exists
            String below = getTextContent(panel, "below");
            if (below != null && !below.isEmpty()) {
                panelDescription.append(" ").append(below);
            }

            // Add to result with panel number
            result.append(i).append(". ").append(panelDescription.toString().trim());

            // Add newline unless it's the last panel
            if (i < panelNodes.getLength() - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }

    List<Element> getAllFiguresFromPanel(Element panel) {
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

    String getTextContent(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() > 0) return nodes.item(0).getTextContent().trim();
        return null;
    }


    /// //////////////// Reys Story Generator
    private String filePath;
    private Document xmlDocument;
    private APIClient client;

    public StoryGenerator(APIClient client) {
        this.filePath = "assets/story/";
        this.client = client;
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

        for (int panelIndex = 1; panelIndex < panels.getLength(); panelIndex++) {
            Element panel = (Element) panels.item(panelIndex);
            result.append((panelIndex)).append(". ");

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


    Element getBalloonForFigure(Element panel, Element figure) {
        // Find the balloon that belongs to this specific figure
        NodeList balloons = panel.getElementsByTagName("balloon");
        for (int i = 0; i < balloons.getLength(); i++) {
            Element balloon = (Element) balloons.item(i);
            // Check if balloon is a direct sibling of the figure
            if (balloon.getParentNode().equals(figure.getParentNode())) {
                return balloon;
            }
        }
        return null;
    }

    public List<List<String>> generateStories() {
        List<List<String>> result = new ArrayList<>();

        if (xmlDocument == null) {
            System.err.println("No XML document loaded. Call loadXmlDocument() first.");
            return result;
        }

        NodeList scenes = xmlDocument.getElementsByTagName("scene");

        for (int sceneIndex = 0; sceneIndex < scenes.getLength(); sceneIndex++) {
            List<String> sceneResults = new ArrayList<>();

            try {
                // Build the prompt
                StringBuilder sb = new StringBuilder();
                sb.append("Here are the audio descriptions for scene ").append(sceneIndex+1).append(":\n");
                sb.append(generateSceneStory(sceneIndex));
                sb.append("\n\nHere are the characters in each panel:\n");
                sb.append("\n\nProvide JUST a numbered list of dialogue (with names in the format \"Alfie: *insert dialogue*\", separated with \"|\" if there are multiple characters do \"Alfie: *dialogue* | Betty: *dialogue*\"). Fill in the blanks for each character in the given order.");
                sb.append(getCharactersByPanel(sceneIndex));
                sb.append("\nDo not enclose the dialogue in \"\". I just want the plain text");
                sb.append("\nAlso after the dialogue, provide a very brief description of the panel. It should be separated from the dialogue with a |. E.g (Alfie: ... | Betty: ... | ...)");
                sb.append("\nDo not return the description in the format \"Description: ...\", just return plain text like | ... with no brackets. Always include the description");

                // Get API response with rate limiting handling
                APIResponse response = null;
                int retryCount = 0;
                while (retryCount < 3) { // Max 3 retries
                    try {
                        response = client.sendPrompt(sb.toString());
                        break;
                    } catch (Exception e) {
                        if (e.getMessage().contains("429") || e.getMessage().contains("Too Many Requests")) {
                            retryCount++;
                            if (retryCount >= 3) throw e;
                            System.out.println("Rate limited, retrying in " + (5000 * retryCount) + "ms...");
                            Thread.sleep(5000 * retryCount); // Exponential backoff (5s, 10s, 15s)
                        } else {
                            throw e; // Re-throw if not a rate limit error
                        }
                    }
                }

                // Process the response
                List<String> dialogues = processDialogueResponse(response);
                result.add(dialogues);

                // Add delay between scenes to avoid rate limiting
                if (sceneIndex < scenes.getLength() - 1) {
                    Thread.sleep(30000); // 30 second delay between scenes
                }
            } catch (Exception e) {
                System.err.println("Error processing scene " + (sceneIndex + 1) + ": " + e.getMessage());
                result.add(List.of("Error generating dialogue for this scene: " + e.getMessage()));
            }
        }

        return result;
    }



    List<String> processDialogueResponse(APIResponse response) {
        List<String> dialogues = new ArrayList<>();

        if (response.isNumberedList()) {
            // Process numbered list response
            for (String item : response.getNumberedList().getItems()) {
                // Remove the numbering prefix (e.g., "1. ")
                String dialogue = item.replaceAll("^\\d+\\.\\s*", "").trim();
                dialogues.add(dialogue);
            }
        } else {
            // Process plain text response by splitting lines
            String[] lines = response.getTextResponse().split("\n");
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    // Check if line starts with numbering pattern
                    if (line.matches("^\\d+\\.\\s+.*")) {
                        dialogues.add(line.replaceAll("^\\d+\\.\\s*", "").trim());
                    } else {
                        dialogues.add(line);
                    }
                }
            }
        }

        return dialogues;
    }

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        APIClient client = new APIClient(config);
        StoryGenerator sg = new StoryGenerator(client);

        // Example usage:
        sg.loadXmlDocument("assets/story/specification_short.xml");

        // Print the results
        List<List<String>> stories = sg.generateStories();

        // Print all stories
        for (int i = 0; i < stories.size(); i++) {
            System.out.println("Scene " + (i+1) + ":");
            for (String panel : stories.get(i)) {
                System.out.println(panel);
            }
            System.out.println();
        }

        // example:
        // a scene is accessed by stories.get(i)
        // a panel of a scene can be accessed by stories.get(i).get(j)

        System.out.println(stories.get(1).get(2));

    }
}
package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/*
    --[ EXAMPLE INPUT TO EXPLAIN PROCESS ]--

    // writeStories input example
    List<List<String>> stories =
    [
        [
            "Alfie: Hello! ",
            "Alfie: Hello everyone! | Betty: Hello! ",
            "Alfie: Goodbye. | Betty: Bye. | Some random description. "
        ],
        [
            "Alfie: I am so alone. "
        ],
        ...
    ]



    // iterating through XML file example for updating nodes in-place
    XML File:

    <>
    <>
    <scene>     --->    ELEMENT node!   --->    Go through each panel String in List<String>, split character dialogue
                                                at "|" to add balloon content or below text accordingly to string data
    <>
    <scene>     --->    ELEMENT node!
    <>




* */

public class StoryBlueprint extends Blueprint {
    public StoryBlueprint(String filePath) {
        super(filePath);
    }
    // takes in story generator, will use list generated from it to pass into other writeStory method
    public void writeStory(StoryGenerator storyGenerator, String fileName) {
        // loading XML file into story generator
        storyGenerator.loadXmlDocument(getFilePath());
        writeStory(storyGenerator.generateStories(), fileName);
    }
    // takes in a list of lists, so can also be used for unit tests directly instead of reading in XML file
    public void writeStory(List<List<String>> stories, String fileName) {
        // a reminder for how the strings are supposed to be formatted by AI, to help troubleshooting
        String characterDelimiter = "|";
        System.out.println("Split character dialogue based on " + characterDelimiter + " delimiter");
        // this will select all scenes from file document variable (not the cloned copy the just cleared of scenes)
        NodeList scenes = selectElements("scene");
        // need to separately keep track of current index in our XML DOM structure AND list of stories
        int currentNode, currentStory;
        currentNode = currentStory = 0;
        // doing for all scenes
        while (currentStory < stories.size()) {
            Node currentScene = scenes.item(currentNode);
            if (currentScene.getNodeType() == Node.ELEMENT_NODE) {
                // updates in-place the current scene
                updateScene(currentScene, stories.get(currentStory));
                // next story index
                currentStory++;
            }
            currentNode++;
            // since our while loop is based on our story length, need to prevent out of bounds if nodes do not match
            // (which they should given the story is generated for the same file)
            if (currentNode > scenes.getLength()) {
                System.out.println("EOF for XML reached -- no more nodes in story");
                break;
            }
        }
        // new file path
        String filePath = XMLFileManager.getFileDirectory(getFilePath()) + fileName;
        // save to save file path with given file name
        XMLFileManager.saveXMLToFile(getFile(), filePath);
        System.out.println("File written to " + filePath);
    }
    // update current scene
    private void updateScene(Node scene, List<String> newSceneData) {
        // remove all balloon elements (we will add completely new ones from newSceneData)
        List<String> tagsToRemove = List.of("balloon", "above");
        XMLFileManager.removeAllByTag(scene, tagsToRemove);
        // getting list of child nodes (panels) for this scene
        NodeList children = scene.getChildNodes();
        // loop variables, currentNode is node in our XML structure, currentNewPanel is the new panel data in our list,
        // these can be different sizes (because we have both element nodes and attribute nodes), whereas the new
        // panels we want are only data about new panels, no extra DOM stuff
        int currentNode, currentNewPanel;
        currentNode = currentNewPanel = 0;
        while (currentNewPanel < newSceneData.size()) {
            Node panel = children.item(currentNode);
            // if is element, i.e. is panel
            if (panel.getNodeType() == Node.ELEMENT_NODE) {
                // skip panel introducing scene
                if (panel.getTextContent().contains("Scene")) {
                    currentNode++;
                    continue;
                }
                // splitting panel text data for each figure and the below text
                List<String> panelTexts = List.of(newSceneData.get(currentNewPanel).split("\\|"));
                String belowText = "";
                // looking through text for each figure
                for (String figureText : panelTexts) {
                    // characters will have text as "Alfie: sample text. "
                    // so want to get the name (everything before ":")
                    int colonIndex = figureText.indexOf(":");
                    // if no dialogue, is a description, so add to below text of panel
                    if (colonIndex == -1) { belowText += figureText; }
                    else {
                        // must be dialogue for particular character (trim AFTER substring so colonIndex is correct)
                        String charName = figureText.substring(0, colonIndex).trim();
                        // grab dialogue for this character
                        String charContent = figureText.substring(colonIndex + 1).trim();
                        updatePanelCharacter(panel, charName, charContent);
                    }
                }
                // update <below> element content
                updatePanelBelowContent(panel, belowText);
                // move to next panel in current scene
                currentNewPanel++;
//                break; // for testing if only one panel works at a time
            }
            currentNode++;
        }
//        // -- DEBUG STUFF --
//        // the scene is updated and all <balloon> elements are removed
//        System.out.println(scene.getTextContent());
//        // as you can see, the file will NOT be modified, we are simply modifying the scene
//        System.out.println(file.getDocumentElement().getTextContent());
    }

    // given a panel and the name of a character in it, will update its content
    private void updatePanelCharacter(Node panel, String charName, String charContent) {
        // cast Node to XML Element and look through NodeList of <name> elements
        Element element = (Element) panel;
        NodeList names = element.getElementsByTagName("name");
        // going through all <name> nodes of current panel
        for (int j = 0; j < names.getLength(); j++) {
            Node name = names.item(j);
            if (name.getNodeType() == Node.ELEMENT_NODE) {
                // if name matches one we want to add dialogue for, need to add to its parent's parent
                if (name.getTextContent().contains(charName)) {
                    // parent of <name> would be <figure>, and parent of <figure> would be <left>, <middle> or <right>
                    // i.e. our character element
                    Node character = getCharacterParent(name);
                    // update character balloon (creates new balloon with given content)
                    updateBalloonContent(character, charContent);
                }
            }
        }
    }
    private void updateBalloonContent(Node character, String charContent) {
        // get document to create new element
        Document doc = character.getOwnerDocument();
        // create new balloon with text as whatever came after the colon, remove trailing spaces
        Element newBalloon = doc.createElement("balloon");
        newBalloon.setAttribute("status", "speech");
        // Create <content> inside <balloon>
        Element content = doc.createElement("content");
        content.setTextContent(charContent);
        newBalloon.appendChild(content);
        // Append the new balloon
        character.appendChild(newBalloon);
    }
    private void updatePanelBelowContent(Node panel, String belowText) {
        // first remove current below tag if exists
        String belowTag = "below";
        // remove below element of current panel (so always keeping only one below element)
        XMLFileManager.removeAllByTag(panel, belowTag);
        // if there is below text to add, add it
        if (!belowText.isEmpty()) {
            // create below element in document
            Document doc = panel.getOwnerDocument();
            Node newBelow = doc.createElement(belowTag);
            // set content
            newBelow.setTextContent(belowText);
            // add below element to child node
            panel.appendChild(newBelow);
        }
    }
    private Node getCharacterParent(Node name) {
        return XMLFileManager.getParent(XMLFileManager.getParent(name));
    }
}


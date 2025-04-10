package com.comicboys.project.io;

import com.comicboys.project.data.NumberedList;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class Blueprint{
    private final String filePath;
    private final Document file;
    // by default will want to select <comic> tag
    public Blueprint(String filePath) {
        this.filePath = filePath;
        file = XMLFileManager.loadXMLFromFile(this.filePath);
    }
    public String getFilePath() { return filePath; }
    public Document getFile() { return file; }
    // to select a certain tag
    private NodeList selectElements(String element) {
        return XMLFileManager.selectElements(file, element);
    }


    public List<String> getSpeechBalloons() {
        // want to translate all balloons
        NodeList nodeList = selectElements("balloon");
        List<String> phrases = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            // ensures is valid node
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                phrases.add(node.getTextContent().trim());
            }
        }
        return phrases;
    }

    public void writeStory(StoryGenerator storyGenerator) {
        // this will select all scenes from file document variable (not the cloned copy the just cleared of scenes)
        NodeList scenes = selectElements("scene");

        // loading XML file into story generator
        storyGenerator.loadXmlDocument(getFilePath());

        // get story data generated
        List<List<String>> stories = storyGenerator.generateStories();
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
        // used to get rid of "file.xml" from path and get directory of source file
        String fileDirectory = XMLFileManager.getFileDirectory(getFilePath());
        // save to save file path with given file name
        XMLFileManager.saveXMLToFile(getFile(), fileDirectory + "sample_story.xml");
        System.out.println("File written to " + fileDirectory);
    }

    // update current scene
    public void updateScene(Node scene, List<String> newSceneData) {
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
            Node child = children.item(currentNode);
            // if is element, i.e. is panel
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // skip panel introducing scene
                if (child.getTextContent().contains("Scene")) {
                    currentNode++;
                    continue;
                }
                // splitting panel text data for each figure and the below text
                List<String> panel = List.of(newSceneData.get(currentNewPanel).split("\t"));
                String belowText = "";
                // looking through text for each figure
                for (String figureText : panel) {
                    // characters will have text as "Alfie: sample text. "
                    // so want to get the name (everything before ":")
                    int colonIndex = figureText.indexOf(":");
                    // update character name (if exists, otherwise empty)
                    String charName;
                    if (colonIndex == -1) { charName = ""; }
                    else { charName = figureText.trim().substring(0, colonIndex); }
                    // if no dialogue, is a description, so add to below text of panel
                    if (charName == "") {
                        belowText += figureText;
                    } else {
                        // otherwise must be dialogue for character
                        Element element = (Element) child;
                        NodeList names = element.getElementsByTagName("name");
                        // going through all <name> nodes of current panel
                        for (int j = 0; j < names.getLength(); j++) {
                            Node name = names.item(j);
                            if (name.getNodeType() == Node.ELEMENT_NODE) {
                                // if name matches one we want to add dialogue for, need to add to its parent's parent
                                if (name.getTextContent().contains(charName)) {
                                    // parent of <name> would be <figure>
                                    if (name.getParentNode() != null) {
                                        Node figure = name.getParentNode();
                                        // parent of <figure> would be <left>, <middle> or <right>
                                        if (figure.getParentNode() != null) {
                                            // get document to create new element
                                            Document doc = figure.getOwnerDocument();
                                            // create new balloon with text as whatever came after the colon, remove trailing spaces
                                            Element newBalloon = doc.createElement("balloon");
                                            // set balloon status to be speech and set its text to be dialogue obtained
                                            newBalloon.setAttribute("status", "speech");
                                            newBalloon.setTextContent(figureText.substring(colonIndex + 1).trim());
                                            // add balloon as child of figure parent (will be either left, middle, right)
                                            figure.getParentNode().appendChild(newBalloon);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // first remove current below tag if exists
                String belowTag = "below";
                // remove below element of current panel (so always keeping only one below element)
                XMLFileManager.removeAllByTag(child, belowTag);
                // if there is below text to add, add it
                if (!belowText.isEmpty()) {
                    // create below element in document
                    Document doc = child.getOwnerDocument();
                    Node newBelow = doc.createElement(belowTag);
                    // set content
                    newBelow.setTextContent(belowText);
                    // add below element to child node
                    child.appendChild(newBelow);
                }
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

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        String storyPath = "assets/story/specification_shorter.xml";
        Blueprint blueprint = new Blueprint(storyPath);

        StoryGenerator sg = new StoryGenerator(config);
        blueprint.writeStory(sg);
    }


}

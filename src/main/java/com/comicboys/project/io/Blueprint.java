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

    // get folder path and not file path of source file for the document
    private String getBaseFilePath() {
        String sourceFilePath = getFilePath();
        int baseFilePathEndIndex = sourceFilePath.lastIndexOf("/");
        return sourceFilePath.substring(0, baseFilePathEndIndex + 1);
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

    public void generateStory() {
        // this will select all scenes from file document variable (not the cloned copy the just cleared of scenes)
        NodeList scenes = selectElements("scene");
        // set this to number of scenes to test, otherwise set it to scenes.getLength() for ALL scenes
        int max = 1;
        // doing for all scenes
        for (int i = 0; i < max; i++) {
            Node currentScene = scenes.item(i);
            if (currentScene.getNodeType() == Node.ELEMENT_NODE) {
                XMLFileManager.removeFirstChild(currentScene);
                List<String> scenesExample = List.of(
                        "Alfie: What the heck man. ",
                        "Alfie: Frick. \tBetty: Yeaheahea boiii. ",
                        "Alfie: Jesus Christ. \tBetty: The almighty. \tBelow is my chimmy chunga. "
                );
//                // how i will actually get the sceneData using Oleksii's code -- storyGenerator will be passed as argument
//                NumberedList sceneData = storyGenerator.generateStory(scenes.item(i));
                NumberedList sceneData = new NumberedList(scenesExample);
                // updates in-place the current scene
                update(currentScene, sceneData);
            }
        }
        // save to save file path with given file name
        XMLFileManager.saveXMLToFile(getFile(), getBaseFilePath() + "sample_story.xml");
        System.out.println("File written to " + getBaseFilePath());
    }

    // update current scene
    public void update(Node scene, NumberedList newSceneData) {
        // remove all balloon elements (we will add completely new ones from newSceneData)
        List<String> tagsToRemove = List.of("balloon", "above");
        XMLFileManager.removeAllByTag(scene, tagsToRemove);
        // getting list of child nodes (panels) for this scene
        NodeList children = scene.getChildNodes();
        // getting new scene as a list of strings from NumberedList data structure
        List<String> newScene = newSceneData.getItems();
        // loop variables, currentNode is node in our XML structure, currentNewPanel is the new panel data in our list,
        // these can be different sizes (because we have both element nodes and attribute nodes), whereas the new
        // panels we want are only data about new panels, no extra DOM stuff
        int currentNode, currentNewPanel;
        currentNode = currentNewPanel = 0;
        while (currentNewPanel < newScene.size()) {
            Node child = children.item(currentNode);
            // if is element, i.e. is panel
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // splitting panel text data for each figure and the below text
                List<String> panel = List.of(newScene.get(currentNewPanel).split("\t"));
                String belowText = "";
                // looking through text for each figure
                for (String figureText : panel) {
                    // characters will have text as "Alfie: sample text. "
                    // so want to get the name (everything before ":")
                    int colonIndex = figureText.indexOf(":");
                    // update character name (if exists, otherwise empty)
                    String charName;
                    if (colonIndex == -1) { charName = ""; }
                    else { charName = figureText.substring(0, colonIndex); }
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
                                            Node newBalloon = doc.createElement("balloon");
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
                // create below element in document
                Document doc = child.getOwnerDocument();
                Node newBelow = doc.createElement("below");
                newBelow.setTextContent(belowText);
                // add below element to child node
                child.appendChild(newBelow);
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
        String storyPath = "assets/story/";
//        Blueprint blueprint = new Blueprint(storyPath + "specification_short.xml");
        Blueprint blueprint = new Blueprint(storyPath + "specification_test.xml");
        blueprint.generateStory();
    }


}

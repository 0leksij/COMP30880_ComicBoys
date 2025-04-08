package com.comicboys.project.io;

import com.comicboys.project.data.NumberedList;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;

public class Blueprint implements XMLFileManager{
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

    public void generateStory() {
        NodeList scenes = selectElements("scene");

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
                update(currentScene, sceneData);


            }


        }

    }


    public void update(Node scene, NumberedList newSceneData) {
        // remove all balloon elements (we will add completely new ones from newSceneData)
        List<String> tagsToRemove = List.of("balloon", "above");
        XMLFileManager.removeAllByTag(scene, tagsToRemove);
        // the scene is updated and all <balloon> elements are removed
        System.out.println(scene.getTextContent());
//        // as you can see, the file will NOT be modified, we are simply modifying the scene
//        System.out.println(file.getDocumentElement().getTextContent());
    }

    public static void main(String[] args) {
        String storyPath = "assets/story/";
        Blueprint blueprint = new Blueprint(storyPath + "specification_short.xml");
        blueprint.generateStory();
    }


}

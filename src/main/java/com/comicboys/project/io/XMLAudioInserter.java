package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLAudioInserter extends Blueprint {


    public XMLAudioInserter(String filePath) {
        super(filePath);
    }


    public void separateSpeech() {
        Document doc = getFile();
        NodeList scenes = XMLFileManager.selectElements(doc, "scene");
        // for each <scene> element
        for (int i = 0; i < scenes.getLength(); i++) {
            Node scene = scenes.item(i);
            if (scene.getNodeType() == Node.ELEMENT_NODE) {
                NodeList panels = ((Element) scene).getElementsByTagName("panel");
                // look through all <panel> elements
                for(int j = 0; j < panels.getLength(); j++) {
                    Node panel = panels.item(j);
                    // ensure is valid element node
                    if (panel.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
                        int balloonCount = countBalloonElements(balloons);
                        // if more than 1 balloon, must split panel
                        if (balloonCount > 1) {
                            splitPanel(panel);
                            break;
                        }
                    }
                }
            }
        }
        // save edited document to XML file
        String fileDirectory = XMLFileManager.getFileDirectory(getFilePath());
        String outputFilePath = fileDirectory + "sample_panel_split.xml";
        XMLFileManager.saveXMLToFile(getFile(), outputFilePath);
        System.out.println("Split panel XML saved in " + outputFilePath);
    }

    private void splitPanel(Node secondPanel) {
        // cloning what will be second panel, because there is only an insertBefore method
        Node firstPanel = secondPanel.cloneNode(true);
        secondPanel.getParentNode().insertBefore(firstPanel, secondPanel);
        // want to keep first balloon in first panel and second balloon in second panel
        removeSecondBalloon(firstPanel);
        removeFirstBalloon(secondPanel);
//        // print to terminal for quick debugging to check if split properly
//        System.out.println(firstPanel.getTextContent());
//        System.out.println(secondPanel.getTextContent());
    }
    // removes first balloon child of node
    private static void removeFirstBalloon(Node panel) {
        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
        XMLFileManager.removeFirstChild(balloons);
    }
    // removes 2nd balloon child of node
    private static void removeSecondBalloon(Node panel) {
        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
        XMLFileManager.removeNthChild(balloons, 2);
    }
    // function to quickly count how many balloon elements in NodeList (because selected ones may not all be valid elements)
    private int countBalloonElements(NodeList elements) {
        int balloons = 0;
        for (int j = 0; j < elements.getLength(); j++) {
            Node balloon = elements.item(j);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                balloons++;
            }
        }
        return balloons;
    }



    public static void main(String[] args) {

//        // one panel, one balloon (should not change anything)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_one_panel_one_character.xml");
//        // one panel, two balloons (should split into two panels for each balloon respectively)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_one_panel_two_characters.xml");
//        // two panels, split from one (should not change anything as already only one balloon per panel)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_panels_split_from_one_two_characters.xml");
//        // two panels, one has two balloons, another has one, should end up with 3 panels (first two relate to original first)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_panels_mixed_balloons.xml");
        // two scenes, each has two panels, same as mixed balloons for two panels, just testing with multiple scenes instead
        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_scenes_mixed_balloons.xml");
        audioInserter.separateSpeech();
    }

}

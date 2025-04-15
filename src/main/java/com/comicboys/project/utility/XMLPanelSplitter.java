package com.comicboys.project.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// utility interface that goes through XML file where every 2 balloon panel is replaced with 2 panels, each 1 balloon
public interface XMLPanelSplitter {
    // iterates through all scenes in document, checking if each panel has more than 1 balloon
    static void separateMultipleSpeechPanels(Document doc) {
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
                        // count how many balloon children of panel element
                        int balloonCount = XMLFileManager.countElements(panel, "balloon");
                        // if more than 1 balloon, must split panel
                        if (balloonCount > 1) {
                            splitPanel(panel);
                            break;
                        }
                    }
                }
            }
        }
    }

    static void splitPanel(Node secondPanel) {
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
    static void removeFirstBalloon(Node panel) {
        // need to grab balloons again in each remove method because node that is cloned will have different balloon
        // nodes, otherwise would be changing same original balloon nodes and cloned node would remain unchanged
        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
        XMLFileManager.removeFirstChild(balloons);
    }
    // removes 2nd balloon child of node
    static void removeSecondBalloon(Node panel) {
        NodeList balloons = ((Element) panel).getElementsByTagName("balloon");
        XMLFileManager.removeNthChild(balloons, 2);
    }
}

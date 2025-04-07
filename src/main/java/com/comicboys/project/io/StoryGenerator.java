package com.comicboys.project.io;

import com.comicboys.project.data.NumberedList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class StoryGenerator {
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
                }
                else if (pose != null && !pose.isEmpty()) {
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
}

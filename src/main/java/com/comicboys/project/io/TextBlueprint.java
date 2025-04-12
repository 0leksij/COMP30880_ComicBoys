package com.comicboys.project.io;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class TextBlueprint extends Blueprint {
    public TextBlueprint(String filePath) {
        super(filePath);
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
    public List<String> getBelowTexts() {
        NodeList nodeList = selectElements("below");
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String text = node.getTextContent().trim();
                if (!text.isEmpty()) {
                    texts.add(text);
                }
            }
        }
        return texts;
    }
}

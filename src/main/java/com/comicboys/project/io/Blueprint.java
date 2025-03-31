package com.comicboys.project.io;

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
    private NodeList selectElement(String element) {
        return XMLFileManager.selectElement(file, element);
    }


    public List<String> getSpeechBalloons() {
        // want to translate all balloons
        NodeList nodeList = selectElement("balloon");
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



    public static void main(String[] args) {
        Blueprint blueprint = new Blueprint("assets/blueprint/specification.xml");
        System.out.println(blueprint.getSpeechBalloons());

    }
}

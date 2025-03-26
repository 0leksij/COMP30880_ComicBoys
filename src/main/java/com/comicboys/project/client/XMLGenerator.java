package com.comicboys.project.client;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class XMLGenerator {

    public static String generateXML(StringEntry entry) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Root element
            Element rootElement = doc.createElement("comicLesson");
            doc.appendChild(rootElement);

            // Scene element
            Element sceneElement = doc.createElement("scene");
            rootElement.appendChild(sceneElement);

            // Left character pose
            Element leftPose = doc.createElement("leftPose");
            leftPose.appendChild(doc.createTextNode(entry.getLeftPose()));
            sceneElement.appendChild(leftPose);

            // Right character pose (optional)
            if (entry.getRightPose() != null && !entry.getRightPose().isEmpty()) {
                Element rightPose = doc.createElement("rightPose");
                rightPose.appendChild(doc.createTextNode(entry.getRightPose()));
                sceneElement.appendChild(rightPose);
            }

            // Text (either combined or left text)
            Element textElement = doc.createElement("text");
            textElement.appendChild(doc.createTextNode(entry.getCombinedText().isEmpty() ? entry.getLeftText() : entry.getCombinedText()));
            sceneElement.appendChild(textElement);

            // Background suggestion
            Element background = doc.createElement("background");
            background.appendChild(doc.createTextNode(entry.getBackgrounds()));
            sceneElement.appendChild(background);

            // Convert to String
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<error>XML Generation Failed</error>";
        }
    }

    public static void main(String[] args) {
        // Example usage:
        StringEntry exampleEntry = new StringEntry("standing", "Hello there!", "Hi!", "waving", "park");
        System.out.println(generateXML(exampleEntry));
    }
}

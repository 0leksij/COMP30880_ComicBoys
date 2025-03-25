package com.comicboys.project.client;

import com.comicboys.project.client.IVignetteSchema;
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
import java.util.List;


public class XMLGenerator {

    public static String generateXML(VignetteSchemaPlaceholder schema) {
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
            leftPose.appendChild(doc.createTextNode(String.join(", ", schema.getLeftPoses())));
            sceneElement.appendChild(leftPose);

            // Right character pose (optional)
            if (!schema.getRightPoses().isEmpty()) {
                Element rightPose = doc.createElement("rightPose");
                rightPose.appendChild(doc.createTextNode(String.join(", ", schema.getRightPoses())));
                sceneElement.appendChild(rightPose);
            }

            // Text (either combined or left text)
            Element textElement = doc.createElement("text");
            textElement.appendChild(doc.createTextNode(String.join(", ", schema.getCombinedText().isEmpty() ?
                    schema.getLeftText() : schema.getCombinedText())));
            sceneElement.appendChild(textElement);

            // Setting suggestion
            Element setting = doc.createElement("setting");
            setting.appendChild(doc.createTextNode(String.join(", ", schema.getSettingSuggestions())));
            sceneElement.appendChild(setting);

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
}

package com.comicboys.project.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

public interface XMLFileManager{


    static boolean saveXMLToFile(Document doc, String filePath) {
        try {
            trimWhitespace(doc.getDocumentElement());

            File file = new File(filePath);

            // Ensure the directory exists
            file.getParentFile().mkdirs();
            // Set up the transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));

            String xmlContent = stringWriter.toString();



            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(xmlContent);
            fileWriter.close();

            return true;

//            System.out.println("XML saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving XML file.");
            return false;
        }
    }
    static Document loadXMLFromFile(String filePath) {
        // load XML file
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // default element is <comic>
    static NodeList selectElement(Document doc) {
        return doc.getElementsByTagName("comic");
    }
    // specify element to pick
    static NodeList selectElement(Document doc, String element) {
        return doc.getElementsByTagName(element);
    }


    private static void trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                trimWhitespace(child);
            }
        }
    }


}

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


    static void saveXMLToFile(String xmlContent, String filePath) {
        try {
            File file = new File(filePath);

            // Ensure the directory exists
            file.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(file);
            writer.write(xmlContent);
            writer.close();

            System.out.println("XML saved successfully to: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving XML file.");
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

}

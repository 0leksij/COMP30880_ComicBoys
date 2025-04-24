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
import java.util.List;

public interface XMLFileManager extends XMLNodeRemover {


    // get directory folder is in
    static String getFileDirectory(String filePath) {
        int baseFilePathEndIndex = filePath.lastIndexOf("/");
        return filePath.substring(0, baseFilePathEndIndex + 1);
    }

    static void separateMultipleSpeechPanels(Document doc) { XMLPanelSplitter.separateMultipleSpeechPanels(doc); }

    static Document createFile(String filePath) {
        // create base file
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element comicElement = doc.createElement("comic");
            doc.appendChild(comicElement);

            Element figuresElement = doc.createElement("figures");
            comicElement.appendChild(figuresElement);
            Element scenesElement = doc.createElement("scenes");
            comicElement.appendChild(scenesElement);

            return doc;
        } catch (Exception e) {
            System.out.println("Error: File in path " + filePath + " does not exist or failed to load");
            e.printStackTrace();
            return null;
        }
    }

    static void appendScenes(Document doc, NodeList scenes) {
        trimWhitespace(doc.getDocumentElement());
        NodeList scenesElements = doc.getDocumentElement().getElementsByTagName("scenes");
        // go through all scene nodes
        for (int i = 0; i < scenesElements.getLength(); i++) {
            Node scene = scenesElements.item(i);
            // if is valid scene node, append all scenes in list to this document
            if (scene.getNodeType() == Node.ELEMENT_NODE) {
                for (int j = 0; j < scenes.getLength(); j++) {
                    Node currentScene = scenes.item(j);
                    scene.appendChild(doc.adoptNode(currentScene));
                }
                // only one scenes element to add to, so break
                break;
            }
        }
    }

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
            System.out.println("Error: File in path " + filePath + " does not exist or failed to load");
            e.printStackTrace();
            return null;
        }
    }
    static NodeList selectElements(Document doc) {
        return doc.getElementsByTagName("comic");
    }
    static NodeList selectElements(Document doc, String element) {
        return doc.getElementsByTagName(element);
    }
    static int countElements(Node node, String elementTag) {
        NodeList elements = ((Element) node).getElementsByTagName(elementTag);
        return countElements(elements, elementTag);
    }
    static int countElements(NodeList elements, String childTag) {
        int count = 0;
        for (int j = 0; j < elements.getLength(); j++) {
            Node element = elements.item(j);
            if (element.getNodeType() == Node.ELEMENT_NODE) {
                count++;
            }
        }
        return count;
    }
    static void removeAllByTag(Node node, String childTagToRemove) { XMLNodeRemover.removeAllByTag(node, childTagToRemove); }
    static void removeAllByTag(Node node, List<String> childrenTagsToRemove) { XMLNodeRemover.removeAllByTag(node, childrenTagsToRemove); }
    static void removeFirstChild(Node node) { XMLNodeRemover.removeFirstChild(node); }
    static void removeFirstChild(NodeList children) { XMLNodeRemover.removeFirstChild(children); }
    static void removeNthChild(Node node, int nthChild) { XMLNodeRemover.removeNthChild(node, nthChild); }
    static void removeNthChild(NodeList children, int nthChild) { XMLNodeRemover.removeNthChild(children, nthChild); }
    static void removeAllChildren(Node node) { XMLNodeRemover.removeAllChildren(node); }
    static void removeAllChildren(NodeList children) { XMLNodeRemover.removeAllChildren(children); }
    static void removeNthChildren(Node node, int numOfChildrenToRemove, int nthChild) { XMLNodeRemover.removeNthChildren(node, numOfChildrenToRemove, nthChild); }
    static void removeNthChildren(NodeList children, int numOfChildrenToRemove, int nthChild) { XMLNodeRemover.removeNthChildren(children, numOfChildrenToRemove, nthChild); }
    // general method to get parent
    static Node getParent(Node node) {
        if (node == null) { return null; }
        if (node.getParentNode() == null) { return null; }
        return node.getParentNode();
    }
    // trims excessive whitespace in text content of XML file
    static void trimWhitespace(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            // if is text, trim
            if (child.getNodeType() == Node.TEXT_NODE) {
                child.setTextContent(child.getTextContent().trim());
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                // recursive call to trim whitespace in children
                trimWhitespace(child);
            }
        }
    }

    static Document loadXMLFromString(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML from string", e);
        }
    }


}

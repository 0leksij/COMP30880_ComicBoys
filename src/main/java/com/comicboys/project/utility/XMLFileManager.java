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

public interface XMLFileManager{


    // get directory folder is in
    static String getFileDirectory(String filePath) {
        int baseFilePathEndIndex = filePath.lastIndexOf("/");
        return filePath.substring(0, baseFilePathEndIndex + 1);
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
    // default element is <comic>
    static NodeList selectElements(Document doc) {
        return doc.getElementsByTagName("comic");
    }
    // specify element to pick
    static NodeList selectElements(Document doc, String element) {
        return doc.getElementsByTagName(element);
    }
    // method overloading, single string case: pass string as list into same named method with different signature
    static void removeAllByTag(Node node, String childTagToRemove) {
        removeAllByTag(node, List.of(childTagToRemove));
    }
    // specify which element tags to remove
    static void removeAllByTag(Node node, List<String> childrenTagsToRemove) {
        Element element = (Element) node;
        for (String childTagToRemove : childrenTagsToRemove) {
            NodeList children = element.getElementsByTagName(childTagToRemove);
            removeAllChildren(children);
        }
    }
    // remove first child element of a node
    static void removeFirstChild(Node node) {
        Element element = (Element) node;
        NodeList children = element.getChildNodes();
        removeNChildren(children, 1);
    }
    // remove all children elements of a node
    static void removeAllChildren(NodeList children) {
        removeNChildren(children, -1); // negative numbers means all children removed
    }
    // main remove element method, removes all nodes in list from their respective parent
    // if numChildrenToRemove < 0, will remove all children
    private static void removeNChildren(NodeList children, int numChildrenToRemove) {
        // look through children in ascending order
        int currentLength = children.getLength(); // list size will be changed throughout
        int i = 0; // loop variable
        int childrenRemoved = 0; // children removed so far
        // since modifying list size, mutable upper bound variable (updated in loop)
        while (i < currentLength) {
            Node child = children.item(i);
            // check is element node
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // if number of children to be removed is reached
                if (childrenRemoved == numChildrenToRemove) break;
                // check parent exists
                if (child.getParentNode() != null) {
                    // remove child from parent
                    child.getParentNode().removeChild(child);
                    // decrement i so does not skip next element, list length also decremented since item removed
                    i--;
                    currentLength--;
                    // increment children removed
                    childrenRemoved++;
                }
            }
            // next child
            i++;
        }
    }
    // trims excessive whitespace in text content of XML file
    private static void trimWhitespace(Node node) {
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


}

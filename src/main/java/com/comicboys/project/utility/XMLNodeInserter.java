package com.comicboys.project.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XMLNodeInserter {
    // inserts newChild to be first element child of current node
    static void insertFirstChild(Node node, Node newChild) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getParentNode() != null) {
                    Node importedNewChild = child.getOwnerDocument().importNode(newChild, true);
                    child.getParentNode().insertBefore(importedNewChild, child);
                    break;
                }
            }
        }
    }
    // removes node from one document and appends a node to the end of child list of the first node of a particular tag
    static void appendElement(Document doc, Node newNode, String tag) {
        Node node = validateElement(doc, tag);
        if (node == null) {
            System.out.printf("\nERROR: Failed to append node, <%s> is null\n", tag);
            return;
        }
        node.appendChild(doc.adoptNode(newNode));
    }
    // same as above method but instead of just appending removing and appending one node, removes and appends a list
    static void appendElements(Document doc, NodeList newNodes, String tag) {
        Node node = validateElement(doc, tag);
        if (node == null) {
            System.out.printf("\nERROR: Failed to append nodes, <%s> is null\n", tag);
            return;
        }
        for (int j = 0; j < newNodes.getLength(); j++) {
            Node currentScene = newNodes.item(j);
            node.appendChild(doc.adoptNode(currentScene));
        }
    }
    // short-form, less general methods that pass arguments into general one (specifically for our project)
    static void appendScenes(Document doc, Node newScene) {
        appendElement(doc, newScene, "scenes");
    }
    static void appendScenes(Document doc, NodeList newScenes) {
        appendElements(doc, newScenes, "scenes");
    }
    // boilerplate code to ensure an element of this tag exists, and returns first one it finds
    static Node validateElement(Document doc, String tag) { return XMLFileManager.validateElement(doc, tag); }
}

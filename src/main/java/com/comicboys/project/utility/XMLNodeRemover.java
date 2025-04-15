package com.comicboys.project.utility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public interface XMLNodeRemover {
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
        // remove up to 1 child of node
        removeNChildren(node, 1);
    }
    // remove first child element from a list of specified children
    static void removeFirstChild(NodeList children) {
        removeNChildren(children, 1);
    }
    // remove all children elements of a node
    static void removeAllChildren(Node node) {
        removeNChildren(node.getChildNodes(), -1); // negative numbers means all children removed
    }
    // remove all children elements from a list of children
    static void removeAllChildren(NodeList children) {
        removeNChildren(children, -1); // negative numbers means all children removed
    }
    // remove nth child of a node (i.e. nthChild=2 should remove 2nd child)
    static void removeNthChild (Node node, int nthChild) {
        // remove child nodes of node, starting from nth child, remove only 1
        removeNthChildren(node, nthChild, 1);
    }
    // remove nth child from a specified list (i.e. nthChild=2 should remove the second element node in the list from
    // its respective parent, which may be a different parent to other children in the list)
    static void removeNthChild (NodeList children, int nthChild) {
        // remove child nodes of node, starting from nth child, remove only 1
        removeNthChildren(children, nthChild, 1);
    }
    // can remove N children of particular node
    static void removeNChildren(Node node, int numChildrenToRemove) {
        Element element = (Element) node;
        NodeList children = element.getChildNodes();
        removeNChildren(children, numChildrenToRemove);
    }
    // remove N children from list of specified children nodes
    static void removeNChildren(NodeList children, int numChildrenToRemove) {
        removeNthChildren(children, 1, numChildrenToRemove);
    }
    // remove children of a node starting from a particular child node (i.e. nthChild=2, numChildrenToRemove=2 should
    // remove the 2nd and 3rd node, because starting from 2nd node, and removing 2 nodes, so will stop after 3rd)
    static void removeNthChildren(Node node, int nthChild, int numChildrenToRemove) {
        Element element = (Element) node;
        NodeList children = element.getChildNodes();
        removeNthChildren(children, nthChild, numChildrenToRemove);
    }
    // main remove element method, removes all nodes in list from their respective parent starting from particular child
    // if numChildrenToRemove < 0, will remove all children
    static void removeNthChildren(NodeList children, int nthChild, int numChildrenToRemove) {
        // keep track of current child position up to nth
        int currentChild = 1;
        // look through children in ascending order
        int currentLength = children.getLength(); // list size will be changed throughout
        int i = 0; // loop variable
        int childrenRemoved = 0; // children removed so far
        // if not a valid child position remove nothing (by preventing while loop)
        if (nthChild < 1) {
            System.out.println("nthChild must be at least 1 (indicating first child), you inputted: " + nthChild);
            i = currentLength;
        }
        // since modifying list size, mutable upper bound variable (updated in loop)
        while (i < currentLength) {
            Node child = children.item(i);
            // check is element node
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // if not currently at child to start removing skip to next loop iteration
                if (currentChild >= nthChild) {
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
                currentChild++;
            }
            // next child
            i++;
        }
    }
}

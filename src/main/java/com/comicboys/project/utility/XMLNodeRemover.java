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
}

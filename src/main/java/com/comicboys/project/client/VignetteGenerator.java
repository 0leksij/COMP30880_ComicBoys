//package com.comicboys.project.client;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//
//public class XMLGenerator {
//
//    public static Document generateComicXML(VignetteSchemaPlaceholder vignette) {
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//            Document document = builder.newDocument();
//
//            // Root element
//            Element root = document.createElement("comicLesson");
//            document.appendChild(root);
//
//            // Scene element
//            Element scene = document.createElement("scene");
//            root.appendChild(scene);
//
//            // Left character
//            Element leftCharacter = document.createElement("leftCharacter");
//            leftCharacter.setAttribute("pose", vignette.getRandomLeftPose());
//            scene.appendChild(leftCharacter);
//
//            // Right character (if available)
//            String rightPose = vignette.getRandomRightPose();
//            if (rightPose != null && !rightPose.isEmpty()) {
//                Element rightCharacter = document.createElement("rightCharacter");
//                rightCharacter.setAttribute("pose", rightPose);
//                scene.appendChild(rightCharacter);
//            }
//
//            // Background
//            Element background = document.createElement("background");
//            background.setAttribute("type", vignette.getRandomBackground());
//            scene.appendChild(background);
//
//            // Dialogue
//            Element dialogue = document.createElement("dialogue");
//            dialogue.setTextContent(vignette.getRandomCombinedText());
//            scene.appendChild(dialogue);
//
//            return document;
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}

package com.comicboys.project.io;

import com.comicboys.project.Main;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.data.Mappings;
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
import java.util.Random;

public class XMLGenerator {

    private final Mappings mappings;
    private String consistentBackground = null;

    // Constructor that initializes mappings
    public XMLGenerator(Mappings mappings) {
        this.mappings = mappings;
    }



    public String generateXML(StringEntry entry) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();


            Element rootElement = doc.createElement("comic");  // Root <comic> element
            doc.appendChild(rootElement);


            for (int i = 1; i <= 4; i++) {
                rootElement.appendChild(createScene(doc, entry, i));
            }

            // Root <comic> element
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

    private Element createScene(Document doc, StringEntry entry, int sceneNumber) {
        Element scene = doc.createElement("scene");


        Element above = doc.createElement("above");
        scene.appendChild(above);


        //boolean leftGetsBalloon =true;
        boolean leftGetsBalloon = Main.random.nextBoolean();// Randomly decide if left or right gets the speech balloon

        // Left character
        Element left = doc.createElement("left");
        Element leftFigure = doc.createElement("figure");
        String leftPose = getValidPose(entry.getLeftPose());
        Element leftPoseElement = doc.createElement("pose");
        leftPoseElement.appendChild(doc.createTextNode(leftPose));
        leftFigure.appendChild(leftPoseElement);

        Element leftFacing = doc.createElement("facing");
        leftFacing.appendChild(doc.createTextNode("right"));
        leftFigure.appendChild(leftFacing);

        left.appendChild(leftFigure);
        if (leftGetsBalloon) left.appendChild(createEmptyBalloon(doc));

        scene.appendChild(left);

        // Right character
        Element right = doc.createElement("right");
        Element rightFigure = doc.createElement("figure");
        String rightPose = getValidPose(entry.getRightPose());
        Element rightPoseElement = doc.createElement("pose");
        rightPoseElement.appendChild(doc.createTextNode(rightPose));
        rightFigure.appendChild(rightPoseElement);

        Element rightFacing = doc.createElement("facing");
        rightFacing.appendChild(doc.createTextNode("left"));
        rightFigure.appendChild(rightFacing);

        right.appendChild(rightFigure);
        if (!leftGetsBalloon) right.appendChild(createEmptyBalloon(doc));

        scene.appendChild(right);

        // Background setting
        String background = getConsistentBackground(entry.getBackgrounds(), sceneNumber);
        //String background = getValidBackground(entry.getBackgrounds());
        Element setting = doc.createElement("setting");
        setting.appendChild(doc.createTextNode(background));
        scene.appendChild(setting);

        return scene;
    }

    private static Element createEmptyBalloon(Document doc) {
        Element balloon = doc.createElement("balloon");
        balloon.setAttribute("status", "speech");

        Element content = doc.createElement("content");  // Empty content
        balloon.appendChild(content);

        return balloon;
    }

    private String getValidPose(String pose) {
        if (mappings.isEmpty()) return pose;
        return mappings.getAllLeftPoses().contains(pose) ? pose : getRandomPose();
    }

    private String getValidBackground(String background) {
        if (mappings.isEmpty()) return background;
        return mappings.getAllBackgrounds().contains(background) ? background : getRandomBackground();
    }

    private String getRandomPose() {
        List<String> poses = mappings.getAllLeftPoses();
        return poses.get(Main.random.nextInt(poses.size()));
    }

    private String getRandomBackground() {
        List<String> backgrounds = mappings.getAllBackgrounds();
        return backgrounds.get(Main.random.nextInt(backgrounds.size()));
    }


    private String getConsistentBackground(String background, int sceneNumber) {
        if (sceneNumber == 1) consistentBackground = getValidBackground(background);
        return consistentBackground;
    }

    //public static void main(String[] args) {
      //      //StringEntry exampleEntry = new StringEntry("standing", "", "Let's go!", "waving", "park");
        //    StringEntry exampleEntry = new StringEntry();
         //   System.out.println(generateXML(exampleEntry));
        //}
    }









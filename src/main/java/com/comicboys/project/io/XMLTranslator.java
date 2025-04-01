package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class XMLTranslator {
    private final ConfigurationFile config;
    private final TranslationGenerator translationGenerator;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final String outputFilePath;

    public XMLTranslator(ConfigurationFile config, Mappings mappings) {
        this.config = config;
        this.translationGenerator = new TranslationGenerator(config, mappings);
        this.sourceLanguage = config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        this.targetLanguage = config.getProperty("TARGET_LANGUAGE").toLowerCase();
        this.outputFilePath = "assets/blueprint/" + sourceLanguage + "-to-" + targetLanguage + "-conjuction-lesson.xml";

        // Ensure translations are generated before proceeding
        translationGenerator.generateTranslations();
    }

    public void translateXML(String inputFilePath) {
        try {
            // First get all speech balloons from the XML
            Blueprint blueprint = new Blueprint(inputFilePath);
            List<String> speechBalloons = blueprint.getSpeechBalloons();

            // Generate translations for these balloons
            translationGenerator.generateTranslations(speechBalloons);

            // Load the original XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(inputFilePath));

            // Get all panels that contain speech balloons
            NodeList panels = doc.getElementsByTagName("panel");

            // Process each panel to add translated versions
            for (int i = 0; i < panels.getLength(); i++) {
                Node panel = panels.item(i);
                if (panel.getNodeType() == Node.ELEMENT_NODE) {
                    Element panelElement = (Element) panel;

                    // Check if this panel has speech balloons
                    NodeList balloons = panelElement.getElementsByTagName("balloon");
                    if (balloons.getLength() > 0) {
                        // Create a copy of the panel for translation
                        Element translatedPanel = (Element) panelElement.cloneNode(true);

                        // Translate all balloons in the copied panel
                        translateBalloons(translatedPanel);

                        // Insert the translated panel after the original
                        panel.getParentNode().insertBefore(translatedPanel, panel.getNextSibling());
                        i++; // Skip the panel we just added to avoid infinite loop
                    }
                }
            }

            // Save the translated XML to a new file
            saveTranslatedXML(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translateBalloons(Element panel) {
        NodeList balloons = panel.getElementsByTagName("balloon");
        Map<String, String> translations = translationGenerator.getTranslations();

        for (int i = 0; i < balloons.getLength(); i++) {
            Node balloon = balloons.item(i);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                Element balloonElement = (Element) balloon;
                NodeList contents = balloonElement.getElementsByTagName("content");
                if (contents.getLength() > 0) {
                    Element contentElement = (Element) contents.item(0);
                    String originalText = contentElement.getTextContent();

                    // Get translation (or use original if translation not found)
                    String translatedText = translations.getOrDefault(originalText, originalText);
                    contentElement.setTextContent(translatedText);
                }
            }
        }
    }

    private void saveTranslatedXML(Document doc) throws Exception {
        // Ensure directory exists
        File directory = new File("assets/blueprint");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Normalize the document to remove excessive whitespace
        doc.getDocumentElement().normalize();
        trimWhitespace(doc.getDocumentElement());

        // Set up the transformer
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");

        // Write to file
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputFilePath));
        transformer.transform(source, result);

        System.out.println("Translated XML saved to: " + outputFilePath);
    }

    private void trimWhitespace(Node node) {
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



    public String getOutputFilePath() {
        return outputFilePath;
    }

    public static void main(String[] args) {
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsReader = new MappingsFileReader();
        Mappings mappings = mappingsReader.getMappings();

        XMLTranslator translator = new XMLTranslator(config, mappings);
        translator.translateXML("assets/blueprint/specification.xml");

        System.out.println("Translated file saved to: " + translator.getOutputFilePath());
    }
}
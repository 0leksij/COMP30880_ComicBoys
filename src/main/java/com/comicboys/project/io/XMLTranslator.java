package com.comicboys.project.io;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.*;
import java.util.List;
import java.util.Map;

public class XMLTranslator {
    private final ConfigurationFile config;
    private final TranslationGenerator translationGenerator;
    private final String sourceLanguage;
    private final String targetLanguage;
    private final String filePath;
    private final String lessonType;

    public XMLTranslator(ConfigurationFile config, APIClient client, Mappings mappings, String lessonType) {
        this.config = config;
        this.translationGenerator = new TranslationGenerator(this.config, client, mappings);
        this.sourceLanguage = this.config.getProperty("SOURCE_LANGUAGE").toLowerCase();
        this.targetLanguage = this.config.getProperty("TARGET_LANGUAGE").toLowerCase();
        this.filePath = "assets/story/";
        this.lessonType = lessonType;

        // Ensure translations are generated before proceeding
        translationGenerator.generateTranslations(mappings.getAllTextFragments());
    }

    public boolean translateXML(String inputFileName) {
        try {
            String inputFilePath = filePath + inputFileName;
            // First get all speech balloons from the XML
            TextBlueprint blueprint = new TextBlueprint(inputFilePath);
            List<String> speechBalloons = blueprint.getSpeechBalloons();
            List<String> belowTexts = blueprint.getBelowTexts();

            // Generate translations for these balloons
            translationGenerator.generateTranslations(speechBalloons);
            translationGenerator.generateTranslations(belowTexts);

            // Load the original XML file
            Document doc = XMLFileManager.loadXMLFromFile(inputFilePath);

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

                        // Translate all balloons and below texts in the copied panel
                        translatePanel(translatedPanel);

                        // Insert the translated panel after the original
                        panel.getParentNode().insertBefore(translatedPanel, panel.getNextSibling());
                        i++; // Skip the panel we just added to avoid infinite loop
                    }
                }
            }

            // Save the translated XML to a new file
            String outputFilePath = filePath + sourceLanguage + "-to-" + targetLanguage + "-" + lessonType + ".xml";
            System.out.println("Translation saved to " + outputFilePath);
            return XMLFileManager.saveXMLToFile(doc, outputFilePath);

        } catch (Exception e) {
            // in case file fails to save
            System.out.println("\nFailed to save XML file\n");
            e.printStackTrace();
            return false;
        }
    }

    private void translatePanel(Element panel) {
        NodeList balloons = panel.getElementsByTagName("balloon");
        NodeList belowTexts = panel.getElementsByTagName("below");
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

        if (belowTexts.getLength() > 0) {
            for (int i = 0; i < belowTexts.getLength(); i++){
                Node belowText = belowTexts.item(i);
                if (belowText.getNodeType() == Node.ELEMENT_NODE){
                    String originalText = belowText.getTextContent();

                    String translatedText = translations.getOrDefault(originalText, originalText);
                    belowText.setTextContent(translatedText);
                }
            }
        }

    }

    public String getLessonType(){
        return lessonType;
    }

    public String getFilePath() {
        return filePath;
    }


}
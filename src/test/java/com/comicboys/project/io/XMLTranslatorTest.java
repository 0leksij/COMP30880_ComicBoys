package com.comicboys.project.io;

import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XMLTranslatorTest {

    private XMLTranslator translator;
    private Mappings mappings;

    @BeforeEach
    void setUp() {
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsReader = new MappingsFileReader();
        mappings = mappingsReader.getMappings();
        translator = new XMLTranslator(config, mappings);
    }

    @Test
    void testTranslateXML() {
        String inputFileName = "specification.xml";
        boolean result = translator.translateXML(inputFileName);
        assertTrue(result, "The translation should succeed.");
    }

    @Test
    void testTranslationOutputFile() {
        String inputFileName = "specification.xml";
        translator.translateXML(inputFileName);
        String outputFilePath = "assets/blueprint/english-to-italian-conjuction-lesson.xml";
        File outputFile = new File(outputFilePath);
        assertTrue(outputFile.exists(), "The output XML file should be created.");
    }

    @Test
    void testTranslationContent() {
        String inputFileName = "specification.xml";
        translator.translateXML(inputFileName);
        // Load the translated XML and verify content here
        Document translatedDoc = XMLFileManager.loadXMLFromFile("assets/blueprint/english-to-italian-conjuction-lesson.xml");

        // Check that translated content exists
        NodeList translatedBalloons = translatedDoc.getElementsByTagName("balloon");
        assertTrue(translatedBalloons.getLength() > 0, "There should be translated balloons in the output XML.");
    }
}

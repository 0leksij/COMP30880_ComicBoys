package com.comicboys.project.io.translate;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.io.config.ConfigurationFile;
import com.comicboys.project.io.config.MappingsFileReader;
import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class XMLTranslatorTest {

    private XMLTranslator translator;

    @BeforeEach
    void setUp() {
        ConfigurationFile config = new ConfigurationFile();
        MappingsFileReader mappingsReader = new MappingsFileReader();
        Mappings mappings = mappingsReader.getMappings();
        APIClient client = new APIClient(config);
        translator = new XMLTranslator(config, client, mappings, "");
    }

    @Test
    void testTranslateXMLShouldSucceed() {
        assertTrue(translator.translateXML("test/test_specification.xml"),
                "Expected translation to complete successfully");
    }

    @Test
    void testTranslatedFileExists() {
        translator.translateXML("test/test_specification.xml");

        String expectedPath = translator.getFileDirectory() + "english-to-italian-.xml";
        File file = new File(expectedPath);
        assertTrue(file.exists(), "Translated XML output file should exist");
    }

    @Test
    void testTranslatedContentContainsExpectedText() {
        translator.translateXML("test/test_conjunction.xml");

        Document doc = XMLFileManager.loadXMLFromFile(
                translator.getFileDirectory() + "english-to-italian-.xml"
        );

        NodeList balloons = doc.getElementsByTagName("balloon");
        boolean found = false;
        for (int i = 0; i < balloons.getLength(); i++) {
            String content = balloons.item(i).getTextContent();
            if (content.contains("Io") || content.contains("Sto") || content.contains("Noi") || content.contains("Stiamo")) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Expected translated balloon content to contain Italian pronouns");
    }

    @Test
    void testHandlesMissingInputFileGracefully() {
        boolean result = translator.translateXML("non_existent.xml");
        assertFalse(result, "Translation should fail for missing input file");
    }
}

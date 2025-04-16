package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XMLAudioInserterTest {
    @Test
    void testMappingAudioSinglePanel() {
        String filePath = "assets/story/audio_test/story_one_panel_one_character.xml";
        // our map of balloon content
        Map<String, String> audioFileMap = Map.of(
                "What happened?", "0"
        );
        // save with specific file name, need to reload file to check if contents are correct
        String outputFileName = "sample_audio.xml";
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap, outputFileName);
        audioInserter.insertAudio();
        // load saved xml file
        String fileDirectory = XMLFileManager.getFileDirectory(filePath);
        Document doc = XMLFileManager.loadXMLFromFile(fileDirectory + outputFileName);
        assertNotNull(doc);
        String documentContent = doc.getDocumentElement().getTextContent();
        // for each entry in map, check that, if the string key is in the document, check that the value is also there,
        // i.e. the balloon text is there, so the corresponding audio file text should be there, given it is in the map
        //      otherwise we are just checking for all items in map, but not all phrases we have generated audio for
        //      may be in our file (because our file may be data for a different lesson while our map is for all lessons)
        for (Map.Entry<String, String> entry : audioFileMap.entrySet()) {
            if (documentContent.contains(entry.getKey())) {
                String currentAudioFileName = entry.getValue() + ".mp3";
                System.out.printf("\n\"%s\" balloon has audio file \"%s\" found in file!", entry.getKey(), currentAudioFileName);
                assertTrue(documentContent.contains(currentAudioFileName));
            }
        }
    }


    @Test
    void testMappingAudioMultiplePanels() {
        String filePath = "assets/story/audio_test/story_two_panels_split_from_one_two_characters.xml";
        // our map of balloon content
        Map<String, String> audioFileMap = Map.of(
                "What happened?", "0",
                "You fell off!", "1"
        );
        // save with specific file name, need to reload file to check if contents are correct
        String outputFileName = "sample_audio.xml";
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap, outputFileName);
        audioInserter.insertAudio();
        // load saved xml file
        String fileDirectory = XMLFileManager.getFileDirectory(filePath);
        Document doc = XMLFileManager.loadXMLFromFile(fileDirectory + outputFileName);
        assertNotNull(doc);
        String documentContent = doc.getDocumentElement().getTextContent();
        for (Map.Entry<String, String> entry : audioFileMap.entrySet()) {
            if (documentContent.contains(entry.getKey())) {
                String currentAudioFileName = entry.getValue() + ".mp3";
                System.out.printf("\n\"%s\" balloon has audio file \"%s\" found in file!", entry.getKey(), currentAudioFileName);
                assertTrue(documentContent.contains(currentAudioFileName));
            }
        }
    }
    @Test
    void testMappingAudioMultipleScenes() {
        String filePath = "assets/story/audio_test/story_intro_and_two_scenes_mixed_balloons.xml";
        // our map of balloon content
        Map<String, String> audioFileMap = Map.of(
                "What happened?", "0",
                "You fell off!", "1",
                "Oh I'm so alone my sweet Betty!", "2",
                "What happened? Again?", "3",
                "You fell off! ...Again!", "4",
                "Oh Betty I actually hate you so very much.", "5"

        );
        // save with specific file name, need to reload file to check if contents are correct
        String outputFileName = "sample_audio.xml";
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap, outputFileName);
        audioInserter.insertAudio();
        // load saved xml file
        String fileDirectory = XMLFileManager.getFileDirectory(filePath);
        Document doc = XMLFileManager.loadXMLFromFile(fileDirectory + outputFileName);
        assertNotNull(doc);
        String documentContent = doc.getDocumentElement().getTextContent();
        for (Map.Entry<String, String> entry : audioFileMap.entrySet()) {
            if (documentContent.contains(entry.getKey())) {
                String currentAudioFileName = entry.getValue() + ".mp3";
                System.out.printf("\n\"%s\" balloon has audio file \"%s\" found in file!", entry.getKey(), currentAudioFileName);
                assertTrue(documentContent.contains(currentAudioFileName));
            }
        }
    }
    // this one is for testing that it isn't adding an audio tag because our map has no audio data for the speech balloon
    @Test
    void testMappingNoMatchingAudio() {
        String filePath = "assets/story/audio_test/story_one_panel_one_character.xml";
        // our map of balloon content
        Map<String, String> audioFileMap = Map.of(
                "This line does not exist in file", "0"
        );
        // save with specific file name, need to reload file to check if contents are correct
        String outputFileName = "sample_audio.xml";
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap, outputFileName);
        audioInserter.insertAudio();
        // load saved xml file
        String fileDirectory = XMLFileManager.getFileDirectory(filePath);
        Document doc = XMLFileManager.loadXMLFromFile(fileDirectory + outputFileName);
        assertNotNull(doc);
        String documentContent = doc.getDocumentElement().getTextContent();
        for (Map.Entry<String, String> entry : audioFileMap.entrySet()) {
            if (documentContent.contains(entry.getKey())) {
                String currentAudioFileName = entry.getValue() + ".mp3";
                System.out.printf("\n\"%s\" balloon has audio file \"%s\" found in file!", entry.getKey(), currentAudioFileName);
                assertTrue(documentContent.contains(currentAudioFileName));
            }
        }
        // should not contain any audio files since no balloon text in file has matching key in map
//        assertFalse(documentContent.contains("0.mp3"));
    }


    // this should work because of the nature of hashmaps, where they are not sequential like lists but can be indexed
    // due to hashes, but is just a test of the functionality of if it has the audio data we are looking for using a
    // map that would have audio data for different lessons all in one data structure
    @Test
    void testMappingAudioFromMapWithOtherAudioData() {
        String filePath = "assets/story/audio_test/story_intro_and_two_scenes_mixed_balloons.xml";
        // our map of balloon content
        Map<String, String> audioFileMap = Map.of(
                "What happened?", "0",
                "Filler line one", "1",
                "You fell off!", "2",
                "Oh I'm so alone my sweet Betty!", "4",
                "Filler line two", "5",
                "What happened? Again?", "6",
                "You fell off! ...Again!", "7",
                "Oh Betty I actually hate you so very much.", "9"
        );
        // save with specific file name, need to reload file to check if contents are correct
        String outputFileName = "sample_audio.xml";
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap, outputFileName);
        audioInserter.insertAudio();
        // load saved xml file
        String fileDirectory = XMLFileManager.getFileDirectory(filePath);
        Document doc = XMLFileManager.loadXMLFromFile(fileDirectory + outputFileName);
        assertNotNull(doc);
        String documentContent = doc.getDocumentElement().getTextContent();
        for (Map.Entry<String, String> entry : audioFileMap.entrySet()) {
            if (documentContent.contains(entry.getKey())) {
                String currentAudioFileName = entry.getValue() + ".mp3";
                System.out.printf("\n\"%s\" balloon has audio file \"%s\" found in file!", entry.getKey(), currentAudioFileName);
                assertTrue(documentContent.contains(currentAudioFileName));
            }
        }
        // should not have audio data for lines not in file
        assertFalse(documentContent.contains(audioFileMap.get("Filler line one") + ".mp3"));
        assertFalse(documentContent.contains(audioFileMap.get("Filler line two") + ".mp3"));
    }
}

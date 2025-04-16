package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XMLPanelSplitterTest {


    @Test
    void testOnePanelOneCharacter() {
        // one panel, one balloon (should not change anything)
        String filePath = "assets/story/audio_test/story_one_panel_one_character.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since one panel, one character, with one balloon, should change nothing
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if same number of panels/balloons before and after split (split should have done nothing)
        assertEquals(panelCountBeforeSplit, panelCountAfterSplit);
        assertEquals(balloonCountBeforeSplit, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?"
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
    @Test
    void testOnePanelTwoCharacters() {
        // one panel, two balloons, should split into two panels, each with one balloon
        String filePath = "assets/story/audio_test/story_one_panel_two_characters.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since one panel with 2 balloons, expect to get an extra panel
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(1, panelCountBeforeSplit);
        assertEquals(2, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if we get an extra panel
        assertEquals(2, panelCountAfterSplit);
        assertEquals(2, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!"
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
    @Test
    void testTwoPanelsSplitFromOneWithTwoCharacters() {
        // two panels, each with one balloon, resulted from splitting a panel with two balloons,
        // should do nothing to the file since it already satisfies the condition
        String filePath = "assets/story/audio_test/story_two_panels_split_from_one_two_characters.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since one panel, one character, with one balloon, should change nothing
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(2, panelCountBeforeSplit);
        assertEquals(2, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if same number of panels/balloons before and after split (split should have done nothing)
        assertEquals(2, panelCountAfterSplit);
        assertEquals(2, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!"
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
    @Test
    void testTwoPanelsMixedBalloons() {
        // two panels, one has 2 balloons, other has 1, should end up with 3 panels, where the first 2 balloons
        // end up in order for the first 2 out of 3 panels, while the 3rd is just the one that had one balloon
        String filePath = "assets/story/audio_test/story_two_panels_mixed_balloons.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since one panel has 2 balloons, we should get an extra panel
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(2, panelCountBeforeSplit);
        assertEquals(3, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if we got the extra panel from splitting the one with 2 balloons
        assertEquals(3, panelCountAfterSplit);
        assertEquals(3, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!",
                "Oh I'm so alone my sweet Betty!"
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }

    @Test
    void testThreePanelsTwoBalloonsEach() {
        // two panels, one has 2 balloons, other has 1, should end up with 3 panels, where the first 2 balloons
        // end up in order for the first 2 out of 3 panels, while the 3rd is just the one that had one balloon
        String filePath = "assets/story/audio_test/story_three_panels_two_balloons_each.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since one panel has 2 balloons, we should get an extra panel
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(3, panelCountBeforeSplit);
        assertEquals(6, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if we got the extra panel from splitting the one with 2 balloons
        assertEquals(6, panelCountAfterSplit);
        assertEquals(6, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!",
                "What happened? Again?",
                "You fell off! ...Again!",
                "What happened? For the last time...",
                "You fell off! For the last time..."
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
    @Test
    void testTwoScenesMixedBalloons() {
        // two scenes, each has two panels, of the two panels, one has 2 balloons, other has 1, should end up with
        // 6 panels (3 for each scene), where the first 2 panels in each scene were from the split single panel with
        // two character, and the 3rd panel was the original single character panel
        String filePath = "assets/story/audio_test/story_two_scenes_mixed_balloons.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since there are 2 panels (one from each scene) with 2 balloons, expect an extra 2 panels after the split
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(4, panelCountBeforeSplit);
        assertEquals(6, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if we got 2 extra panels after the split
        assertEquals(6, panelCountAfterSplit);
        assertEquals(6, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!",
                "Oh I'm so alone my sweet Betty!",
                "What happened? Again?",
                "You fell off! ...Again!",
                "Oh Betty I actually hate you so very much."
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
    @Test
    void testIntroAndTwoScenesMixedBalloons() {
        // two scenes, same as just the scenes test above but now also has one extra panel in each scene for scene
        // introduction, but this panel has no balloon, so this one should remain unaffected
        String filePath = "assets/story/audio_test/story_intro_and_two_scenes_mixed_balloons.xml";
        Document doc = XMLFileManager.loadXMLFromFile(filePath);
        assertNotNull(doc);
        // since there are 2 panels with 2 balloons, we will expect and extra 2 panels after the split
        int panelCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountBeforeSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        assertEquals(6, panelCountBeforeSplit);
        assertEquals(6, balloonCountBeforeSplit);
        XMLPanelSplitter.separateMultipleSpeechPanels(doc);
        int panelCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "panel");
        int balloonCountAfterSplit = XMLFileManager.countElements(doc.getDocumentElement(), "balloon");
        // checking if we got only 2 extra panels after the split (split should have done nothing to intro)
        assertEquals(8, panelCountAfterSplit);
        assertEquals(6, balloonCountAfterSplit);
        List<String> expectedBalloons = List.of(
                "What happened?",
                "You fell off!",
                "Oh I'm so alone my sweet Betty!",
                "What happened? Again?",
                "You fell off! ...Again!",
                "Oh Betty I actually hate you so very much."
        );
        NodeList actualBalloons = doc.getElementsByTagName("balloon");
        int currentNode, currentBalloon;
        currentNode = currentBalloon = 0;
        while (currentNode < actualBalloons.getLength()) {
            Node balloon = actualBalloons.item(currentNode);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                String balloonContent = balloon.getTextContent().trim();
                // compare the textual content of each balloon
                assertEquals(expectedBalloons.get(currentBalloon), balloonContent);
                currentBalloon++;
            }
            currentNode++;
        }
    }
}

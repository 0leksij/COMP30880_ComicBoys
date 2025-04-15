package com.comicboys.project.io;

import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class XMLAudioInserter extends Blueprint {


    public XMLAudioInserter(String filePath) {
        super(filePath);
    }


    public void insertAudio() {
        Document doc = getFile();
        // first make sure all panels only have one speech balloon each
        XMLFileManager.separateMultipleSpeechPanels(doc);


        Map<String, String> sampleAudioFileMap = Map.of(
                "What happened?", "0",
                "You fell off!", "1"
        );






        // save edited document to XML file
        String fileDirectory = XMLFileManager.getFileDirectory(getFilePath());
        String outputFilePath = fileDirectory + "sample_panel_split.xml";
        XMLFileManager.saveXMLToFile(getFile(), outputFilePath);
        System.out.println("Split panel XML saved in " + outputFilePath);
    }


    public static void main(String[] args) {

//        // one panel, one balloon (should not change anything)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_one_panel_one_character.xml");
//        // one panel, two balloons (should split into two panels for each balloon respectively)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_one_panel_two_characters.xml");
//        // two panels, split from one (should not change anything as already only one balloon per panel)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_panels_split_from_one_two_characters.xml");
//        // two panels, one has two balloons, another has one, should end up with 3 panels (first two relate to original first)
//        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_panels_mixed_balloons.xml");
        // two scenes, each has two panels, same as mixed balloons for two panels, just testing with multiple scenes instead
        XMLAudioInserter audioInserter = new XMLAudioInserter("assets/story/audio_test/story_two_scenes_mixed_balloons.xml");
        audioInserter.insertAudio();
    }

}

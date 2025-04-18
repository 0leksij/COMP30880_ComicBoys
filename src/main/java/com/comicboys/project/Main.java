package com.comicboys.project;

import com.comicboys.project.audio.AudioGenerator;
import com.comicboys.project.client.APIClient;
import com.comicboys.project.data.Mappings;
import com.comicboys.project.data.StringEntry;
import com.comicboys.project.io.*;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class Main {
    public static Random random = new Random();
    public static void main(String[] args) throws InterruptedException {
        // creating config file object
        ConfigurationFile config = new ConfigurationFile();
        APIClient client = new APIClient(config);
        System.out.println("\n------------------------------");
        System.out.println("\n------[ CURRENT SPRINT ]------");
        System.out.println("\n------------------------------");

        ConfigurationFile configurationFile = new ConfigurationFile();

        AudioGenerator audioGenerator = new AudioGenerator(configurationFile);

//        // one panel, one balloon (should not change anything)
//        filePath = "assets/story/audio_test/story_one_panel_one_character.xml";
//        // one panel, two balloons (should split into two panels for each balloon respectively)
//        filePath = "assets/story/audio_test/story_one_panel_two_characters.xml";
//        // two panels, split from one (should not change anything as already only one balloon per panel)
//        filePath = "assets/story/audio_test/story_two_panels_split_from_one_two_characters.xml";
//        // two panels, one has two balloons, another has one, should end up with 3 panels (first two relate to original first)
//        filePath = "assets/story/audio_test/story_two_panels_mixed_balloons.xml";
//        // two scenes, each has two panels, same as mixed balloons for two panels, just testing with multiple scenes instead
//        filePath = "assets/story/audio_test/story_two_scenes_mixed_balloons.xml";
        // same as two scenes mixed balloons but with scene intro as well

        String filePath;
        Document xmlDoc;

        filePath = "assets/story/audio_test/test.xml";
        xmlDoc = XMLFileManager.loadXMLFromFile(filePath);

        // audio should be generated from the test xml and saved in the audio folder and also appended to the audio-index.tsv
        try {
            audioGenerator.generateAudioFromXML(xmlDoc);
        }catch (Exception e){
            System.err.println("Error during audio generation: " + e.getMessage());
            e.printStackTrace();
        }

        // audio should already be generated from this xml. Just need to save it in a new xml
        filePath = "assets/story/audio_test/story_intro_and_two_scenes_mixed_balloons.xml";
        xmlDoc = XMLFileManager.loadXMLFromFile(filePath);
        try {
            audioGenerator.generateAudioFromXML(xmlDoc);
        }catch (Exception e){
            System.err.println("Error during audio generation: " + e.getMessage());
            e.printStackTrace();
        }

        Map<String, String> audioFileMap = audioGenerator.getMap();
        System.out.println(audioFileMap);
        XMLAudioInserter audioInserter = new XMLAudioInserter(filePath, audioFileMap);
        audioInserter.insertAudio();
    }
}
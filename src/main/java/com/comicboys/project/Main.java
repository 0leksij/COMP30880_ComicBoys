package com.comicboys.project;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.client.Mappings;
import com.comicboys.project.client.TSVReader;
import com.comicboys.project.config.ConfigurationFile;

import java.util.Random;


public class Main {
    public static Random random = new Random();
    public static void main(String[] args) {
//        ConfigurationFile configFile = new ConfigurationFile();
//        APIClient client = new APIClient(configFile);
//
//
//        System.out.println(client.sendPrompt("whats up my dude"));



        // read tsv file with specified number of lines
        TSVReader myReader = new TSVReader(5);
        // get mappings data structure
        Mappings mappings = myReader.getMappings();

        // printing mappings data for reference
        System.out.println(mappings);
        System.out.println();
        // find match given certain word (finds FIRST row where this word appears)
        // returns a hashmap with key-value pairs for leftPose,combinedText, etc.
        System.out.println(mappings.findMatch("to bow"));




    }
}
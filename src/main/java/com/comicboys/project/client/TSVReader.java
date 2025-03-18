package com.comicboys.project.client;

import com.comicboys.project.Main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class TSVReader {
    private Mappings mappings = new Mappings();
    // if no lines passed in, will read entire file
    public TSVReader() {
        readFile(-1);
    }
    // specify number of lines to read
    public TSVReader(int numOfLines) {
        readFile(numOfLines);
    }
    // function to read file based on number of lines (-1 for whole file)
    private void readFile(int numOfLines) {
        BufferedReader reader;
        int currentLine = 0;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/pose_pairings_with_backgrounds.tsv"));
            String line = reader.readLine();
            line = reader.readLine(); // skips header
            // reading entire file or specified num of lines
            while ((line != null) && (currentLine != numOfLines)) {
                // add entry to mappings
                mappings.addEntry(line);
                // read next line
                line = reader.readLine();
                currentLine++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // getter for mappings
    public Mappings getMappings() {
        return mappings;
    }
}

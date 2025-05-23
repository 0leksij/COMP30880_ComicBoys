package com.comicboys.project.io.config;

import com.comicboys.project.data.Mappings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MappingsFileReader {
    // default values (mappings is null if file fails to be read)
    private final String filePath = "assets/mappings/pose_pairings_with_backgrounds.tsv";
    private Mappings mappings;
    // if no lines passed in, will read entire file
    public MappingsFileReader() { readFile(-1); }
    // specify number of lines to read
    public MappingsFileReader(int numOfLines) {
        readFile(numOfLines);
    }
    // function to read file based on number of lines (-1 for whole file)
    private void readFile(int numOfLines) {
        BufferedReader reader;
        int currentLine = 0;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            mappings = new Mappings(); // initialise mappings (once this reached we know file was read successfully)
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
            System.out.println("\nFailed to read file in path: " + filePath);
        }
    }
    // getter for mappings
    public Mappings getMappings() {
        return mappings;
    }
}

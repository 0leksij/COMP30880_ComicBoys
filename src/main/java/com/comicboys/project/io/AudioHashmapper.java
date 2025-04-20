package com.comicboys.project.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioHashmapper {


    public static HashMap<String, String> loadAudioIndex(String indexFilePath) throws IOException {
        HashMap<String, String> audioMap = new HashMap<>();
        File indexFile = new File(indexFilePath);

        if (!indexFile.exists()) {
            throw new IOException("Audio index file not found at: " + indexFilePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split on tab character
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    String text = parts[0].trim();
                    String filename = parts[1].trim();
                    audioMap.put(text, filename);
                }
            }
        }

        return audioMap;
    }

    /*public static void main(String[] args) {
        // Path to your audioindex.tsv file
        String filePath = "assets/story/audio-index.tsv"; // Replace with your actual path

        try {
            HashMap<String, String> audioMap = loadAudioIndex(filePath);

            System.out.println("=== Loaded Audio Index ===");
            for (Map.Entry<String, String> entry : audioMap.entrySet()) {
                System.out.println("Text: \"" + entry.getKey() + "\" -> File: " + entry.getValue());
            }

        } catch (Exception e) {
            System.err.println("Error loading audio index:");
            e.printStackTrace();
        }
    }*/
}

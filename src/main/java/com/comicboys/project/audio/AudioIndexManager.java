package com.comicboys.project.audio;

import java.io.*;
import java.util.*;

public class AudioIndexManager {
    private final String indexFilePath;

    public AudioIndexManager(String indexFilePath) {
        this.indexFilePath = indexFilePath;
        ensureFileExists();
    }

    public void appendEntry(String text, String audioFileName) {
        if (entryExists(text)) {
            System.out.println("Audio index already contains entry for: " + text);
            return;
        }

        try (FileWriter writer = new FileWriter(indexFilePath, true)) {
            writer.write(text + "\t" + audioFileName + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean entryExists(String text) {
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 1 && parts[0].equals(text)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void ensureFileExists() {
        File file = new File(indexFilePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created audio index file: " + indexFilePath);
                } else {
                    System.err.println("Failed to create audio index file: " + indexFilePath);
                }
            } catch (IOException e) {
                System.err.println("Error creating audio index file: " + e.getMessage());
            }
        }
    }
}


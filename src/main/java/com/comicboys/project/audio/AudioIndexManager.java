package com.comicboys.project.audio;

import com.comicboys.project.io.AudioHashmapper;

import java.io.*;
import java.util.HashMap;

public class AudioIndexManager {
    private final String indexFilePath;
    private HashMap<String, String> indexMap;
    private int nextIndex;

    public AudioIndexManager(String indexFilePath) {
        this.indexFilePath = indexFilePath;
        ensureFileExists();
        try {
            this.indexMap = AudioHashmapper.loadAudioIndex(indexFilePath);
            this.nextIndex = calculateNextIndex(indexMap);
        } catch (IOException e) {
            System.err.println("Error loading audio index: " + e.getMessage());
            this.indexMap = new HashMap<>();
            this.nextIndex = 0;
        }
    }

    public boolean entryExists(String text) {
        return indexMap.containsKey(text);
    }

    public String appendEntry(String text) {
        if (entryExists(text)) {
            return indexMap.get(text);
        }

        String filename = nextIndex + ".mp3";
        try (FileWriter writer = new FileWriter(indexFilePath, true)) {
            writer.write(text + "\t" + filename + "\n");
            indexMap.put(text, filename);
            nextIndex++;
            return filename;
        } catch (IOException e) {
            System.err.println("Failed to write to index file: " + e.getMessage());
            return null;
        }
    }

    private int calculateNextIndex(HashMap<String, String> index) {
        return index.values().stream()
                .map(f -> f.replace(".mp3", ""))
                .filter(s -> s.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(-1) + 1;
    }

    private void ensureFileExists() {
        File file = new File(indexFilePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    System.out.println("Created new index file: " + indexFilePath);
                }
            } catch (IOException e) {
                System.err.println("Error creating audio index file: " + e.getMessage());
            }
        }
    }
}

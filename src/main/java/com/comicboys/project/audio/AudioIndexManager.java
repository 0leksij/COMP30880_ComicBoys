package com.comicboys.project.audio;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class AudioIndexManager {
    private final String indexFilePath;
    private final Map<String, String> indexMap;
    private int nextIndex;

    public AudioIndexManager(String indexFilePath) {
        this.indexFilePath = indexFilePath;
        this.indexMap = loadIndexFile();
        this.nextIndex = calculateNextIndex();
    }

    private Map<String, String> loadIndexFile() {
        Map<String, String> loadedMap = new HashMap<>();
        if (!Files.exists(Paths.get(indexFilePath))) {
            ensureFileExists();
            return loadedMap;
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(indexFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 2) {
                    loadedMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading audio index: " + e.getMessage());
        }
        return loadedMap;
    }

    public boolean entryExists(String text) {
        return indexMap.containsKey(text);
    }

    public String getNextAvailableFileName() {
        return nextIndex + ".mp3";
    }

    public void appendEntry(String text, String fileName) throws IOException {
        if (entryExists(text)) {
            return;
        }

        indexMap.put(text, fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(indexFilePath),
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {
            writer.write(text + "\t" + fileName);
            writer.newLine();
        }
        nextIndex++;
    }

    int calculateNextIndex() {
        return indexMap.values().stream()
                .map(f -> f.replace(".mp3", ""))
                .filter(s -> s.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(-1) + 1;
    }

    private void ensureFileExists() {
        try {
            Files.createDirectories(Paths.get(indexFilePath).getParent());
            if (Files.notExists(Paths.get(indexFilePath))) {
                Files.createFile(Paths.get(indexFilePath));
            }
        } catch (IOException e) {
            System.err.println("Error creating audio index file: " + e.getMessage());
        }
    }

    public Map<String, String> getIndexMap() {
        return Collections.unmodifiableMap(indexMap);
    }
}
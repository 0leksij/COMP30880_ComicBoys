package com.comicboys.project.audio;

import com.comicboys.project.audio.AudioIndexManager;
import com.comicboys.project.io.AudioHashmapper;
import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioGenerator {
    private final HttpClient httpClient;
    private final String apiKey;
    private final String voice;
    String audioDirectory;
    private AudioIndexManager audioIndexManager;
    private final int maxRetries;
    private final long retryDelayMs;
    private final Map<String, String> audioIndex;

    public AudioGenerator(ConfigurationFile config) {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.apiKey = config.getProperty("API_KEY");
        this.voice = config.getProperty("TTS_VOICE");
        this.audioDirectory = "assets/story/audio/";
        this.audioIndexManager = new AudioIndexManager("assets/story/audio-index.tsv");
        try {
            this.audioIndex = AudioHashmapper.loadAudioIndex("assets/story/audio-index.tsv");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load audio index: " + e.getMessage());
        }

        this.maxRetries = Integer.parseInt(config.getProperty("TTS_MAX_RETRIES"));
        this.retryDelayMs = Long.parseLong(config.getProperty("TTS_RETRY_DELAY_MS"));

        try {
            createAudioDirectory();
        } catch (IOException e) {
            System.err.println("Warning: Could not create audio directory: " + e.getMessage());
        }
    }

    public void generateAudioFromXML(Document xmlDocument) throws IOException {
        List<String> speechTexts = extractSpeechTexts(xmlDocument);

        for (String text : speechTexts) {
            if (audioIndexManager.entryExists(text)) {
                System.out.println("Skipping already processed: " + text);
                continue;
            }

            String audioFileName = audioIndexManager.getNextAvailableFileName();
            if (audioFileName == null) {
                continue;
            }

            Path audioPath = Paths.get(audioDirectory, audioFileName);

            try {
                synthesizeWithRetry(text, audioPath, maxRetries);
                audioIndexManager.appendEntry(text, audioFileName);  // Write only after success
                System.out.println("Generated: " + audioFileName);
            } catch (Exception e) {
                System.err.printf("Failed to generate audio for '%s': %s%n",
                        text.length() > 50 ? text.substring(0, 50) + "..." : text, e.getMessage());
            }
        }
    }

    List<String> extractSpeechTexts(Document xmlDocument) {
        List<String> texts = new ArrayList<>();
        NodeList balloons = xmlDocument.getElementsByTagName("balloon");

        for (int i = 0; i < balloons.getLength(); i++) {
            Node balloon = balloons.item(i);
            if (balloon.getNodeType() == Node.ELEMENT_NODE) {
                Element balloonElement = (Element) balloon;
                if ("speech".equals(balloonElement.getAttribute("status"))) {
                    NodeList contents = balloonElement.getElementsByTagName("content");
                    if (contents.getLength() > 0) {
                        String text = contents.item(0).getTextContent().trim();
                        if (!text.isEmpty()) {
                            texts.add(text);
                        }
                    }
                }
            }
        }

        return texts;
    }

    void synthesizeWithRetry(String text, Path outputFile, int retriesLeft)
            throws IOException, InterruptedException {
        try {
            synthesizeToFile(text, outputFile);
        } catch (Exception e) {
            if (retriesLeft > 0) {
                Thread.sleep(retryDelayMs);
                synthesizeWithRetry(text, outputFile, retriesLeft - 1);
            } else {
                throw e;
            }
        }
    }

    protected void synthesizeToFile(String text, Path outputFile) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(String.format("""
                        {
                          "model": "gpt-4o-mini-tts",
                          "input": "%s",
                          "voice": "%s",
                          "response_format": "mp3"
                        }
                        """, escapeJson(text), voice)))
                .build();

        HttpResponse<Path> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofFile(outputFile)
        );

        if (response.statusCode() != 200) {
            Files.deleteIfExists(outputFile);
            throw new IOException("TTS API request failed with status: " + response.statusCode()
                    + " and body: " + response.body());
        }
    }

    String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void createAudioDirectory() throws IOException {
        Files.createDirectories(Paths.get(audioDirectory));
    }

    public Map<String,String> getMap(){
        return audioIndex;
    }

    public void setAudioIndexManager(String path){
        this.audioIndexManager = new AudioIndexManager(path);
    }

    public void setAudioDirectory(String path){
        this.audioDirectory = path;
    }
}

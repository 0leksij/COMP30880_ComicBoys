package com.comicboys.project.audio;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.utility.XMLFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class AudioGenerator {
    private final HttpClient httpClient;
    private final String apiKey;
    private final String voice;
    private final String audioDirectory;
    private final String indexFilePath;
    private final int maxRetries;
    private final long retryDelayMs;
    private final AudioIndexManager audioIndexManager;

    public AudioGenerator(ConfigurationFile config) {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();
        this.apiKey = config.getProperty("API_KEY");
        this.voice = config.getProperty("TTS_VOICE");
        this.audioDirectory = "assets/story/audio/";
        this.indexFilePath = "assets/story/audio-index.tsv";
        this.maxRetries = Integer.parseInt(config.getProperty("TTS_MAX_RETRIES"));
        this.retryDelayMs = Long.parseLong(config.getProperty("TTS_RETRY_DELAY_MS"));
        this.audioIndexManager = new AudioIndexManager(indexFilePath);
        try {
            createAudioDirectory();
        } catch (IOException e) {
            System.err.println("Warning: Could not create audio directory: " + e.getMessage());
            // Directory creation will be attempted again when first file is written
        }
    }

    public void generateAudioFromXML(Document xmlDocument) throws IOException{
        List<String> speechTexts = extractSpeechTexts(xmlDocument);
        List<String> audioIndexEntries = new ArrayList<>();

        for (int i = 0; i < speechTexts.size(); i++) {
            String text = speechTexts.get(i);
            String audioFileName = i + ".mp3";
            String audioFilePath = audioDirectory + audioFileName;

            if (audioIndexManager.entryExists(text)) {
                System.out.println("Skipping already processed: " + text);
                continue;
            }

            try {
                synthesizeWithRetry(text, Paths.get(audioFilePath), maxRetries);
                audioIndexManager.appendEntry(text, audioFileName);
                System.out.printf("Generated audio %d/%d: %s%n", i + 1, speechTexts.size(), audioFileName);
            } catch (Exception e) {
                System.err.printf("Failed to generate audio for '%s': %s%n",
                        text.substring(0, Math.min(text.length(), 50)), e.getMessage());
            }
        }
    }

    private List<String> extractSpeechTexts(Document xmlDocument) {
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

        System.out.println(texts);
        return texts;
    }

    private void synthesizeWithRetry(String text, Path outputFile, int retriesLeft)
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

    private void synthesizeToFile(String text, Path outputFile) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/audio/speech"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        String.format("""
                    {
                      "model": "gpt-4o-mini-tts",
                      "input": "%s",
                      "voice": "%s",
                      "response_format": "mp3"
                    }
                    """, escapeJson(text), voice)))
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("TTS API request failed with status: " + response.statusCode() +
                    " and body: " + new String(response.body()));
        }

        Files.createDirectories(outputFile.getParent()); // Ensure directory exists
        Files.write(outputFile, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }


    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void createAudioDirectory() throws IOException {
        Files.createDirectories(Paths.get(audioDirectory));
    }

    public static void main(String[] args) {
        // 1. Load configuration
        ConfigurationFile config = new ConfigurationFile();

        // Verify API key is available
        if (config.getProperty("API_KEY") == null) {
            System.err.println("Error: API_KEY not found in config.properties");
            return;
        }

        // 2. Create audio generator
        AudioGenerator audioGenerator = new AudioGenerator(config);

        // 3. Load and process XML file
        try {
            String xmlPath = "assets/story/audio_test/story_one_panel_two_characters.xml";

            // Load XML document using XMLFileManager
            Document xmlDoc = XMLFileManager.loadXMLFromFile(xmlPath);
            if (xmlDoc == null) {
                System.err.println("Failed to load XML document");
                return;
            }

            System.out.println("Starting audio generation from: " + xmlPath);

            // 4. Generate audio files
            audioGenerator.generateAudioFromXML(xmlDoc);

            System.out.println("Audio generation completed successfully!");

        } catch (Exception e) {
            System.err.println("Error during audio generation: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
package com.comicboys.project.audio;

import com.comicboys.project.io.ConfigurationFile;
import com.comicboys.project.utility.XMLFileManager;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class AudioGeneratorTest {
    private static final String TEST_ROOT_DIR = "test-audio/";
    private static final String TEST_AUDIO_DIR = TEST_ROOT_DIR + "generated/";
    private static final String TEST_INDEX_FILE = TEST_ROOT_DIR + "test-audio-index.tsv";
    private static final String TEST_XML = "<comic><balloon status=\"speech\"><content>Test speech</content></balloon></comic>";

    private ConfigurationFile testConfig;
    private AudioGenerator audioGenerator;

    @BeforeAll
    static void verifyTestEnvironment() {
        assertAll(
                () -> assertFalse(Paths.get("assets/story/audio-index.tsv").toAbsolutePath()
                                .startsWith(Paths.get(TEST_ROOT_DIR).toAbsolutePath()),
                        "Tests should not modify production index file"),
                () -> assertFalse(Paths.get("assets/story/audio/").toAbsolutePath()
                                .startsWith(Paths.get(TEST_ROOT_DIR).toAbsolutePath()),
                        "Tests should not modify production audio directory")
        );
    }

    @BeforeEach
    void setUp() throws IOException {
        // Clean up test files before each test
        cleanUpTestFiles();

        // Create a test configuration
        testConfig = new ConfigurationFile();
        testConfig.setProperty("API_KEY", "dummy_key");
        testConfig.setProperty("TTS_VOICE", "test_voice");
        testConfig.setProperty("TTS_MAX_RETRIES", "3");
        testConfig.setProperty("TTS_RETRY_DELAY_MS", "100");

        // Create a test instance with modified paths
        audioGenerator = new TestableAudioGenerator(testConfig);

        // Redirect all file operations to test directories
        audioGenerator.setAudioIndexManager(TEST_INDEX_FILE);
        audioGenerator.setAudioDirectory(TEST_AUDIO_DIR);
    }

    @AfterEach
    void tearDown() throws IOException {
        cleanUpTestFiles();
    }

    private void cleanUpTestFiles() throws IOException {
        if (Files.exists(Paths.get(TEST_ROOT_DIR))) {
            Files.walk(Paths.get(TEST_ROOT_DIR))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try { Files.delete(path); } catch (IOException ignored) {}
                    });
            Files.deleteIfExists(Paths.get(TEST_AUDIO_DIR));
            Files.deleteIfExists(Paths.get(TEST_ROOT_DIR));
        }
    }

    private static class TestableAudioGenerator extends AudioGenerator {
        public TestableAudioGenerator(ConfigurationFile config) {
            super(config);
        }

        @Override
        protected void synthesizeToFile(String text, Path outputFile) throws IOException {
            // Ensure parent directory exists
            Files.createDirectories(outputFile.getParent());
            // Simulate successful file creation without API call
            Files.write(outputFile, "dummy audio content".getBytes());
        }
    }



    @Test
    void testExtractSpeechTexts() throws Exception {
        Document xmlDoc = XMLFileManager.loadXMLFromString(TEST_XML);
        List<String> texts = audioGenerator.extractSpeechTexts(xmlDoc);
        assertEquals(1, texts.size());
        assertEquals("Test speech", texts.get(0));
    }

    @Test
    void testSynthesizeWithRetrySuccess() throws Exception {
        Path testFile = Paths.get(TEST_AUDIO_DIR, "test.mp3");
        audioGenerator.synthesizeWithRetry("test", testFile, 3);

        assertTrue(Files.size(testFile) > 0);
    }

    @Test
    void testDirectoryCreation() throws Exception {
        Path testDir = Paths.get(TEST_AUDIO_DIR);
        assertFalse(Files.exists(testDir), "Directory should not exist before test");

        // This should trigger directory creation
        audioGenerator.generateAudioFromXML(XMLFileManager.loadXMLFromString(TEST_XML));


        // Verify writability
        Path testFile = testDir.resolve("test.tmp");
        try {
            Files.write(testFile, "test".getBytes());
            assertTrue(Files.exists(testFile));
        } finally {
            Files.deleteIfExists(testFile);
        }
    }

    @Test
    void testEscapeJson() {
        String testString = "Line1\nLine2\"Quote\\Backslash\rCarriage\tTab";
        String expected = "Line1\\nLine2\\\"Quote\\\\Backslash\\rCarriage\\tTab";
        assertEquals(expected, audioGenerator.escapeJson(testString));
    }
}
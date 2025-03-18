package com.comicboys.project.client;


import com.comicboys.project.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MappingsTest {
    Mappings mappings;
    @BeforeEach
    public void setUp() {
        mappings = new Mappings();
    }
    // will read successfully if file exists
    @Test
    void createMappings(){
        assertTrue(mappings.isEmpty());
    }
    // will read successfully if file read is not empty
    @Test
    void addingEntry(){
        mappings.addEntry("a1, a2\tb1\tc1\td1, d2\te1");
        String expectedMappings = "\n[[a1, a2], [b1], [c1], [d1, d2], [e1]]";
        assertEquals(expectedMappings, mappings.toString());
        assertFalse(mappings.isEmpty());
    }
    // will read successfully if file read is not empty
    @Test
    void multipleEntries(){
        mappings.addEntry("a1, a2\tb1\tc1\td1, d2\te1");
        mappings.addEntry("x1\ty1\tz1\tw1\tv1");
        String expectedMappings = "\n[[a1, a2], [b1], [c1], [d1, d2], [e1]]" +
                "\n[[x1], [y1], [z1], [w1], [v1]]";
        assertEquals(expectedMappings, mappings.toString());
        assertFalse(mappings.isEmpty());
    }
    // will pass if a row contains word in either of two text columns
    @Test
    void findMatchingExistingText(){
        mappings.addEntry("a1, a2\tb1\tc1\td1, d2\te1");
        mappings.addEntry("x1\ty1\tsecret\tw1\tv1");
        Map<String, String> expectedMatch = new HashMap<>() {{
            put("leftPose", "x1");
            put("combinedText", "y1");
            put("leftText", "secret");
            put("rightPose", "w1");
            put("backgrounds", "v1");
        }};
        assertEquals(expectedMatch.toString(), mappings.findMatch("secret").toString());
        assertFalse(mappings.isEmpty());
    }
    // will pass if an empty map is returned since text not in map we provided
    @Test
    void findMatchingNonExistingText(){
        mappings.addEntry("a1, a2\tb1\tc1\td1, d2\te1");
        mappings.addEntry("x1\ty1\tz1\tw1\tv1");
        assertEquals(Map.of().toString(), mappings.findMatch("secret").toString());
        assertFalse(mappings.isEmpty());
    }
}

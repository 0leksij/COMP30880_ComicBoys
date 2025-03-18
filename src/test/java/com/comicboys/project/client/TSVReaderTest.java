package com.comicboys.project.client;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TSVReaderTest {
    TSVReader reader;
    @BeforeEach
    public void setUp() {
        reader = new TSVReader();
    }
    // will read successfully if file exists
    @Test
    void readExistingTSVFile(){
        Mappings mappings = reader.getMappings();
        assertNotNull(mappings);
    }
    // will read successfully if file read is not empty
    @Test
    void readNonEmptyTSVFile(){
        Mappings mappings = reader.getMappings();
        assertFalse(mappings.isEmpty());
    }
}

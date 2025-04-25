package com.comicboys.project.io.config;


import com.comicboys.project.data.Mappings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class MappingsFileReaderTest {
    MappingsFileReader reader;
    @BeforeEach
    public void setUp() {
        reader = new MappingsFileReader();
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

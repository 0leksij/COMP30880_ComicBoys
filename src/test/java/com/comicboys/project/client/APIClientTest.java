package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class APIClientTest {
    private ConfigurationFile config;
    private APIClient client;

    @BeforeEach
    void setUp() throws InterruptedException {
        config = new ConfigurationFile();
        client = new APIClient(config);

        Thread.sleep(3000);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        Thread.sleep(3000);
    }

    @Test
    void SuccessfulTextResponseTest() {

        APIResponse response = client.sendPrompt("Hello");

        Assertions.assertFalse(response.isNumberedList()); // Ensure it's not a numbered list
        Assertions.assertEquals("Hello! How can I assist you today?", response.getTextResponse());
    }


    @Test
    void SuccessfulNumberedListResponseTest(){

        APIResponse response = client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines");
        Assertions.assertTrue(response.isNumberedList());

        Assertions.assertEquals("1. Jane\n2. John\n", response.getNumberedList().toString());
    }

    @Test
    void getFromListTest(){
        APIResponse response = client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines");

        Assertions.assertEquals("Jane",response.getNumberedList().getItem(1));
        Assertions.assertEquals("John",response.getNumberedList().getItem(2));
    }

    @Test
    void InvalidIndexTest() {
        APIResponse response = client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            response.getNumberedList().getItem(0); // Invalid index (0)
        });

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            response.getNumberedList().getItem(3); // Invalid index (3)
        });
    }
}

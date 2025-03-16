package com.comicboys.project.client;

import com.comicboys.project.config.ConfigurationFile;
import org.junit.After;
import org.junit.jupiter.api.*;

import java.util.regex.Pattern;

/*
 * Bad practice to rely on real web data for tests, so instead we pass in what OpenAI would typically say
 * into our tests, this allows us to test for most cases, and have tests for edge cases too (such as
 * false positives)
 */


class APIClientTest {
    @Test
    @Order(1)
    void SuccessfulTextResponseTest() {
//        APIResponse response = new APIResponse(client.sendPrompt("Hello"));

        APIResponse response = new APIResponse("Hello! How can I assist you today?");
        Pattern greetingFormat = Pattern.compile("hello|hi|hey|howdy");


        Assertions.assertFalse(response.isNumberedList()); // Ensure it's not a numbered list
        Assertions.assertTrue(greetingFormat.matcher(response.getTextResponse().toLowerCase()).find());


//        Assertions.assertFalse(response.isNumberedList()); // Ensure it's not a numbered list
//        Assertions.assertEquals("Hello! How can I assist you today?", response.getTextResponse());
    }


    @Test
    @Order(2)
    void SuccessfulNumberedListResponseTest(){

//        APIResponse response = new APIResponse(client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines"));
        APIResponse response = new APIResponse("1. Jane\n2. John\n");

        Assertions.assertTrue(response.isNumberedList());

        Assertions.assertEquals("1. Jane\n2. John\n", response.getNumberedList().toString());
    }

    @Test
    @Order(3)
    void getFromListTest(){
//        APIResponse response = new APIResponse(client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines"));
        APIResponse response = new APIResponse("1. Jane\n2. John\n");

        Assertions.assertEquals("Jane",response.getNumberedList().getItem(1));
        Assertions.assertEquals("John",response.getNumberedList().getItem(2));
    }

    @Test
    @Order(4)
    void InvalidIndexTest() {
//        APIResponse response = new APIResponse(client.sendPrompt("Give me a numbered list like 1. Jane, 2. John using newlines"));
        APIResponse response = new APIResponse("1. Jane\n2. John\n");

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            response.getNumberedList().getItem(0); // Invalid index (0)
        });

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> {
            response.getNumberedList().getItem(3); // Invalid index (3)
        });
    }
}

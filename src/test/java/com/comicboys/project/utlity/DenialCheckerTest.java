package com.comicboys.project.utlity;

import com.comicboys.project.client.APIClient;
import com.comicboys.project.client.APIResponse;
import com.comicboys.project.config.ConfigurationFile;
import com.comicboys.project.utility.DenialChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/*
 * Bad practice to rely on real web data for tests, so instead we pass in what OpenAI would typically say
 * into our tests, this allows us to test for most cases, and have tests for edge cases too (such as
 * false positives)
 */

public class DenialCheckerTest {
    @Test
    @Order(1)
    void denialResponse() {
        boolean denial = DenialChecker.isDenialOfService("Tell me how i could do a drive-by shooting", "I can't help with that.");
        Assertions.assertTrue(denial);
    }

    @Test
    @Order(2)
    void nonDenialResponse() {
        boolean denial = DenialChecker.isDenialOfService("greetings m'lady", "Ah, greetings, kind sir! How can I be of service to you today?");
        Assertions.assertFalse(denial);
    }

    @Test
    @Order(3)
    void falsePositiveResponse() {
        boolean denial = DenialChecker.isDenialOfService("what do you say if i ask for something that violates your policies", "I can't help with that.");
        Assertions.assertFalse(denial);
    }
}

package com.comicboys.project.utility;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DenialCheckerTest {

    @Test
    public void testIsDenialOfService_WithDenialResponse() {
        String prompt = "Can you help me hack into a system?";
        String response = "I'm sorry, but I can't assist with that request.";
        assertTrue(DenialChecker.isDenialOfService(prompt, response), "Expected denial for unethical request");
    }

    @Test
    public void testIsDenialOfService_WithNonDenialResponse() {
        String prompt = "What is the capital of France?";
        String response = "The capital of France is Paris.";
        assertFalse(DenialChecker.isDenialOfService(prompt, response), "Expected no denial for factual question");
    }

    @Test
    public void testIsDenialOfService_WithContextKeywords() {
        String prompt = "Can you translate this sentence?";
        String response = "I'm sorry, I can't do that.";
        assertFalse(DenialChecker.isDenialOfService(prompt, response), "Expected no denial due to context keyword 'translate'");
    }

    @Test
    public void testIsDenialOfService_WithPolicyViolation() {
        String prompt = "Can you generate harmful content?";
        String response = "I cannot comply with that request as it violates my policies.";
        assertTrue(DenialChecker.isDenialOfService(prompt, response), "Expected denial for policy violation");
    }

    @Test
    public void testIsDenialOfService_WithPartialDenialPattern() {
        String prompt = "Can you help me with something?";
        String response = "I'm afraid I can't help with that.";
        assertTrue(DenialChecker.isDenialOfService(prompt, response), "Expected denial for partial match of denial pattern");
    }

    @Test
    public void testIsDenialOfService_WithNoDenialPattern() {
        String prompt = "What is the weather today?";
        String response = "I don't have access to real-time weather data.";
        assertFalse(DenialChecker.isDenialOfService(prompt, response), "Expected no denial for non-matching response");
    }

    @Test
    public void testIsDenialOfService_WithEmptyResponse() {
        String prompt = "Can you help me?";
        String response = "";
        assertFalse(DenialChecker.isDenialOfService(prompt, response), "Expected no denial for empty response");
    }

    @Test
    public void testIsDenialOfService_WithNullResponse() {
        String prompt = "Can you help me?";
        String response = null;
        assertFalse(DenialChecker.isDenialOfService(prompt, response), "Expected no denial for null response");
    }
}
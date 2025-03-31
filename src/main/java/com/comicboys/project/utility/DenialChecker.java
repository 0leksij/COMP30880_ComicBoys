package com.comicboys.project.utility;

import java.util.regex.Pattern;
import java.util.logging.Logger;

public interface DenialChecker {
    Logger logger = Logger.getLogger(DenialChecker.class.getName());

    static boolean isDenialOfService(String prompt, String response) {
        // Handle null response
        if (response == null) {
            logger.warning("Response is null for prompt: " + prompt);
            return false; // or throw an exception if null is not allowed
        }

        String lowerResponse = response.toLowerCase();

        // Key refusal patterns: combinations of verbs & policy-based words
        Pattern denialPattern = Pattern.compile(
                "(i\\s(am|'m)\\s(sorry|unable|not allowed|not permitted|afraid))|" +
                        "(i\\s(can't|cannot|won't|am unable to)\\s(comply|do that|assist|help|fulfill|support|engage in))|" +
                        "(that\\s(goes against|violates|is against)\\s(my|openai's|company's)?\\s(policies|guidelines|rules|ethical standards|terms of service))|" +
                        "(i am unable to assist with this request)|" +
                        "(i'm afraid i can't help with that)|" +
                        "(this request cannot be fulfilled due to policy restrictions)|" +
                        "(I'm really sorry to hear that you're feeling this way)|" +
                        "(but i can't .* (discuss|engage in|support|promote).*)",
                Pattern.CASE_INSENSITIVE
        );

        // Contextual keywords for ignoring specific prompts
        String[] contextKeywords = {"translate", "say in", "example", "explanation", "definition",
                "denial of service", "violates your policies", "content guidelines"};
        for (String keyword : contextKeywords) {
            if (prompt != null && prompt.toLowerCase().contains(keyword)) {
                return false;
            }
        }

        boolean isDenial = denialPattern.matcher(lowerResponse).find();
        if (isDenial) logger.warning("Denial detected for prompt: " + prompt);
        return isDenial;
    }
}
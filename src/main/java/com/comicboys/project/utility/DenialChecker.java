package com.comicboys.project.utility;

import java.util.regex.Pattern;

public class DenialChecker {
    public static boolean isDenialOfService(String prompt, String response) {
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


        if (prompt.toLowerCase().matches(".*(translate|say in|example|explanation|definition|denial of service|violates your policies|content guidelines).*")) {
            return false;
        }
        /*
        // Context-based check: Ignore if the user prompt asks for translation or similar topics
        Pattern contextPattern = Pattern.compile(
                "(translate|say in|example|explanation|definition|denial of service|violates your policies|content guidelines|interpret|rephrase|summarize)",
                Pattern.CASE_INSENSITIVE
        );

        if (contextPattern.matcher(prompt).find()) {
            return false;
        }*/

        return denialPattern.matcher(lowerResponse).find();
    }
}

package com.comicboys.project.client;

import java.util.List;

public class VignetteSchemaPlaceholder implements IVignetteSchema{

    @Override
    public List<String> getLeftPoses() {
        return List.of();
    }
    @Override
    public List<String> getRightPoses() {
        return List.of();
    }
    @Override
    public List<String> getLeftText() {
        return List.of();
    }
    @Override
    public List<String> getCombinedText() {
        return List.of();
    }
    @Override
    public List<String> getSettingSuggestions() {
        return List.of();
    }
    @Override
    public String getRandomLeftPose() {return "";}
    @Override
    public String getRandomRightPose() {return "";}
    @Override
    public String getRandomBackground() {return "";}
    @Override
    public String getRandomCombinedText() {return "";}
    @Override
    public String getRandomLeftText() {return "";}
}

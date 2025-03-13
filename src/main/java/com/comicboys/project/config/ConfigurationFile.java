package com.comicboys.project.config;

import java.net.*;
import java.io.*;


public class ConfigurationFile {
    private int sample;
    public ConfigurationFile() {
        sample = 1;
    }
    public int getSample() {
        return sample;
    }
    public void setSample(int sample) {
        this.sample = sample;
    }
}
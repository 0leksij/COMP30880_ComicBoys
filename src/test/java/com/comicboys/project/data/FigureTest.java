package com.comicboys.project.data;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class FigureTest {
    @Test
    void createFigure(){
        Figure figure = new Figure("Michael Jackson", "yes", "white?", "balding", "white? skin", "short", "frosted tips", "non-existent");
        String expectedOutput = "\nName: Michael Jackson" +
                "\nSex: yes" +
                "\nSkin: white?" +
                "\nHair: balding" +
                "\nBeard: white? skin" +
                "\nHair Length: short" +
                "\nHair Style: frosted tips" +
                "\nLips: non-existent";
        assertEquals(expectedOutput, figure.toString());
    }
    @Test
    void getFeatures(){
        Figure figure = new Figure("Michael Jackson", "yes", "white?", "balding", "white? skin", "short", "frosted tips", "non-existent");
        assertEquals("Michael Jackson", figure.getName());
        assertEquals("yes", figure.getSex());
        assertEquals("white?", figure.getSkin());
        assertEquals("balding", figure.getHair());
        assertEquals("white? skin", figure.getBeard());
        assertEquals("short", figure.getHairLength());
        assertEquals("frosted tips", figure.getHairStyle());
        assertEquals("non-existent", figure.getLips());
    }
}

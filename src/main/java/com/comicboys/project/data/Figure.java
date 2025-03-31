package com.comicboys.project.data;

public class Figure {
    // properties of figure
    private final String name;
    private final String sex;
    private final String skin;
    private final String hair;
    private final String beard;
    private final String hairLength;
    private final String hairStyle;
    private final String lips;
    // create figure
    public Figure(String name, String sex, String skin, String hair, String beard, String hairLength, String hairStyle, String lips) {
        this.name = name;
        this.sex = sex;
        this.skin = skin;
        this.hair = hair;
        this.beard = beard;
        this.hairLength = hairLength;
        this.hairStyle = hairStyle;
        this.lips = lips;
    }
    // getters for figure properties (no setters)
    public String getName() { return name; }
    public String getSex() { return sex; }
    public String getSkin() { return skin; }
    public String getHair() { return hair; }
    public String getBeard() { return beard; }
    public String getHairLength() { return hairLength; }
    public String getHairStyle() { return hairStyle; }
    public String getLips() { return lips; }
    // print out figure properties
    @Override
    public String toString() {
        return  "\nName: " + name +
                "\nSex: " + sex +
                "\nSkin: " + skin +
                "\nHair: " + hair +
                "\nBeard: " + beard +
                "\nHair Length: " + hairLength +
                "\nHair Style: " + hairStyle +
                "\nLips: " + lips;
    }
}

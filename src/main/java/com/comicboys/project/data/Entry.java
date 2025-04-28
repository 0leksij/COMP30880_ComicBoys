package com.comicboys.project.data;

// need abstract class for entry, two implementations: list of string for each column or picking 1 string per column
public abstract class Entry<T> {
    private final T leftPose;
    private final T combinedText;
    private final T leftText;
    private final T rightPose;
    private final T backgrounds;
    public Entry(T leftPose, T combinedText, T leftText, T rightPose, T backgrounds) {
        this.leftPose = leftPose;
        this.combinedText = combinedText;
        this.leftText = leftText;
        this.rightPose = rightPose;
        this.backgrounds = backgrounds;
    }
    public T getLeftPose() { return leftPose; }
    public T getCombinedText() { return combinedText; }
    public T getLeftText() { return leftText; }
    public T getRightPose() { return rightPose; }
    public T getBackgrounds() { return backgrounds; }
    public String toString() {
        return "\n[" + leftPose + ", " + combinedText + ", " +
                leftText + ", " + rightPose + ", " +
                backgrounds + "]";
    }
}

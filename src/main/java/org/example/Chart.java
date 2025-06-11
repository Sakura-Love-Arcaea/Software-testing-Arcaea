package org.example;

public class Chart {
    String songName;
    float constant; // 1 digit after decimal
    int noteCount;
    double[] notes;

    public Chart(String songName, int noteCount, float constant) {
        this.songName = songName;
        this.noteCount = noteCount;
        this.constant = constant;
    }

    public void setNotes(double[] notes) {
        this.notes = notes;
    }

    public double[] getNotes() {
        return notes;
    }
}

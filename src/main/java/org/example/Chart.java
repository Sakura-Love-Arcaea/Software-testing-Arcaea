package org.example;

public class Chart {
    String songName;
    LowiroService lowiroService;

    @Deprecated
    float constant; // 1 digit after decimal
    @Deprecated
    int noteCount;
    @Deprecated
    double[] notes;

    @Deprecated
    public Chart(String songName, int noteCount, float constant) {
        this.songName = songName;
        this.noteCount = noteCount;
        this.constant = constant;
    }

    public Chart(String songName, LowiroService lowiroService) {
        this.songName = songName;
        this.lowiroService = lowiroService;
    }

    @Deprecated
    public void setNotes(double[] notes) {
        this.notes = notes;
    }


    public double getConstant() {
        return lowiroService.getConstant(songName);
    }

    public int getNoteCount() {
        return lowiroService.getNoteCount(songName);
    }

    public double[] getNotes() {
//        return this.notes;
        return lowiroService.getNotes(songName);
    }
}

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

    /**
     * 在本地寫死音符數量和常數，已經棄用，因為真實情況是從伺服器取得，常更改
     * @param songName
     * @param noteCount
     * @param constant
     */
    @Deprecated
    public Chart(String songName, int noteCount, float constant) {
        this.songName = songName;
        this.noteCount = noteCount;
        this.constant = constant;
    }

    /**
     * 使用服務來獲取歌曲的常數和音符數量。
     * @param songName
     * @param lowiroService
     */
    public Chart(String songName, LowiroService lowiroService) {
        this.songName = songName;
        this.lowiroService = lowiroService;
    }

    /**
     * 在本地寫死時間序列，已經棄用，因為真實情況是從伺服器取得，常更改
     * @param notes
     */
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

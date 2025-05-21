package org.example;

public enum Status {
    PM("PM"), //("Pure Memory"),
    FR("F"), //("Full Recall"),
    TC("C"), //("Track Complete"),
    TL("L"); //("Track Lost");

    private final String str;

    Status(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return this.str;
    }
}

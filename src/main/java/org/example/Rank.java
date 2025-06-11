package org.example;

public enum Rank {
    EX_PLUS("EX+"),
    EX("EX"),
    AA("AA"),
    A("A"),
    B("B"),
    C("C"),
    D("D");

    Rank(String s) {
        this.str = s;
    }

    private final String str;

    @Override
    public String toString() {
        return this.str;
    }
}

package org.example;

public interface LowiroService {
    public double getConstant(String songName);
    public int getNoteCount(String songName);
    public double[] getNotes(String songName);
}

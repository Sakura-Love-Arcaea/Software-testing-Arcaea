package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Judgement {
    private final Properties timings = new Properties();
    private final Chart chart;
    private final double[] log;

    public Judgement(Chart chart, double[] log) throws IOException {
        this.chart = chart;
        this.log = log;
        timings.load(new FileInputStream("src/main/java/org/example/app.properties"));
    }

    public int[] getJudgements() {
        int[] judgements = new int[3]; // pure(0), far(1), miss(2)
        double[] notes = chart.getNotes();

        // (xxx - fastMiss - fastFar - fastPure - Just - slowPure - slowFar - xxx)
        double fastPure = Double.parseDouble(timings.getProperty("fast-pure-timing"));
        double slowPure = Double.parseDouble(timings.getProperty("slow-pure-timing"));
        double fastFar = Double.parseDouble(timings.getProperty("fast-far-timing"));
        double slowFar = Double.parseDouble(timings.getProperty("slow-far-timing"));
        double fastMiss = Double.parseDouble(timings.getProperty("fast-miss-timing"));

        int i=0, j=0; // i->chart, j->log
        while (i < notes.length) {
            if (j >= log.length) { // no more input, mark all remaining notes as miss
                judgements[2]++;
                i++; continue;
            }

            if (log[j] < notes[i] - fastMiss) { // ignore when too early (xxx - fastMiss)
                j++; // only skip input
            } else if (log[j] < notes[i] - fastFar) { // miss when too-ooo fast (fastMiss - fastFar)
                judgements[2]++;
                i++; j++;
            } else if (log[j] < notes[i] - fastPure) { // far when too fast (fastFar - fastPure)
                judgements[1]++;
                i++; j++;
            } else if (log[j] < notes[i] + slowPure) { // perfect when in range (fastPure - Just - slowPure)
                judgements[0]++;
                i++; j++;
            } else if (log[j] < notes[i] + slowFar) { // far when too slow (slowPure - slowFar)
                judgements[1]++;
                i++; j++;
            } else { // miss when passed judgement window
                judgements[2]++;
                i++; // miss current note and hold input to next judgement
            }

        }

        return judgements;
    }
}

package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Judgement {
    private final Chart chart;
    private final double[] log;

    private final double fastPure;
    private final double slowPure;
    private final double fastFar;
    private final double slowFar;
    private final double fastMiss;

    public enum Timing {
        Pure, Far, LateMiss, FastMiss, Ignore
    }

    public Judgement(Chart chart, double[] log) throws IOException {
        this.chart = chart;
        this.log = log;
        Properties timings = new Properties();
        timings.load(new FileInputStream("src/main/java/org/example/app.properties"));

        fastPure = Double.parseDouble(timings.getProperty("fast-pure-timing"));
        slowPure = Double.parseDouble(timings.getProperty("slow-pure-timing"));
        fastFar = Double.parseDouble(timings.getProperty("fast-far-timing"));
        slowFar = Double.parseDouble(timings.getProperty("slow-far-timing"));
        fastMiss = Double.parseDouble(timings.getProperty("fast-miss-timing"));
    }

    public Timing getJudgement(double expected, double actual) {
        // (xxx - fastMiss - fastFar - fastPure - Just - slowPure - slowFar - xxx)

        if (actual < expected - fastMiss) { // ignore when too early (xxx - fastMiss)
            return Timing.Ignore; // only skip input
        } else if (actual < expected - fastFar) { // miss when too-ooo fast (fastMiss - fastFar)
            return Timing.FastMiss;
        } else if (actual < expected - fastPure) { // far when too fast (fastFar - fastPure)
            return Timing.Far;
        } else if (actual <= expected + slowPure) { // perfect when in range (fastPure - Just - slowPure)
            return Timing.Pure;
        } else if (actual <= expected + slowFar) { // far when too slow (slowPure - slowFar)
            return Timing.Far;
        } else { // miss when passed judgement window
            return Timing.LateMiss;
        }
    }

    public int[] getJudgements() {
        // pure(0), far(1), miss(2)
        int pure, far, miss;
        pure = far = miss = 0;
        double[] notes = chart.getNotes();

        int i=0, j=0; // i->chart, j->log
        while (i < notes.length) {
            if (j >= log.length) { // no more input, mark all remaining notes as miss
                miss++;
                i++; continue;
            }

            Timing timing = getJudgement(notes[i], log[j]);
            if (timing == Timing.Ignore) { // ignore when way too early (xxx - fastMiss)
                j++; // only skip input
            } else if (timing == Timing.FastMiss) { // miss when too-ooo fast (fastMiss - fastFar)
                miss++;
                i++; j++;
            } else if (timing == Timing.Far) { // far when too fast (fastFar - fastPure)
                far++;
                i++; j++;
            } else if (timing == Timing.Pure) { // perfect when in range (fastPure - Just - slowPure)
                pure++;
                i++; j++;
            } else { // far when too slow (slowPure - slowFar)
                miss++;
                i++; // miss current note and hold input to next judgement
            }

        }

        return new int[] {pure, far, miss};
    }
}

package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class JudgementTest {

    @Test
    void testA() throws IOException {
        double[] notes =   {     1000, 2000, 3000, 4000, 4000};
        double[] playlog = { 500, 750, 1950, 2900, 4003      }; // i,m,p,f,p,m

        Chart chart = new Chart("A", 5, 1);
        chart.setNotes(notes);

        Judgement judgement = new Judgement(chart, playlog);
        int[] result = judgement.getJudgements();

        Record record = new Record(chart, result[0], result[1], result[2]);

        assertEquals("Record{chart=A, score=5000000, potential=0.00, pure=2, far=1, lost=2}", record.toString());

    }

    @Test
    void testGetSingleJudgement() throws IOException {
        double[] notes = {};
        double[] playlog = {};

        Chart chart = new Chart("B", 2, 1);
        chart.setNotes(notes);

        Judgement judgement = new Judgement(chart, playlog);

        assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 950));
        assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1050));
        assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 800));
        assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1200));
        assertEquals(Judgement.Timing.LateMiss, judgement.getJudgement(1000, 1300));
        assertEquals(Judgement.Timing.FastMiss, judgement.getJudgement(1000, 700));
        assertEquals(Judgement.Timing.Ignore, judgement.getJudgement(1000, 600));
    }

    @Test
    void testGetJudgementsAccuracy() throws IOException {
        double[] notes = {1000, 1500, 2000, 2500, 3500, 4000};
        double[] playlog = {950, 1550, 1800, 2700, 3200, 4500};

        Chart chart = new Chart("C", 3, 1);
        chart.setNotes(notes);

        Judgement judgement = new Judgement(chart, playlog);
        int[] result = judgement.getJudgements();

        assertEquals(2, result[0]); // pure
        assertEquals(2, result[1]); // far
        assertEquals(2, result[2]); // miss
    }
}
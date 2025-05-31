package org.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JudgementTest {



    @Test
    void testA() throws IOException {
        double[] notes = {      1000, 2000, 3000, 4000, 4000};
        double[] playlog = {500, 750, 1950, 2900, 4003      }; // i,m,p,f,p,m

        Chart chart = new Chart("A", 5, 1);
        chart.setNotes(notes);

        Judgement judgement = new Judgement(chart, playlog);
        int[] result = judgement.getJudgements();

        Record record = new Record(chart, result[0], result[1], result[2]);

        assertEquals("Record{chart=A, score=5000000, potential=0.00, pure=2, far=1, lost=2}", record.toString());

    }
}
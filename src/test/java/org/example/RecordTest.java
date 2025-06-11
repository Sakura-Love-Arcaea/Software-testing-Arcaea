package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RecordTest {

    @Test
    public void testScoreCalculation() {
        Chart chart = new Chart("Test Song", 100, 1.5f);
        Record record = new Record(chart, 90, 5, 5); // 90 pure, 5 far, 5 lost

        double base = 10000000.0 / 100;
        int expectedScore = (int) (base * 90 + base / 2 * 5);
        assertEquals(expectedScore, record.getScore());
    }

    @Test
    public void testScoreCalculation2() {
        Chart chart = new Chart("Test Song", 100, 1.5f);
        Record record = new Record(chart, 98, 0, 2); // 90 pure, 5 far, 5 lost

        double base = 10000000.0 / 100;
        int expectedScore = (int) (base * 98 + base / 2 * 0);
        assertEquals(expectedScore, record.getScore());
    }

    @Test
    public void testPotentialOver1000W() {
        Chart chart = new Chart("Perfect Play", 100, 9.0f);
        Record record = new Record(chart, 100, 0, 0);

        assertEquals(11.0, record.getPotential()); // 9.0 + 2.0
    }

    @Test
    public void testPotentialBetween980WAnd1000W() {
        Chart chart = new Chart("Near Perfect", 100, 9.0f);
        // score = 9900000
        int pure = 99;
        int far = 2;
        int lost = -1; // illegal, but let's calculate correct values first
        int totalNotes = 100;
        int calculatedLost = totalNotes - (pure + far); // make lost = 0
        Record record = new Record(chart, pure, far, calculatedLost);

        double expected = 9.0 + 1.0 + (record.getScore() - 9800000) / 200000.0;
        assertEquals(expected, record.getPotential(), 0.01);
    }

    @Test
    public void testRank() {
        Chart chart = new Chart("Rank Test", 100, 8.0f);
        Record record = new Record(chart, 95, 5, 0); // will score 9750000
        assertEquals(Rank.AA, record.getRank());
    }

    @Test
    public void testRank2() {
        Chart chart = new Chart("Rank Test", 100, 8.0f);
        Record record = new Record(chart, 90, 0, 10);
        assertEquals(Rank.B, record.getRank());
    }

    @Test
    public void testRank3() {
        Chart chart = new Chart("Rank Test", 100, 8.0f);
        Record record = new Record(chart, 87, 0, 13);
        assertEquals(Rank.C, record.getRank());
    }

    @Test
    public void testStatus_PM() {
        Chart chart = new Chart("Status PM", 100, 7.0f);
        Record record = new Record(chart, 100, 0, 0);
        assertEquals(Status.PM, record.getStatus());
    }

    @Test
    public void testStatus_FR() {
        Chart chart = new Chart("Status FR", 100, 7.0f);
        Record record = new Record(chart, 90, 10, 0);
        assertEquals(Status.FR, record.getStatus());
    }

    @Test
    public void testStatus_TC() {
        Chart chart = new Chart("Status TC", 100, 7.0f);
        Record record = new Record(chart, 80, 10, 10);
        assertEquals(Status.TC, record.getStatus());
    }

    @Test
    public void testInvalidNoteCountThrowsException() {
        Chart chart = new Chart("Error Case", 100, 5.0f);
        assertThrows(IllegalArgumentException.class, () -> {
            new Record(chart, 90, 5, 10); // 90+5+10 = 105 != 100
        });
    }

    @Test
    public void testtoString() {
        Chart chart = new Chart("Rank Test", 100, 8.0f);
        Record record = new Record(chart, 95, 5, 0); // will score 9750000
        double potential = record.getPotential();
        String text = String.format("Record{chart=%s, score=%d, potential=%.2f, pure=%d, far=%d, lost=%d}",
                chart.songName, 9750000, potential, 95, 5, 0);
        assertEquals(text, record.toString());
    }
}
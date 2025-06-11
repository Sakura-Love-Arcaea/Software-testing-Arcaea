package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecordTest {

    private LowiroService lowiroService;

    @BeforeEach
    public void setup() {
        lowiroService = mock(LowiroService.class);
    }

    @Test
    public void testScoreCalculation() {
        String songName = "testScoreCalculation";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(1.5);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 90, 5, 5); // 90 pure, 5 far, 5 lost

        double base = 10000000.0 / 100;
        int expectedScore = (int) (base * 90 + base / 2 * 5);
        assertEquals(expectedScore, record.getScore());
    }

    @Test
    public void testScoreCalculation2() {
        String songName = "testScoreCalculation2";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(1.5);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 98, 0, 2);

        double base = 10000000.0 / 100;
        int expectedScore = (int) (base * 98 + base / 2 * 0);
        assertEquals(expectedScore, record.getScore());
    }

    @Test
    public void testPotentialOver1000W() {
        String songName = "Perfect Play";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(9.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 100, 0, 0);

        assertEquals(11.0, record.getPotential()); // 9.0 + 2.0
    }

    @Test
    public void testPotentialBetween980WAnd1000W() {
        String songName = "Near Perfect";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(9.0);

        Chart chart = new Chart(songName, lowiroService);
        // score = 9900000
        int pure = 99;
        int far = 2;
        int totalNotes = 100;
        int calculatedLost = totalNotes - (pure + far); // make lost = 0
        Record record = new Record(chart, pure, far, calculatedLost);

        double expected = 9.0 + 1.0 + (record.getScore() - 9800000) / 200000.0;
        assertEquals(expected, record.getPotential(), 0.01);
    }

    @Test
    public void testRank() {
        String songName = "Rank Test";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(8.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 95, 5, 0); // will score 9750000
        assertEquals(Rank.AA, record.getRank());
    }

    @Test
    public void testRank2() {
        String songName = "Rank Test 2";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(8.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 90, 0, 10);
        assertEquals(Rank.B, record.getRank());
    }

    @Test
    public void testRank3() {
        String songName = "Rank Test 3";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(8.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 87, 0, 13);
        assertEquals(Rank.C, record.getRank());
    }

    @Test
    public void testStatus_PM() {
        String songName = "Status PM";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(7.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 100, 0, 0);
        assertEquals(Status.PM, record.getStatus());
    }

    @Test
    public void testStatus_FR() {
        String songName = "Status FR";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(7.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 90, 10, 0);
        assertEquals(Status.FR, record.getStatus());
    }

    @Test
    public void testStatus_TC() {
        String songName = "Status TC";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(7.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 80, 10, 10);
        assertEquals(Status.TC, record.getStatus());
    }

    @Test
    public void testInvalidNoteCountThrowsException() {
        String songName = "Error Case";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(5.0);

        Chart chart = new Chart(songName, lowiroService);
        assertThrows(IllegalArgumentException.class, () -> {
            new Record(chart, 90, 5, 10); // 90+5+10 = 105 != 100
        });
    }

    @Test
    public void testToString() {
        String songName = "ToString Test";
        when(lowiroService.getNoteCount(songName)).thenReturn(100);
        when(lowiroService.getConstant(songName)).thenReturn(8.0);

        Chart chart = new Chart(songName, lowiroService);
        Record record = new Record(chart, 95, 5, 0);
        double potential = record.getPotential();
        String text = String.format("Record{chart=%s, score=%d, potential=%.2f, pure=%d, far=%d, lost=%d}",
                songName, record.getScore(), potential, 95, 5, 0);
        assertEquals(text, record.toString());
    }
}
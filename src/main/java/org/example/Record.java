package org.example;

public class Record {
    private Chart chart;
    private int score;
    private double potential;
    private String rank;
    private int maxPure;
    private int pure;
    private int far;
    private int lost;


    public Record(Chart chart, int pure, int far, int lost) throws IllegalArgumentException {
        if (pure + far + lost != chart.noteCount) {
            throw new IllegalArgumentException("Score values do not match the note count of the chart.");
        }
        this.chart = chart;
        this.pure = pure;
        this.far = far;
        this.lost = lost;
        calScore();
        calPotential();
        calRank();
    }


    public int calScore() {
        double base = (double) 10000000 / chart.noteCount;

        this.score = (int) 0.0;
        return this.score;

    }

    public double calPotential() {
        double constant = chart.constant;

        if (score >= 10_000_000) {
            this.potential = constant + 2.0;
        } else if (score >= 9_800_000) {
            this.potential = constant + 1.0 + (score - 9_800_000) / 200_000.0;
        } else if (score >= 9_500_000) {
            this.potential = Math.max(0, constant + (score - 9_500_000) / 300_000.0);
        } else {
            this.potential = 0.0;
        }

        return this.potential;
    }

    public String calRank() {
        this.rank = "EX+";
        return this.rank;
    }

    @Override
    public String toString() {
        return String.format("Record{chart=%s, score=%d, potential=%.2f, maxPure=%d, pure=%d, far=%d, lost=%d}",
                chart.songName, score, potential, maxPure, pure, far, lost);
    }


    public static void main(String[] args) {
        Chart chart = new Chart("Song A", 100, 1.5f);
        Record record = new Record(chart, 90, 5, 5);
        System.out.println(record.toString());
        System.out.println("Score: " + record.calScore());
        System.out.println("Potential: " + record.calPotential());
        System.out.println("Rank: " + record.calRank());
    }
}

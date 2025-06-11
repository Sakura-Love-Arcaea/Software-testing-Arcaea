package org.example;

public class Record {
    private final Chart chart;
    private int score;
    private double potential;
    private Rank rank;
    private Status status;
    private final int pure;
    private final int far;
    private final int lost;
//    TODO
//    private int maxPure;

    public Record(Chart chart, int pure, int far, int lost) throws IllegalArgumentException {
        if (pure + far + lost != chart.getNoteCount()) {
            throw new IllegalArgumentException("Score values do not match the note count of the chart.");
        }
        this.chart = chart;
        this.pure = pure;
        this.far = far;
        this.lost = lost;
        calScore();
        calPotential();
        calRank();
        calStatus();
    }


    public void calScore() {
        double base = (double) 10000000 / chart.getNoteCount();

        this.score = (int) (base * pure + base/2 * far);
    }

    public void calPotential() {
        double constant = chart.getConstant();

        if (score >= 10_000_000) {
            this.potential = constant + 2.0;
        } else if (score >= 9_800_000) {
            this.potential = constant + 1.0 + (score - 9_800_000) / 200_000.0;
        } else {
            this.potential = Math.max(0, constant + (score - 9_500_000) / 300_000.0);
        }
    }

    public void calRank() {
        if (this.score >=  9_900_000) {
            this.rank = Rank.EX_PLUS;
        } else if (this.score >=  9_800_000) {
            this.rank = Rank.EX;
        } else if (this.score >=  9_500_000) {
            this.rank = Rank.AA;
        } else if (this.score >=  9_200_000) {
            this.rank = Rank.A;
        } else if (this.score >=  8_900_000) {
            this.rank = Rank.B;
        } else if (this.score >=  8_600_000) {
            this.rank = Rank.C;
        } else {
            this.rank = Rank.D;
        }
    }

    public void calStatus() {
        if (this.far == 0 && this.lost == 0) {
            this.status = Status.PM;
        } else if (this.lost == 0) {
            this.status = Status.FR;
        } else {
            this.status = Status.TC;
        }

    }

    public int getScore() {
        return score;
    }

    public double getPotential() {
        return potential;
    }

    public Rank getRank() {
        return rank;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("Record{chart=%s, score=%d, potential=%.2f, pure=%d, far=%d, lost=%d}",
                chart.songName, score, potential, pure, far, lost);
    }


    public static void main(String[] args) {
        Chart chart = new Chart("Song A", 100, 1.5f);
        Record record = new Record(chart, 90, 5, 5);
        System.out.println(record);
        System.out.println("Score: " + record.getScore());
        System.out.println("Potential: " + record.getPotential());
        System.out.println("Rank: " + record.getRank());
        System.out.println("Status: " + record.getStatus());
    }
}

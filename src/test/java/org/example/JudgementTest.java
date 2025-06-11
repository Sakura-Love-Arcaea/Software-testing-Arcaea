package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JudgementTest {

    @Nested
    @DisplayName("Single Judgement Test")
    class SingleJudgementTest {
        private static Judgement judgement;

        @BeforeAll
        static void setup() {
            judgement = new Judgement(new Chart("SingleJudgementTest", 0, 1), new double[]{}); // blank chart for setup
        }

        @Test
        void testIgnore() {
            int offset = -500; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Ignore, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testFastMiss() {
            int offset = -300; // 與expected timing的偏移量，設定為Ignore < offset < fastMiss
            assertEquals(Judgement.Timing.FastMiss, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInFastFarRangeShouldFar() {
            int offset = -200; // 與expected timing的偏移量，設定為fastMiss < offset < fastFar
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInFastPureRangeShouldBePure() {
            int offset = -50; // 與expected timing的偏移量，設定為fastFar < offset < fastPure
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInSlowPureShouldBePure() {
            int offset = 50; // 與expected timing的偏移量，設定為fastPure < offset < slowPure
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInSlowFarRangeShouldBeFar() {
            int offset = 200; // 與expected timing的偏移量，設定為slowPure < offset < slowFar
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testTooLateShouldBeLateMiss() {
            int offset = 300; // 與expected timing的偏移量，設定為slowFar < offset
            assertEquals(Judgement.Timing.LateMiss, judgement.getJudgement(1000, 1000 + offset));
        }
    }

    @Nested
    @DisplayName("Multi Judgement Test")
    class MultiJudgementsTest {
        private static LowiroService lowiroService;
        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
        }

        @Test
        void testJudgements() {
            String songName = "MultiJudgementsTest";
            when(lowiroService.getNotes(songName)).thenReturn(new double[]{1000, 1500, 2000, 2500, 3500, 4000});
            double[] playlog = {950, 1550, 1800, 2700, 3200, 4500};

            Chart chart = new Chart("MultiJudgementsTest", lowiroService);

            Judgement judgement = new Judgement(chart, playlog);

            int[] result = judgement.getJudgements();

            assertEquals(2, result[0]); // pure
            assertEquals(2, result[1]); // far
            assertEquals(2, result[2]); // miss
        }
    }
}
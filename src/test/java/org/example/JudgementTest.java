package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        private static Chart chart;
        @BeforeAll
        static void setup() {
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000}); // mock notes
            Chart testChart = new Chart("Test", lowiroService);
            chart = testChart;
        }

        @ParameterizedTest
        @CsvSource({
                // Ignore区间组合
                "-301, -550, 0, 0, 1",   // Ignore + Ignore
                "-301, -350, 0, 0, 1",   // Ignore + FastMiss
                "-301, -150, 0, 1, 0",   // Ignore + Far(快)
                "-301, 0,    1, 0, 0",      // Ignore + Pure
                "-301, 150,  0, 1, 0",    // Ignore + Far(慢)
                "-301, 350,  0, 0, 1",    // Ignore + LateMiss

                // FastMiss区间组合
                "-201, -320, 0, 0, 1",   // FastMiss + FastMiss
                "-201, -150, 0, 0, 1",   // FastMiss + Far(快)
                "-201, 0,    0, 0, 1",      // FastMiss + Pure
                "-201, 150,  0, 0, 1",    // FastMiss + Far(慢)
                "-201, 350,  0, 0, 1",    // FastMiss + LateMiss

                // Far(快)区间组合
                "-150, -120, 0, 1, 0",   // Far(快) + Far(快)
                "-150, 0,    0, 1, 0",      // Far(快) + Pure
                "-150, 150,  0, 1, 0",    // Far(快) + Far(慢)
                "-150, 350,  0, 1, 0",    // Far(快) + LateMiss

                // Pure区间组合
                "0, 30,  1, 0, 0",        // Pure + Pure
                "0, 150, 1, 0, 0",       // Pure + Far(慢)
                "0, 350, 1, 0, 0",       // Pure + LateMiss

                // Far(慢)区间组合
                "150, 180, 0, 1, 0",     // Far(慢) + Far(慢)
                "150, 350, 0, 1, 0",     // Far(慢) + LateMiss

                // LateMiss区间组合
                "201, 400, 0, 0, 1"      // LateMiss + LateMiss
        })
        void testDoubleHit(double hit1, double hit2, int pure, int far, int miss) {
            Judgement judgement = new Judgement(chart, new double[]{1000+hit1, 1000+hit2});
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(pure, results[0], "Pure count mismatch"),
                    () -> assertEquals(far, results[1], "Far count mismatch"),
                    () -> assertEquals(miss, results[2], "Miss count mismatch")
            );

        }
    }
}
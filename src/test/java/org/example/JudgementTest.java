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
    @DisplayName("OneNoteOneHit Test")
    class OneNoteOneHitTest {
        private static Judgement judgement;

        @BeforeAll
        static void setup() {
            judgement = new Judgement(null, null); // blank chart for setup
            
        }

        @Test
        void testIgnore() {
            int offset = -500; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Ignore, judgement.getJudgement(1000, 1000 + offset));
            offset = -400; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Ignore, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testFastMiss() {
            int offset = -300; // 與expected timing的偏移量，設定為Ignore < offset < fastMiss
            assertEquals(Judgement.Timing.FastMiss, judgement.getJudgement(1000, 1000 + offset));
            offset = -250; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.FastMiss, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInFastFarRangeShouldFar() {
            int offset = -200; // 與expected timing的偏移量，設定為fastMiss < offset < fastFar
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
            offset = -100; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInFastPureRangeShouldBePure() {
            int offset = -50; // 與expected timing的偏移量，設定為fastFar < offset < fastPure
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
            offset = -1; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInSlowPureShouldBePure() {
            int offset = 1; // 與expected timing的偏移量，設定為fastPure < offset < slowPure
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
            offset = 50; // 與expected timing的偏移量，設定為大於 fastMiss的值
            assertEquals(Judgement.Timing.Pure, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testInSlowFarRangeShouldBeFar() {
            int offset = 100; // 與expected timing的偏移量，設定為slowPure < offset < slowFar
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
            offset = 200; // 與expected timing的偏移量，設定為slowPure < offset < slowFar
            assertEquals(Judgement.Timing.Far, judgement.getJudgement(1000, 1000 + offset));
        }

        @Test
        void testTooLateShouldBeLateMiss() {
            int offset = 250; // 與expected timing的偏移量，設定為slowFar < offset
            assertEquals(Judgement.Timing.LateMiss, judgement.getJudgement(1000, 1000 + offset));
            offset = 300; // 與expected timing的偏移量，設定為slowPure < offset < slowFar
            assertEquals(Judgement.Timing.LateMiss, judgement.getJudgement(1000, 1000 + offset));
        }
    }

    @Nested
    @DisplayName("OneNoteTwoHit Test")
    class OneNoteTwoHitTest {
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
                // Ignore heading
                "-302, -301, 0, 0, 1",    // Ignore + Ignore
                "-301, -201, 0, 0, 1",    // Ignore + FastMiss
                "-301, -150, 0, 1, 0",    // Ignore + Far(fast)
                "-301, 0,    1, 0, 0",    // Ignore + Pure
                "-301, 150,  0, 1, 0",    // Ignore + Far(slow)
                "-301, 201,  0, 0, 1",    // Ignore + LateMiss

                // FastMiss heading
                "-202, -201, 0, 0, 1",    // FastMiss + FastMiss
                "-201, -150, 0, 0, 1",    // FastMiss + Far(fast)
                "-201, 0,    0, 0, 1",    // FastMiss + Pure
                "-201, 150,  0, 0, 1",    // FastMiss + Far(slow)
                "-201, 201,  0, 0, 1",    // FastMiss + LateMiss

                // Far(fast) heading
                "-151, -150, 0, 1, 0",    // Far(fast) + Far(fast)
                "-150, 0,    0, 1, 0",    // Far(fast) + Pure
                "-150, 150,  0, 1, 0",    // Far(fast) + Far(slow)
                "-150, 201,  0, 1, 0",    // Far(fast) + LateMiss

                // Pure heading
                "-1, 0,  1, 0, 0",        // Pure + Pure
                "0, 150, 1, 0, 0",        // Pure + Far(slow)
                "0, 201, 1, 0, 0",        // Pure + LateMiss

                // Far(slow) heading
                "149, 150, 0, 1, 0",     // Far(slow) + Far(slow)
                "150, 201, 0, 1, 0",     // Far(slow) + LateMiss

                // LateMiss heading
                "201, 202, 0, 0, 1"      // LateMiss + LateMiss
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

    @Nested
    @DisplayName("TwoNotesOneHitOverlap Test")
    class TwoNotesOneHitOverlapTest {

        @Test
        void secondFastMissUnderFirstLateMiss() {
            // 1. hit first's late miss
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(2); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000 + 300 + 300}); // second's fast miss under first's late miss, locate second's +0
            Chart testChart = new Chart("Test", lowiroService);
            Judgement judgement = new Judgement(testChart, new double[]{1000 + 201}); // hit first's late miss
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, results[0], "Pure count mismatch"),
                    () -> assertEquals(0, results[1], "Far count mismatch"),
                    () -> assertEquals(2, results[2], "Miss count mismatch") // both notes missed
            );
        }

        @Test
        void secondFastMissUnderFirstLateFar() {
            // 1. hit first's late far
            // 2. hit first's late miss

            // 1.
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(2); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000 + 300 + 200}); // second's fast miss under first's late far, locate second's +0
            Chart testChart = new Chart("Test", lowiroService);
            Judgement judgement = new Judgement(testChart, new double[]{1000 + 51}); // hit first's late far
            int[] resultsHitLateFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateFar[1], "Far count mismatch"), // first's late far
                    () -> assertEquals(1, resultsHitLateFar[2], "Miss count mismatch") // first's late miss
            );

            // 2.
            judgement = new Judgement(testChart, new double[]{1000 + 201}); // hit first's late miss
            int[] resultsHitLateMiss = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateMiss[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitLateMiss[1], "Far count mismatch"),
                    () -> assertEquals(2, resultsHitLateMiss[2], "Miss count mismatch") // both notes missed
            );


        }

        @Test
        void secondFastMissUnderFirstPure() {
            // 1. hit first's pure
            // 2. hit first's late far
            // 3. hit first's late miss
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(2); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000 + 300 + 50}); // second's fast miss under first's pure, locate second's +0
            Chart testChart = new Chart("Test", lowiroService);
            Judgement judgement = new Judgement(testChart, new double[]{1000}); // hit first's pure
            // 1. hit first's pure
            int[] resultsHitPure = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(1, resultsHitPure[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitPure[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitPure[2], "Miss count mismatch") // first's late miss
            );

            // 2. hit first's late far
            judgement = new Judgement(testChart, new double[]{1000 + 51}); // hit first's late far
            int[] resultsHitLateFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateFar[1], "Far count mismatch"), // first's late far
                    () -> assertEquals(1, resultsHitLateFar[2], "Miss count mismatch") // first's late miss
            );

            // 3. hit first's late miss
            judgement = new Judgement(testChart, new double[]{1000 + 201}); // hit first's late miss
            int[] resultsHitLateMiss = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateMiss[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateMiss[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitLateMiss[2], "Miss count mismatch") // both notes missed
            );


        }

        @Test
        void secondFastMissUnderFirstFastFar() {
            // 1. hit first's fast far
            // 2. hit first's pure
            // 3. hit first's late far
            // 4. hit first's late miss
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(2); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000 + 300 - 50}); // second's fast miss under first's fast far, locate second's +0
            Chart testChart = new Chart("Test", lowiroService);

            // 1. hit first's fast far
            Judgement judgement = new Judgement(testChart, new double[]{1000 - 51}); // hit first's fast far
            int[] resultsHitFastFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitFastFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitFastFar[1], "Far count mismatch"), // first's fast far
                    () -> assertEquals(1, resultsHitFastFar[2], "Miss count mismatch") // first's late miss
            );
            // 2. hit first's pure
            judgement = new Judgement(testChart, new double[]{1000}); // hit first's pure
            int[] resultsHitPure = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(1, resultsHitPure[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitPure[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitPure[2], "Miss count mismatch") // first's late miss
            );
            // 3. hit first's late far
            judgement = new Judgement(testChart, new double[]{1000 + 51}); // hit first's late far
            int[] resultsHitLateFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateFar[1], "Far count mismatch"), // first's late far
                    () -> assertEquals(1, resultsHitLateFar[2], "Miss count mismatch") // first's late miss
            );
            // 4. hit first's late miss
            judgement = new Judgement(testChart, new double[]{1000 + 201}); // hit first's late miss
            int[] resultsHitLateMiss = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(1, resultsHitLateMiss[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitLateMiss[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitLateMiss[2], "Miss count mismatch") // first's late miss
            );
        }

        @Test
        void secondFastMissUnderFirstFastMiss() {
            // 1. hit first's fast miss
            // 2. hit first's fast far
            // 3. hit first's pure
            // 4. hit first's late far
            // 5. hit first's late miss
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(2); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000 + 300 - 200}); // second's fast miss under first's fast miss, locate second's +0
            Chart testChart = new Chart("Test", lowiroService);
            // 1. hit first's fast miss
            Judgement judgement = new Judgement(testChart, new double[]{1000 - 201}); // hit first's fast miss
            int[] resultsHitFastMiss = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitFastMiss[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitFastMiss[1], "Far count mismatch"), // first's fast miss
                    () -> assertEquals(2, resultsHitFastMiss[2], "Miss count mismatch") // first's late miss
            );
            // 2. hit first's fast far
            judgement = new Judgement(testChart, new double[]{1000 - 51}); // hit first's fast far
            int[] resultsHitFastFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitFastFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitFastFar[1], "Far count mismatch"), // first's fast far
                    () -> assertEquals(1, resultsHitFastFar[2], "Miss count mismatch") // first's late miss
            );
            // 3. hit first's pure
            judgement = new Judgement(testChart, new double[]{1000}); // hit first's pure
            int[] resultsHitPure = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(1, resultsHitPure[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultsHitPure[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitPure[2], "Miss count mismatch") // first's late miss
            );
            // 4. hit first's late far
            judgement = new Judgement(testChart, new double[]{1000 + 51}); // hit first's late far
            int[] resultsHitLateFar = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateFar[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateFar[1], "Far count mismatch"), // first's late far
                    () -> assertEquals(1, resultsHitLateFar[2], "Miss count mismatch") // first's late miss
            );
            // 5. hit first's late miss
            judgement = new Judgement(testChart, new double[]{1000 + 201}); // hit first's late miss
            int[] resultsHitLateMiss = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, resultsHitLateMiss[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultsHitLateMiss[1], "Far count mismatch"),
                    () -> assertEquals(1, resultsHitLateMiss[2], "Miss count mismatch") // first's late miss
            );
        }

    }

    @Nested
    @DisplayName("Density Test")
    class DensityTest {
        private static Chart chart;

        @BeforeAll
        static void setup() {
            LowiroService lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(10); // mock note count
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{
                    1000, 1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090
            }); // mock notes
            Chart testChart = new Chart("DenseNotes", lowiroService);
            chart = testChart;
        }

        @Test
        void allpure() {
            Judgement judgement = new Judgement(chart, new double[]{
                    1000, 1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090
            });
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(10, results[0], "Pure count mismatch"),
                    () -> assertEquals(0, results[1], "Far count mismatch"),
                    () -> assertEquals(0, results[2], "Miss count mismatch")
            );
        }

        @Test
        void allLateFar() {
            Judgement judgement = new Judgement(chart, new double[]{
                    1200, 1210, 1220, 1230, 1240, 1250, 1260, 1270, 1280, 1290
            });
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, results[0], "Pure count mismatch"),
                    () -> assertEquals(10, results[1], "Far count mismatch"),
                    () -> assertEquals(0, results[2], "Miss count mismatch")
            );
        }

        void allFastFar() {
            Judgement judgement = new Judgement(chart, new double[]{
                    800, 810, 820, 830, 840, 850, 860, 870, 880, 890
            });
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(0, results[0], "Pure count mismatch"),
                    () -> assertEquals(10, results[1], "Far count mismatch"),
                    () -> assertEquals(0, results[2], "Miss count mismatch")
            );
        }
        
        @Test
        void missingHead() {
            Judgement judgement = new Judgement(chart, new double[]{
                    1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090
            });
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(9, results[0], "Pure count mismatch"),
                    () -> assertEquals(0, results[1], "Far count mismatch"),
                    () -> assertEquals(1, results[2], "Miss count mismatch")
            );
        }

        @Test
        void fastFarringHead() {
            Judgement judgement = new Judgement(chart, new double[]{
                    1000 - 51, 1010, 1020, 1030, 1040, 1050, 1060, 1070, 1080, 1090
            });
            int[] results = judgement.getJudgements();
            assertAll(
                    () -> assertEquals(9, results[0], "Pure count mismatch"),
                    () -> assertEquals(1, results[1], "Far count mismatch"),
                    () -> assertEquals(0, results[2], "Miss count mismatch")
            );
        }
    }
}
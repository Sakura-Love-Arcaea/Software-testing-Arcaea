package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JudgementMultiTest {
    @Nested
    @DisplayName("Overlap to Miss Head")
    class MissHeadOverlap {
        private static Chart chart;
        private static LowiroService lowiroService;

        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            chart = new Chart("Test", lowiroService);
        }

        @Test
        void overlapToMissHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000});

            double[] log = {699,699,1000,1000};// ignore*2 and then pure*2
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{700,700,1000,1000};
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(2, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{701,701,1000,1000};
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(2, resultNorm[2], "Miss count mismatch")
            );
        }

        @Test
        void overlapToFarHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1100});

            double[] log = {799, 799, 1100};// miss, ignore, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{800, 800};// far, miss
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{801, 801};// far, miss
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToPureHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1250});

            double[] log = {949, 949, 1250};// far, ignore, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{950, 950};// pure, miss
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{951, 951};// pure, miss
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );
        }

        @Test
        void overlapToPureTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1350});

            double[] log = {1049, 1049, 1350};// pure, ignore, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1050, 1050};// pure, miss
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1051, 1051};// far, miss
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToFarTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1500});

            double[] log = {1199, 1199, 1500};// far, ignore, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1200, 1200};// far, miss
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1201, 1201};// miss, miss
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(2, resultNorm[2], "Miss count mismatch")
            );
        }
    }

    @Nested
    @DisplayName("Overlap to Far Head")
    class FarHeadOverlap {
        private static Chart chart;
        private static LowiroService lowiroService;

        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            chart = new Chart("Test", lowiroService);
        }

        @Test
        void overlapToFarHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000});

            double[] log = {799, 799};// miss, miss
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(2, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{800, 800};// far, far
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{801, 801};// far, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToPureHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1150});

            double[] log = {949, 949};// far, miss
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{950, 950};// pure, far
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{951, 951};// pure, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );
        }

        @Test
        void overlapToPureTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1250});

            double[] log = {1049, 1049};// pure, miss
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1050, 1050};// pure, far
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1051, 1051};// far, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToFarTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1400});

            double[] log = {1199, 1199};// far, miss
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(1, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1200, 1200};// far, far
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1201, 1201};// miss, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );
        }
    }

    @Nested
    @DisplayName("Overlap to Pure Head")
    class PureHeadOverlap {
        private static Chart chart;
        private static LowiroService lowiroService;

        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            chart = new Chart("Test", lowiroService);
        }

        @Test
        void overlapToPureHead() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000});

            double[] log = {949, 949};// far, far
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{950, 950};// pure, pure
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{951, 951};// pure, pure
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );
        }

        @Test
        void overlapToPureTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1100});

            double[] log = {1049, 1049};// pure, far
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1050, 1050};// pure, pure
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1051, 1051};// far, pure
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToFarTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1250});

            double[] log = {1199, 1199};// far, far
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1200, 1200};// far, pure
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1201, 1201};// miss, pure
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );
        }
    }

    @Nested
    @DisplayName("Overlap to Pure Tail")
    class PureTailOverlap {
        private static Chart chart;
        private static LowiroService lowiroService;

        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            chart = new Chart("Test", lowiroService);
        }

        @Test
        void overlapToPureTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000});

            double[] log = {1049, 1049};// pure, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1050, 1050};// pure, pure
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(2, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1051, 1051};// far, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(0, resultNorm[2], "Miss count mismatch")
            );

        }

        @Test
        void overlapToFarTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1150});

            double[] log = {1199, 1199};// far, pure
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1200, 1200};// far, pure
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(1, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1201, 1201};// miss, far
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(1, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(1, resultNorm[2], "Miss count mismatch")
            );
        }
    }

    @Nested
    @DisplayName("Overlap to Far Tail")
    class FarTailOverlap {
        private static Chart chart;
        private static LowiroService lowiroService;

        @BeforeAll
        static void setup() {
            lowiroService = mock(LowiroService.class);
            when(lowiroService.getConstant(anyString())).thenReturn(1.0); // mock constant
            when(lowiroService.getNoteCount(anyString())).thenReturn(1); // mock note count
            chart = new Chart("Test", lowiroService);
        }

        @Test
        void overlapToFarTail() {
            when(lowiroService.getNotes(anyString())).thenReturn(new double[]{1000, 1000});

            double[] log = {1199, 1199};// far, far
            int[] resultMin_m = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin_m[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin_m[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin_m[2], "Miss count mismatch")
            );

            log = new double[]{1200, 1200};// far, far
            int[] resultMin = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultMin[0], "Pure count mismatch"),
                    () -> assertEquals(2, resultMin[1], "Far count mismatch"),
                    () -> assertEquals(0, resultMin[2], "Miss count mismatch")
            );

            log = new double[]{1201, 1201};// miss, miss
            int[] resultNorm = new Judgement(chart, log).getJudgements();
            assertAll(
                    () -> assertEquals(0, resultNorm[0], "Pure count mismatch"),
                    () -> assertEquals(0, resultNorm[1], "Far count mismatch"),
                    () -> assertEquals(2, resultNorm[2], "Miss count mismatch")
            );
        }
    }
}

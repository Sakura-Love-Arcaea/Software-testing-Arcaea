package org.example;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.*;

import java.util.regex.Pattern;

public class InputCaptureGUITest {

    private static Robot robot;
    private FrameFixture window;

    @BeforeAll
    public static void setUpOnce() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }

    @BeforeEach
    public void setUp() {
        Chart chart = new Chart("Test", new MockLowiroService());
        chart.setNotes(new double[]{500, 1000, 1500});
        InputCaptureGUI gui = new InputCaptureGUI(chart);
        window = new FrameFixture(robot, gui);
        window.show(); // 顯示 GUI
    }

    @Test
    public void testCaptureButtonClick() {
        window.button("startButton").click();
        window.button("captureButton").click();
        window.button("resetButton").click();
        window.button("judgeButton").click();

        Assertions.assertTrue(window.textBox("capturedArea").text().contains("捕捉點 1"));
    }

    @Test
    public void testStartButtonEnablesCapture() {
        // 剛開啟時，captureButton 可能是 disable 狀態（根據你 GUI 設計）
        window.button("captureButton").requireDisabled();

        // 點擊 startButton 之後，captureButton 應該被啟用
        window.button("startButton").click();
        window.button("captureButton").requireEnabled();
    }

    @Test
    public void testCaptureAddsPointsAndUpdatesText() {
        window.button("startButton").click();

        // 第一次點擊 captureButton，應該新增「捕捉點 1」
        window.button("captureButton").click();
        //window.textBox("capturedArea").requireText(Pattern.compile(".*捕捉點 1.*"));
        assertTrue(window.textBox("capturedArea").text().contains("捕捉點 1"));

        // 第二次點擊 captureButton，應該新增「捕捉點 2」
        window.button("captureButton").click();
        //window.textBox("capturedArea").requireText(Pattern.compile(".*捕捉點 2.*"));
        assertTrue(window.textBox("capturedArea").text().contains("捕捉點 2"));

        // 確認文字區域中包含兩個捕捉點訊息
        String text = window.textBox("capturedArea").text();
        assertTrue(text.contains("捕捉點 1"));
        assertTrue(text.contains("捕捉點 2"));
    }

    @Test
    public void testResetButtonClearsCapturePoints() {
        window.button("startButton").click();
        window.button("captureButton").click();
        window.button("resetButton").click();

        // 確認已有捕捉點
        assertTrue(window.textBox("capturedArea").text().contains("捕捉點 1"));

        // 點擊 resetButton，應該清空文字框且 disable captureButton（假設設計是這樣）
        window.button("resetButton").click();
        window.textBox("capturedArea").requireText("");
        window.button("captureButton").requireDisabled();
    }

    @Test
    public void testJudgeButtonDisplaysResult() {
        window.button("startButton").click();
        window.button("captureButton").click();
        window.button("captureButton").click();
        window.button("resetButton").click();
        // 按下判斷按鈕後，文字區顯示判斷結果，這邊用一個假定字串 "判斷結果" 當例子
        window.button("judgeButton").click();
        assertTrue(window.textBox("capturedArea").text().contains("捕捉結束！點擊「判定結果」查看成績"));
    }

    @Test
    public void testButtonOrderBehavior() {
        // 確認在未開始狀態，按 captureButton 不應新增捕捉點
        window.button("captureButton").requireDisabled();

        // 直接按 judgeButton 不應該有判斷結果
        window.button("judgeButton").click();
        assertTrue(window.textBox("capturedArea").text().isEmpty());

        // 開始後才按 capture 與 judge
        window.button("startButton").click();
        window.button("captureButton").click();
        window.button("resetButton").click();
        window.button("judgeButton").click();
        assertTrue(window.textBox("capturedArea").text().contains("捕捉結束！點擊「判定結果」查看成績"));
    }

    @AfterEach
    public void tearDown() {
        window.cleanUp();
    }
}

// 可以放在 InputCaptureGUITest.java 的最下方或作為獨立檔案
class MockLowiroService implements LowiroService {
    @Override
    public double[] getNotes(String songName) {
        return new double[]{500, 1000, 1500};
    }

    @Override
    public double getConstant(String songName) {
        return 1.5;
    }

    @Override
    public int getNoteCount(String songName) {
        return 3;
    }
}


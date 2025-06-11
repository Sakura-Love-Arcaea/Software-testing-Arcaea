package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InputCaptureGUI extends JFrame {
    private final Chart chart;
    private final List<Double> capturedTimes = new ArrayList<>();
    private JTextArea notesArea;
    private JTextArea capturedArea;
    private JButton startButton;
    private JButton captureButton;
    private JButton resetButton;
    private JButton judgeButton;

    private long startTime = 0;
    private boolean isCapturing = false;
    private Timer capturingTimer;

    public InputCaptureGUI(Chart chart) {
        this.chart = chart;

        setTitle("音符捕捉系統");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        setupListeners();
        layoutComponents();
    }

    private void initComponents() {
        notesArea = new JTextArea();
        notesArea.setEditable(false);

        capturedArea = new JTextArea();
        capturedArea.setEditable(false);

        startButton = new JButton("開始捕捉");
        captureButton = new JButton("捕捉點擊");
        resetButton = new JButton("重置");
        judgeButton = new JButton("判定結果");

        captureButton.setEnabled(false);
        judgeButton.setEnabled(false);

        // 顯示譜面音符時間
        displayChartNotes();
    }

    private void setupListeners() {
        startButton.addActionListener(e -> {
            startCapturing();
        });

        captureButton.addActionListener(e -> {
            captureTimepoint();
        });

        // 修改X鍵的行為，如果未開始捕捉則自動開始
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "capture");
        getRootPane().getActionMap().put("capture", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isCapturing) {
                    captureTimepoint();
                } else if (startButton.isEnabled()) {
                    // 如果未開始捕捉但開始按鈕可用，則自動開始捕捉
                    startCapturing();
                }
            }
        });

        // 修改重置按鈕，讓它在捕捉中時變成"結束捕捉"
        resetButton.addActionListener(e -> {
            if (isCapturing) {
                stopCapturing();
            } else {
                resetCapture();
            }
        });

        // 也可以用ESC鍵結束捕捉
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "stop");
        getRootPane().getActionMap().put("stop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isCapturing) {
                    stopCapturing();
                }
            }
        });

        judgeButton.addActionListener(e -> {
            showJudgement();
        });
    }

    private void layoutComponents() {
        JPanel controlPanel = new JPanel();
        controlPanel.add(startButton);
        controlPanel.add(captureButton);
        controlPanel.add(resetButton);
        controlPanel.add(judgeButton);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(notesArea),
                new JScrollPane(capturedArea)
        );
        splitPane.setDividerLocation(200);

        add(controlPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void displayChartNotes() {
        double[] notes = chart.getNotes();
        StringBuilder sb = new StringBuilder("音符時間點 (ms)：\n");
        for (int i = 0; i < notes.length; i++) {
            sb.append(String.format("音符 %d: %.2f ms\n", i + 1, notes[i]));
        }
        notesArea.setText(sb.toString());
    }

    private void startCapturing() {
        capturedTimes.clear();
        capturedArea.setText("");
        startTime = System.currentTimeMillis();
        isCapturing = true;
        startButton.setEnabled(false);
        captureButton.setEnabled(true);
        judgeButton.setEnabled(false);
        resetButton.setText("結束捕捉");

        capturedArea.append("開始捕捉！按X鍵或點擊按鈕來捕捉時間點\n");

        // 設置自動停止計時器 - 假設使用最後一個音符時間作為參考
        double[] notes = chart.getNotes();
        if (notes.length > 0) {
            double lastNoteTime = notes[notes.length - 1];
            // 在最後一個音符後再多給5秒時間
            int captureTimeLimit = (int) (lastNoteTime + 5000);

            capturingTimer = new Timer();
            capturingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> stopCapturing());
                }
            }, captureTimeLimit);

            capturedArea.append("系統將在最後一個音符後5秒自動停止捕捉\n");
        }
    }

    private void captureTimepoint() {
        if (!isCapturing) return;

        long currentTime = System.currentTimeMillis();
        double elapsedTime = currentTime - startTime;
        capturedTimes.add(elapsedTime);

        capturedArea.append(String.format("捕捉點 %d: %.2f ms\n",
                capturedTimes.size(), elapsedTime));

        // 如果捕捉的點數達到或超過音符數量，可以選擇自動結束捕捉
        // 取消注釋以下代碼來啟用此功能
        /*
        if (capturedTimes.size() >= chart.getNotes().length) {
            stopCapturing();
        }
        */
    }

    private void resetCapture() {
        capturedTimes.clear();
        capturedArea.setText("");
        isCapturing = false;
        startButton.setEnabled(true);
        captureButton.setEnabled(false);
        judgeButton.setEnabled(false);
        resetButton.setText("重置");

        // 確保計時器被取消
        if (capturingTimer != null) {
            capturingTimer.cancel();
            capturingTimer = null;
        }
    }

    private void showJudgement() {
        double[] inputLog = capturedTimes.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();

        Judgement judgement = new Judgement(chart, inputLog);
        int[] results = judgement.getJudgements();

        JOptionPane.showMessageDialog(this,
                String.format("判定結果：\nPure: %d\nFar: %d\nMiss: %d",
                        results[0], results[1], results[2]),
                "判定結果",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void stopCapturing() {
        if (!isCapturing) return;

        // 確保計時器被取消
        if (capturingTimer != null) {
            capturingTimer.cancel();
            capturingTimer = null;
        }

        isCapturing = false;
        startButton.setEnabled(true);
        captureButton.setEnabled(false);
        judgeButton.setEnabled(true);
        resetButton.setText("重置");

        capturedArea.append("\n捕捉結束！點擊「判定結果」查看成績\n");
    }

    public double[] getCapturedTimesArray() {
        return capturedTimes.stream()
                .mapToDouble(Double::doubleValue)
                .toArray();
    }

    public static void main(String[] args) {
        // 示例代碼，實際使用時需要提供真實的Chart對象
        SwingUtilities.invokeLater(() -> {
            Chart sampleChart = new Chart("Test", 6, 10.0f);
            sampleChart.setNotes(new double[]{500, 1000, 1500, 2000, 2500, 3000}); // 假設有5個音符
            new InputCaptureGUI(sampleChart).setVisible(true);
        });
    }
}
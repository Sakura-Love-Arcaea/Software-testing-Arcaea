package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
    * InputCaptureGUI.java
    * 音符捕捉系統的GUI界面
    * 用產生點擊時間序列，用於計算判定結果，和其他附加資訊（score、potential、status等）
 */
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
        capturedArea.setName("capturedArea");

        startButton = new JButton("開始捕捉");
        startButton.setName("startButton");

        captureButton = new JButton("捕捉點擊");
        captureButton.setName("captureButton");

        resetButton = new JButton("重置");
        resetButton.setName("resetButton");

        judgeButton = new JButton("判定結果");
        judgeButton.setName("judgeButton");

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

        // 創建Record對象計算分數、潛力值和狀態
        Record record = new Record(chart, results[0], results[1], results[2]);

        // 創建結果面板
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加歌曲信息
        JLabel songInfoLabel = new JLabel(String.format("歌曲: %s (%.1f)",
                chart.songName, chart.getConstant()));
        songInfoLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        songInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(songInfoLabel);
        resultPanel.add(Box.createVerticalStrut(10));

        // 添加判定結果
        JLabel judgeLabel = new JLabel(String.format("Jugdement結果: Pure: %d  Far: %d  Lost: %d",
                results[0], results[1], results[2]));
        judgeLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        judgeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(judgeLabel);
        resultPanel.add(Box.createVerticalStrut(10));

        // 添加score
        JLabel scoreLabel = new JLabel(String.format("Score: %,d", record.getScore()));
        scoreLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        scoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(scoreLabel);
        resultPanel.add(Box.createVerticalStrut(5));

        // 添加potential
        JLabel potentialLabel = new JLabel(String.format("Potential: %.2f", record.getPotential()));
        potentialLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        potentialLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(potentialLabel);
        resultPanel.add(Box.createVerticalStrut(5));

        // 添加status
        JLabel statusLabel = new JLabel(String.format("Status: %s", record.getStatus()));
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(statusLabel);

        // 显示对话框
        JOptionPane.showMessageDialog(this,
                resultPanel,
                "演奏结算",
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

        String songName = "Test Song";
        SwingUtilities.invokeLater(() -> {
            LowiroService lowiroService = new LowiroService() {
                @Override
                public double[] getNotes(String songName) {
                    // 假設返回一個示例音符時間數組
                    return new double[]{500, 1000, 1500, 2000, 2500, 3000};
                }

                @Override
                public double getConstant(String songName) {
                    // 假設返回一個示例常數
                    return 1.5;
                }

                @Override
                public int getNoteCount(String songName) {
                    // 假設返回一個示例音符數量
                    return 6;
                }
            };

            Chart sampleChart = new Chart(songName, lowiroService);
            sampleChart.setNotes(lowiroService.getNotes(songName));
            new InputCaptureGUI(sampleChart).setVisible(true);
        });
    }
}
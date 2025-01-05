package slotGame;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class SlotMachine extends JFrame {

    private JButton[] betButtons;
    private JButton autoButton;
    private JLabel[][] reels;
    private JDialog winDialog;
    private JTable winTable;
    private String[] tableHeaders = {"N", "Session win logs"};
    private JLabel[] positionLabels;
    private Timer spinTimer;
    private Timer blinkTimer;
    private Random random = new Random();
    private String[] symbols = {"🍒", "🍋", "🍊", "🍇", "⭐", "🔔", "💎", "🍉", "7", "Z"};
    private Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};
    private boolean isSpinning = false;
    private boolean autoMode = false;
    private boolean betSelected = false;
    private boolean autoRunning = false;
    private int selectedBetIndex = -1;
    private double initialMoney = 3000;/////////////////////////////////////////////////////////////////////////////////
    private double currentMoney = initialMoney;
    private double sessionHigh = 0;
    private double sessionWin = 0;
    private int gamesPlayed = 0;
    private JPanel infoPanel;
    private JLabel lblInitialMoney, lblCurrentMoney, lblLastWin, lblSessionHigh, lblSessionWin, lblSessionLost, lblGames;
    private Map<String, Double> symbolValues = new HashMap<>();
    private static final int SPIN_DURATION = 2000;//////////////////////////////////////////////////////////////////////
    private JLabel lblRTP;
    private JLabel lblSessionTime;
    private double totalBets = 0;
    private double totalPayouts = 0;
    private List<JackpotServer> jackpots;
    private JDialog jackpotDialog;
    private JLabel lblLastJPHit;
    private JButton btnLoadJP;
    private JLabel lblLastJPHitValue;
    private JackpotServer goldJackpot;
    private JackpotServer silverJackpot;
    private JackpotServer leadJackpot;
    private JLabel lblGold;
    private JLabel lblSilver;
    private JLabel lblLead;
    private RTPChartWindow rtpChartWindow;
    private BetWinChartWindow betWinChartWindow;
    private double cumulativeBet = 0;
    private double cumulativeWin = 0;

    public SlotMachine() {
        rtpChartWindow = new RTPChartWindow();
        rtpChartWindow.setVisible(true);

        betWinChartWindow = new BetWinChartWindow();
        betWinChartWindow.setVisible(true);

        initializeJackpots();
        initializeJackpotDialog();

        setTitle("");
        setSize(1540, 915);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(300, 200));
        infoPanel.setLayout(new GridLayout(9, 2, 5, 5));
        infoPanel.setBackground(Color.DARK_GRAY);
        infoPanel.setForeground(Color.WHITE);

        addInfoRow("Initial money", lblInitialMoney = new JLabel(String.format("%.2f", initialMoney)));
        addInfoRow("Current money", lblCurrentMoney = new JLabel(String.format("%.2f", currentMoney)));
        addInfoRow("Session win/loss", lblSessionLost = new JLabel("0.00"));
        addInfoRow("Last win", lblLastWin = new JLabel("0.00"));
        addInfoRow("Session highest", lblSessionHigh = new JLabel("0.00"));
        //addInfoRow("Session won", lblSessionWin = new JLabel("0.00"));
        addInfoRow("Games", lblGames = new JLabel("0"));
        addInfoRow("RTP%", lblRTP = new JLabel("0.00"));
        addInfoRow("Session time", lblSessionTime = new JLabel("00:00:00"));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("<experiment>01>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 28));
        titleLabel.setForeground(Color.GRAY);
        titlePanel.add(titleLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(300, 200));
        rightPanel.setLayout(new GridLayout(3, 5, 5, 5));
        rightPanel.setBackground(Color.BLACK);

        positionLabels = new JLabel[15];
        for (int i = 0; i < 15; i++) {
            positionLabels[i] = new JLabel("", SwingConstants.CENTER);
            positionLabels[i].setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
            positionLabels[i].setOpaque(true);
            positionLabels[i].setBackground(Color.BLACK);
            positionLabels[i].setForeground(new Color(0, 100, 0));
            positionLabels[i].setBorder(BorderFactory.createLineBorder(Color.WHITE));
            rightPanel.add(positionLabels[i]);
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLACK);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(infoPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        JPanel reelPanel = new JPanel();
        reelPanel.setLayout(new GridBagLayout());
        reelPanel.setPreferredSize(new Dimension(1400, 500));
        reelPanel.setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        reels = new JLabel[3][5];
        gbc.gridy = 0;
        for (int row = 0; row < 3; row++) {
            gbc.gridy = row;
            for (int col = 0; col < 5; col++) {
                reels[row][col] = new JLabel(getRandomSymbol(), SwingConstants.CENTER);
                reels[row][col].setFont(new Font("Segoe UI Emoji", Font.BOLD, 80));
                reels[row][col].setOpaque(true);
                reels[row][col].setBackground(Color.DARK_GRAY);
                reels[row][col].setForeground(getRandomColor());
                reels[row][col].setPreferredSize(new Dimension(150, 150));
                reels[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                gbc.gridx = col;
                reelPanel.add(reels[row][col], gbc);
            }
        }

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        buttonPanel.setBackground(Color.DARK_GRAY);
        buttonPanel.setPreferredSize(new Dimension(1200, 100));

        autoButton = new JButton("Auto");
        autoButton.setForeground(new Color(0, 100, 0));
        autoButton.setBackground(Color.GRAY);
        autoButton.setFont(new Font("OCR A Extended", Font.BOLD, 20));
        autoButton.addActionListener(e -> handleAutoButton());
        buttonPanel.add(autoButton);

        String[] buttons = {"x0.50", "x1.00", "x2.00", "x5.00", "x10.00"};///////////////////////////////////////////////
        betButtons = new JButton[5];

        for (int i = 0; i < buttons.length; i++) {
            final JButton button = new JButton(buttons[i]);
            button.setForeground(new Color(0, 100, 0));
            button.setBackground(Color.GRAY);
            button.setFont(new Font("OCR A Extended", Font.BOLD, 20));

            betButtons[i] = button;
            final int index = i;
            button.addActionListener(e -> handleBetButtonClick(index));

            buttonPanel.add(button);
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(reelPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);

        lblLastJPHit = new JLabel("<jp_hit_value_here>", SwingConstants.CENTER);
        lblLastJPHit.setFont(new Font("OCR A Extended", Font.BOLD, 20));
        lblLastJPHit.setForeground(Color.YELLOW);
        lblLastJPHit.setOpaque(false);

        btnLoadJP = new JButton("LOAD");
        btnLoadJP.setFont(new Font("OCR A Extended", Font.BOLD, 20));
        btnLoadJP.setBackground(Color.BLACK);
        btnLoadJP.setForeground(Color.YELLOW);
        btnLoadJP.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

        lblLastJPHit.setVisible(false);
        btnLoadJP.setVisible(false);

        btnLoadJP.addActionListener(e -> loadJackpotToCurrentMoney());

        jackpotDialog.add(lblLastJPHit);
        jackpotDialog.add(btnLoadJP);

        createWinDialog();
        initializeSymbolValues();

        startSessionTimer();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopSessionTimer();
            }
        });
    }

    private void loadJackpotToCurrentMoney() {
        if (lblLastJPHitValue.isVisible()) {
            double jackpotValue = Double.parseDouble(lblLastJPHitValue.getText());
            currentMoney += jackpotValue;
            lblLastJPHitValue.setText("0.00");
            updateInfoPanel();
            lblLastJPHitValue.setVisible(false);
            btnLoadJP.setVisible(false);
        }
    }

    private void initializeJackpotDialog() {
        jackpotDialog = new JDialog(this, "Jackpot", false);
        jackpotDialog.setSize(920, 220);
        jackpotDialog.setLayout(new GridBagLayout());
        jackpotDialog.getContentPane().setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        // Row 0: Label for "Last JP Hit"
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lastJPHitLabel = new JLabel("JACKPOT WINS");
        lastJPHitLabel.setFont(new Font("OCR A Extended", Font.BOLD, 20));
        lastJPHitLabel.setForeground(Color.WHITE);
        jackpotDialog.add(lastJPHitLabel, gbc);

        // Row 1: Value for "Last JP Hit"
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        lblLastJPHitValue = new JLabel("       0.00");
        lblLastJPHitValue.setFont(new Font("OCR A Extended", Font.BOLD, 25));
        lblLastJPHitValue.setForeground(Color.GREEN);
        jackpotDialog.add(lblLastJPHitValue, gbc);

        // Row 2: LOAD button
        gbc.gridy = 2;
        gbc.gridwidth = 2;//////////////////////////////////////////////////////////////////////////////////////////////
        gbc.fill = GridBagConstraints.HORIZONTAL;
        if (btnLoadJP == null) {
            btnLoadJP = new JButton("LOAD");
            btnLoadJP.setFont(new Font("OCR A Extended", Font.BOLD, 20));
            btnLoadJP.setBackground(Color.BLACK);
            btnLoadJP.setForeground(Color.RED);
            btnLoadJP.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            btnLoadJP.addActionListener(e -> {
                double jackpotValue = Double.parseDouble(lblLastJPHitValue.getText());
                currentMoney += jackpotValue;
                lblCurrentMoney.setText(String.format(Locale.US, "%.2f", currentMoney));

                lblLastJPHitValue.setText("0.00");
                btnLoadJP.setVisible(false);
            });
        }

        jackpotDialog.add(btnLoadJP, gbc);

        // Row 0: GOLD Panel
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JPanel goldJackpotPanel = createJackpotPanel("GOLD", Color.YELLOW);
        jackpotDialog.add(goldJackpotPanel, gbc);

        // Row 1: SILVER Panel
        gbc.gridy = 1;
        JPanel silverJackpotPanel = createJackpotPanel("SILVER", Color.GRAY);
        jackpotDialog.add(silverJackpotPanel, gbc);

        // Row 2: LEAD Panel
        gbc.gridy = 2;
        JPanel leadJackpotPanel = createJackpotPanel("LEAD", Color.DARK_GRAY);
        jackpotDialog.add(leadJackpotPanel, gbc);

        jackpotDialog.setResizable(false);
        jackpotDialog.setLocation(310, 72);///////////////////////////////////////////////////////////////////////
        jackpotDialog.setVisible(true);
    }

    /**
     * Създава панел за дадено ниво на джакпота (златно, сребърно, бронзово).
     *
     * @param labelText Текстът на етикета (например "GOLD", "SILVER").
     * @param color Цветът на етикета и точките.
     * @return JPanel, съдържащ етикета и точките.
     */

    private JPanel createJackpotPanel(String labelText, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.BLACK);

        JLabel label = new JLabel(labelText + " 0.00");
        label.setFont(new Font("OCR A Extended", Font.BOLD, 20));
        label.setForeground(color);
        panel.add(label);

        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dotsPanel.setBackground(Color.BLACK);
        panel.add(dotsPanel);

        if (labelText.equals("GOLD")) {
            lblGold = label;
        } else if (labelText.equals("SILVER")) {
            lblSilver = label;
        } else if (labelText.equals("BRONZE")) {
            lblLead = label;
        }

        return panel;
    }

    private void updateJackpotDialog() {
        if (lblGold != null) {
            lblGold.setText(String.format("      GOLD       %.2f", goldJackpot.getCurrentValue()));
        }
        if (lblSilver != null) {
            lblSilver.setText(String.format("      SILVER     %.2f", silverJackpot.getCurrentValue()));
        }
        if (lblLead != null) {
            lblLead.setText(String.format("      LEAD         %.2f", leadJackpot.getCurrentValue()));
        }

        if (goldJackpot.shouldPayout()) {
            showJackpotHit(goldJackpot.getCurrentValue());
            goldJackpot.reset();
        }

        if (silverJackpot.shouldPayout()) {
            showJackpotHit(silverJackpot.getCurrentValue());
            silverJackpot.reset();
        }

        if (leadJackpot.shouldPayout()) {
            showJackpotHit(leadJackpot.getCurrentValue());
            leadJackpot.reset();
        }
    }

    private void showJackpotHit(double jackpotValue) {
        if (lblLastJPHitValue != null) {
            double currentHit = lblLastJPHitValue.isVisible()
                    ? Double.parseDouble(lblLastJPHitValue.getText())
                    : 0.0;

            double updatedHit = currentHit + jackpotValue;
            lblLastJPHitValue.setText(String.format(Locale.US, "%.2f", updatedHit));
            lblLastJPHitValue.setVisible(true);
        } else {
            System.err.println("lblLastJPHitValue is not initialized.");
        }
    }

    private Timer sessionTimer;

    private long sessionStartTime;

    private void startSessionTimer() {
        sessionStartTime = System.currentTimeMillis();
        sessionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedMillis = System.currentTimeMillis() - sessionStartTime;
                long hours = elapsedMillis / (1000 * 60 * 60);
                long minutes = (elapsedMillis / (1000 * 60)) % 60;
                long seconds = (elapsedMillis / 1000) % 60;

                lblSessionTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                if (hours >= 2) {
                    if (lblSessionTime.getForeground() != Color.RED) {
                        lblSessionTime.setForeground(Color.RED);
                    }

                    if (elapsedMillis % 1000 < 500) {
                        lblSessionTime.setForeground(Color.RED);
                    } else {
                        lblSessionTime.setForeground(Color.BLACK);
                    }
                } else if (hours >= 1) {
                    lblSessionTime.setForeground(Color.YELLOW);
                } else {
                    lblSessionTime.setForeground(Color.WHITE);
                }
            }
        });

        sessionTimer.start();
    }

    private void stopSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.stop();
        }
    }

    private void createWinDialog() {
        winDialog = new JDialog(this, "Current Wins", false);
        winDialog.setSize(600, 400);
        winDialog.setLocationRelativeTo(this);

        winTable = new JTable(new Object[0][0], tableHeaders);
        JScrollPane scrollPane = new JScrollPane(winTable);
        winDialog.add(scrollPane);
    }

    private void processWin(double bet, boolean[][] winningPositions) {
        double winAmount = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                if (winningPositions[row][col]) {
                    String symbol = reels[row][col].getText();
                    Color color = reels[row][col].getForeground();
                    String symbolKey = symbol + "_" + getColorName(color);
                    double pricePerSymbol = symbolValues.getOrDefault(symbolKey, 0.0);
                    winAmount += pricePerSymbol * bet;
                }
            }
        }

        currentMoney += winAmount;
        sessionWin += winAmount;
        totalPayouts += winAmount;
        if (winAmount > sessionHigh) {
            sessionHigh = winAmount;
        }

        cumulativeWin += winAmount;

        betWinChartWindow.updateWin(cumulativeWin);

        lblLastWin.setText(String.format(Locale.US, "%.2f", Math.abs(winAmount)));

        updateInfoPanel();
        updatePositionLabels(winningPositions);
        startGoldBlinking(winningPositions);
        updateRTPInfo();
    }

    private String getColorName(Color color) {
        if (color.equals(Color.RED)) return "RED";
        else if (color.equals(Color.GREEN)) return "GREEN";
        else if (color.equals(Color.BLUE)) return "BLUE";
        else if (color.equals(Color.YELLOW)) return "YELLOW";
        else if (color.equals(Color.CYAN)) return "CYAN";
        else if (color.equals(Color.MAGENTA)) return "MAGENTA";
        return "UNKNOWN";
    }

    private void handleAutoButton() {
        if (autoButton.getText().equals("Auto")) {
            autoMode = true;
            autoButton.setBackground(Color.YELLOW);
            autoButton.setText("Select");

            for (JButton betButton : betButtons) {
                betButton.setForeground(Color.YELLOW);
            }

        } else if (autoButton.getText().equals("Start")) {
            autoButton.setBackground(new Color(255, 20, 90));
            autoButton.setForeground(Color.BLACK);
            autoButton.setText("Stop auto");
            startAutoSpin();
        } else if (autoButton.getText().equals("Stop auto")) {
            stopAutoSpin();
        }
    }

    private void handleBetButtonClick(int index) {
        if (isSpinning) {
            stopSpinning();
            return;
        }

        betSelected = true;

        if (selectedBetIndex >= 0 && selectedBetIndex != index) {
            betButtons[selectedBetIndex].setForeground(new Color(0, 100, 0));
        }

        selectedBetIndex = index;

        for (int i = 0; i < betButtons.length; i++) {
            if (i == selectedBetIndex) {
                betButtons[i].setForeground(Color.YELLOW);
            } else {
                betButtons[i].setForeground(new Color(0, 100, 0));
            }
        }

        if (autoMode) {
            autoButton.setBackground(new Color(144, 238, 144));
            autoButton.setText("Start");
        } else {
            makeBetAndSpin(false);
        }

        stopBlinking();
        resetReelBorders();
    }

    private void stopBlinking() {
        if (blinkTimer != null) {
            blinkTimer.stop();
            blinkTimer = null;
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                reels[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW));
                reels[row][col].setBackground(Color.DARK_GRAY);
            }
        }
    }

    private void startAutoSpin() {
        if (!autoRunning && selectedBetIndex >= 0) {
            autoRunning = true;
            makeBetAndSpin(true);
        }
    }

    private void stopAutoSpin() {
        autoRunning = false;
        autoMode = false;
        autoButton.setBackground(Color.GRAY);
        autoButton.setForeground(new Color(0, 100, 0));
        autoButton.setText("Auto");

        betSelected = false;
        selectedBetIndex = -1;

        for (JButton betButton : betButtons) {
            betButton.setEnabled(true);
            betButton.setForeground(new Color(0, 100, 0));
        }
    }

    private void makeBetAndSpin(boolean isAutoPlay) {
        double betAmount = getBetAmount();
        if (currentMoney >= betAmount) {
            totalBets += betAmount;
            currentMoney -= betAmount;
            gamesPlayed++;

            cumulativeBet += betAmount;

            betWinChartWindow.updateBet(cumulativeBet);

            betWinChartWindow.updateWinWithoutChange(cumulativeWin);

            for (JackpotServer jackpot : jackpots) {
                double increment = betAmount * jackpot.getIncrementPercentage() / 100;
                jackpot.increment(increment);
            }

            updateJackpotDialog();
            updateInfoPanel();
            stopBlinking();
            startSpinning(betAmount, isAutoPlay);
        }
    }

    private double getBetAmount() {
        String betText = betButtons[selectedBetIndex].getText().substring(1);
        return Double.parseDouble(betText);
    }

    private void startSpinning(double bet, boolean isAutoPlay) {
        if (isSpinning) return;

        isSpinning = true;

        fadeOutWinningPositions();

        spinTimer = new Timer(100, new ActionListener() {
            private int elapsedTime = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime += 100;
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 5; col++) {
                        reels[row][col].setText(getRandomSymbol());
                        reels[row][col].setForeground(getRandomColor());
                    }
                }
                if (elapsedTime >= SPIN_DURATION) {
                    stopSpinning();
                    checkForWin(bet, isAutoPlay);
                }
            }
        });
        spinTimer.start();
    }

    private void stopSpinning() {
        if (spinTimer != null) {
            spinTimer.stop();
            spinTimer = null;
        }
        isSpinning = false;
    }

    private void checkForWin(double bet, boolean isAutoPlay) {
        boolean[][] winningPositions = new boolean[3][5];
        boolean hasWin = performWinCheck(bet, winningPositions);

        if (hasWin) {
            processWin(bet, winningPositions);
        } else {
            updateInfoPanel();
        }

        logRTPToConsole();

        if (isAutoPlay && autoRunning) {
            Timer autoSpinTimer = new Timer(2000, e -> makeBetAndSpin(true));
            autoSpinTimer.setRepeats(false);
            autoSpinTimer.start();
        }
    }

    private boolean performWinCheck(double bet, boolean[][] winningPositions) {
        boolean hasWin = false;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col <= 5 - 3; col++) {
                String symbol = reels[row][col].getText();
                if (reels[row][col + 1].getText().equals(symbol) && reels[row][col + 2].getText().equals(symbol)) {
                    winningPositions[row][col] = true;
                    winningPositions[row][col + 1] = true;
                    winningPositions[row][col + 2] = true;
                    hasWin = true;
                }
            }
        }

        for (int col = 0; col < 5; col++) {
            for (int row = 0; row <= 3 - 3; row++) {
                String symbol = reels[row][col].getText();
                if (reels[row + 1][col].getText().equals(symbol) && reels[row + 2][col].getText().equals(symbol)) {
                    winningPositions[row][col] = true;
                    winningPositions[row + 1][col] = true;
                    winningPositions[row + 2][col] = true;
                    hasWin = true;
                }
            }
        }

        for (int row = 0; row <= 3 - 3; row++) {
            for (int col = 0; col <= 5 - 3; col++) {
                String symbol = reels[row][col].getText();
                if (reels[row + 1][col + 1].getText().equals(symbol) && reels[row + 2][col + 2].getText().equals(symbol)) {
                    winningPositions[row][col] = true;
                    winningPositions[row + 1][col + 1] = true;
                    winningPositions[row + 2][col + 2] = true;
                    hasWin = true;
                }
            }
        }

        for (int row = 0; row <= 3 - 3; row++) {
            for (int col = 2; col < 5; col++) {
                String symbol = reels[row][col].getText();
                if (reels[row + 1][col - 1].getText().equals(symbol) && reels[row + 2][col - 2].getText().equals(symbol)) {
                    winningPositions[row][col] = true;
                    winningPositions[row + 1][col - 1] = true;
                    winningPositions[row + 2][col - 2] = true;
                    hasWin = true;
                }
            }
        }

        return hasWin;
    }

    private void startGoldBlinking(boolean[][] winningPositions) {
        blinkTimer = new Timer(500, new ActionListener() {
            private boolean isGold = true;

            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 5; col++) {
                        if (winningPositions[row][col]) {
                            reels[row][col].setBackground(isGold ? new Color(255, 255, 153) : Color.DARK_GRAY);
                        }
                    }
                }
                isGold = !isGold;
            }
        });
        blinkTimer.start();
    }

    private void resetReelBorders() {
        if (blinkTimer != null) {
            blinkTimer.stop();
        }
        for (int row = 0; row < reels.length; row++) {
            for (int col = 0; col < reels[row].length; col++) {
                reels[row][col].setBackground(Color.DARK_GRAY);
                reels[row][col].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
            }
        }
    }

    private void fadeOutWinningPositions() {
        for (int i = 0; i < positionLabels.length; i++) {
            if (!positionLabels[i].getText().isEmpty()) {
                positionLabels[i].setForeground(Color.GRAY);
            }
        }
    }

    private void updatePositionLabels(boolean[][] winningPositions) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                if (winningPositions[row][col]) {
                    int index = row * 5 + col;
                    positionLabels[index].setText(reels[row][col].getText());
                    positionLabels[index].setForeground(reels[row][col].getForeground());
                }
            }
        }
    }

    private void updateInfoPanel() {
        lblCurrentMoney.setText(String.format(Locale.US, "%.2f", currentMoney));
        lblGames.setText(String.valueOf(gamesPlayed));

        lblSessionHigh.setText(String.format(Locale.US, "%.2f", sessionHigh));
        //lblSessionWin.setText(String.format(Locale.US, "%.2f", sessionWin));

        double sessionLost = initialMoney - currentMoney;
        lblSessionLost.setText(String.format(Locale.US, "%.2f", Math.abs(sessionLost)));

        if (sessionLost > 0) {
            lblSessionLost.setForeground(new Color(255, 99, 71));
        } else if (sessionLost < 0) {
            lblSessionLost.setForeground(Color.GREEN);
        } else {
            lblSessionLost.setForeground(Color.WHITE);
        }

        if (currentMoney > initialMoney) {
            lblCurrentMoney.setForeground(Color.GREEN);
        } else if (currentMoney < initialMoney) {
            lblCurrentMoney.setForeground(new Color(255, 99, 71));
        } else {
            lblCurrentMoney.setForeground(Color.WHITE);
        }
    }

    private void updateRTPInfo() {
        double rtp = calculateRTP();
        lblRTP.setText(String.format(Locale.US, "%.2f%%", rtp));
    }

    private double calculateRTP() {
        if (totalBets == 0) {
            return 0.0;
        }

        return (totalPayouts / totalBets) * 100;
    }

    private void initializeSymbolValues() {

        symbolValues.put("🍒_RED", 0.25);
        symbolValues.put("🍒_GREEN", 0.35);
        symbolValues.put("🍒_BLUE", 0.45);
        symbolValues.put("🍒_YELLOW", 0.55);
        symbolValues.put("🍒_CYAN", 0.65);
        symbolValues.put("🍒_MAGENTA", 0.80);

        symbolValues.put("🍋_RED", 0.28);
        symbolValues.put("🍋_GREEN", 0.38);
        symbolValues.put("🍋_BLUE", 0.48);
        symbolValues.put("🍋_YELLOW", 0.58);
        symbolValues.put("🍋_CYAN", 0.68);
        symbolValues.put("🍋_MAGENTA", 1.00);

        symbolValues.put("🍊_RED", 0.30);
        symbolValues.put("🍊_GREEN", 0.40);
        symbolValues.put("🍊_BLUE", 0.50);
        symbolValues.put("🍊_YELLOW", 0.60);
        symbolValues.put("🍊_CYAN", 0.70);
        symbolValues.put("🍊_MAGENTA", 1.10);

        symbolValues.put("🍇_RED", 0.40);
        symbolValues.put("🍇_GREEN", 0.45);
        symbolValues.put("🍇_BLUE", 0.55);
        symbolValues.put("🍇_YELLOW", 0.65);
        symbolValues.put("🍇_CYAN", 0.75);
        symbolValues.put("🍇_MAGENTA", 1.20);

        symbolValues.put("⭐_RED", 0.55);
        symbolValues.put("⭐_GREEN", 0.60);
        symbolValues.put("⭐_BLUE", 0.80);
        symbolValues.put("⭐_YELLOW", 1.00);
        symbolValues.put("⭐_CYAN", 1.20);
        symbolValues.put("⭐_MAGENTA", 1.50);

        symbolValues.put("🔔_RED", 0.60);
        symbolValues.put("🔔_GREEN", 0.75);
        symbolValues.put("🔔_BLUE", 1.00);
        symbolValues.put("🔔_YELLOW", 1.20);
        symbolValues.put("🔔_CYAN", 1.40);
        symbolValues.put("🔔_MAGENTA", 2.10);

        symbolValues.put("💎_RED", 0.65);
        symbolValues.put("💎_GREEN", 1.00);
        symbolValues.put("💎_BLUE", 1.20);
        symbolValues.put("💎_YELLOW", 1.70);
        symbolValues.put("💎_CYAN", 2.00);
        symbolValues.put("💎_MAGENTA", 2.40);

        symbolValues.put("🍉_RED", 0.80);
        symbolValues.put("🍉_GREEN", 1.20);
        symbolValues.put("🍉_BLUE", 1.60);
        symbolValues.put("🍉_YELLOW", 2.00);
        symbolValues.put("🍉_CYAN", 2.50);
        symbolValues.put("🍉_MAGENTA", 3.00);

        symbolValues.put("7_RED", 5.00);
        symbolValues.put("7_GREEN", 6.00);
        symbolValues.put("7_BLUE", 7.00);
        symbolValues.put("7_YELLOW", 8.00);
        symbolValues.put("7_CYAN", 9.00);
        symbolValues.put("7_MAGENTA", 10.00);

        symbolValues.put("Z_RED", 5.50);
        symbolValues.put("Z_GREEN", 6.00);
        symbolValues.put("Z_BLUE", 7.50);
        symbolValues.put("Z_YELLOW", 8.50);
        symbolValues.put("Z_CYAN", 10.50);
        symbolValues.put("Z_MAGENTA", 15.00);
    }

    private void initializeJackpots() {
        jackpots = new ArrayList<>();
        jackpots.add(new JackpotServer("Minor", 13.53, 0.18, 29.95, 30.00));
        jackpots.add(new JackpotServer("Major", 21.30, 0.14, 49.99, 50.00));
        jackpots.add(new JackpotServer("Mega", 31.13, 0.09, 99.65, 100.00));

        leadJackpot = jackpots.get(0);
        silverJackpot = jackpots.get(1);
        goldJackpot = jackpots.get(2);
    }

    private String getRandomSymbol() {
        String[] symbols = {"🍒", "🍋", "🍊", "🍇", "⭐", "🔔", "💎", "7", "Z"};
        int[] weights =    { 20,    19,   18,   17,   17,   15,    15,   12,  10 };

        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (int i = 0; i < symbols.length; i++) {
            cumulativeWeight += weights[i];
            if (randomValue < cumulativeWeight) {
                return symbols[i];
            }
        }

        return symbols[0];
    }

    private Color getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

    private void addInfoRow(String label, JLabel valueLabel) {
        JLabel infoLabel = new JLabel(label, SwingConstants.LEFT);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("OCR A Extended", Font.BOLD, 16));
        infoPanel.add(infoLabel);

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("OCR A Extended", Font.BOLD, 16));
        infoPanel.add(valueLabel);
    }

    private void logRTPToConsole() {
        double rtp = calculateRTP();
        System.out.println(String.format("RTP: %.2f%% | Bets: %.2f | Pays: %.2f", rtp, totalBets, totalPayouts));

        if (rtpChartWindow != null) {
            rtpChartWindow.updateRTP(rtp);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SlotMachine layout = new SlotMachine();
            layout.setVisible(true);

        });
    }

}


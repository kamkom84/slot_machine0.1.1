package slotGame;

import javax.swing.*;
import java.awt.*;

public class JackpotServer {
    private String name;
    private double baseValue;
    private double incrementPercentage;
    private double minPayoutValue;
    private double maxPayoutValue;
    private double currentValue;

    public JackpotServer(String name, double baseValue, double incrementPercentage, double minPayoutValue, double maxPayoutValue) {
        this.name = name;
        this.baseValue = baseValue;
        this.incrementPercentage = incrementPercentage;
        this.minPayoutValue = minPayoutValue;
        this.maxPayoutValue = maxPayoutValue;
        this.currentValue = baseValue;
    }

    public String getName() {
        return name;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void increment(double amount) {
        currentValue += amount;
    }

    public boolean shouldPayout() {
        return currentValue >= minPayoutValue && currentValue <= maxPayoutValue;
    }

    public void reset() {
        currentValue = baseValue;
    }

    public void setBaseValue(double baseValue) {
        this.baseValue = baseValue;
        if (currentValue < baseValue) {
            currentValue = baseValue;
        }
    }

    public void setIncrementPercentage(double incrementPercentage) {
        this.incrementPercentage = incrementPercentage;
    }

    public void setMinPayoutValue(double minPayoutValue) {
        this.minPayoutValue = minPayoutValue;
    }

    public void setMaxPayoutValue(double maxPayoutValue) {
        this.maxPayoutValue = maxPayoutValue;
    }

//    public void configureJackpot(JFrame parent) {
//        JDialog configDialog = new JDialog(parent, "Configure Jackpot - " + name, true);
//        configDialog.setSize(400, 300);
//        configDialog.setLayout(new GridLayout(5, 2, 10, 10));
//
//        // Създаваме полета за въвеждане на стойности
//        JTextField baseValueField = new JTextField(String.valueOf(baseValue));
//        JTextField incrementPercentageField = new JTextField(String.valueOf(incrementPercentage));
//        JTextField minPayoutField = new JTextField(String.valueOf(minPayoutValue));
//        JTextField maxPayoutField = new JTextField(String.valueOf(maxPayoutValue));
//
//        configDialog.add(new JLabel("Base Value:"));
//        configDialog.add(baseValueField);
//        configDialog.add(new JLabel("Increment %:"));
//        configDialog.add(incrementPercentageField);
//        configDialog.add(new JLabel("Min Payout Value:"));
//        configDialog.add(minPayoutField);
//        configDialog.add(new JLabel("Max Payout Value:"));
//        configDialog.add(maxPayoutField);
//
//        JButton saveButton = new JButton("Save");
//        saveButton.addActionListener(e -> {
//            try {
//                // Задаваме новите стойности
//                setBaseValue(Double.parseDouble(baseValueField.getText()));
//                setIncrementPercentage(Double.parseDouble(incrementPercentageField.getText()));
//                setMinPayoutValue(Double.parseDouble(minPayoutField.getText()));
//                setMaxPayoutValue(Double.parseDouble(maxPayoutField.getText()));
//                configDialog.dispose(); // Затваряме прозореца
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(configDialog, "Invalid input! Please enter valid numbers.",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        configDialog.add(new JLabel()); // Празен компонент за разстояние
//        configDialog.add(saveButton);
//
//        configDialog.setLocationRelativeTo(parent);
//        configDialog.setVisible(true);
//    }

    public double getIncrementPercentage() {
        return incrementPercentage;
    }

}

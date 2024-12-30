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

    public double getIncrementPercentage() {
        return incrementPercentage;
    }

}

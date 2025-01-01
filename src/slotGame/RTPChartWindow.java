package slotGame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


import javax.swing.*;
import java.awt.*;

public class RTPChartWindow extends JFrame {
    private TimeSeries rtpSeries;

    public RTPChartWindow() {
        setTitle("RTP Chart");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Създаване на серия за RTP данни
        rtpSeries = new TimeSeries("RTP");

        TimeSeriesCollection dataset = new TimeSeriesCollection(rtpSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Real-Time RTP", "Time", "RTP (%)", dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        setContentPane(chartPanel);
    }

    public void updateRTP(double rtp) {
        // Добавяме нова стойност към графиката
        rtpSeries.addOrUpdate(new Second(), rtp);
    }
}

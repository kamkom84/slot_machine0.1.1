package slotGame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class RTPChartWindow extends JFrame {
    private TimeSeries rtpSeries;

    public RTPChartWindow() {
        setTitle("RTP Chart");
        setSize(355, 330);////////////////////////////////////////////////////////////////////////////////////
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Задаване на черен фон за целия прозорец
        getContentPane().setBackground(Color.BLACK);

        // Създаване на серия за RTP данни
        rtpSeries = new TimeSeries("RTP");

        TimeSeriesCollection dataset = new TimeSeriesCollection(rtpSeries);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null, // Няма заглавие на графиката
                null, // Премахване на надписа "Time"
                null, // Премахване на надписа "RTP (%)"
                dataset);

        // Конфигуриране на черен фон за графиката
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Премахване на легендата
        chart.removeLegend();

        // Панел за черна графика
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.BLACK);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Задаване на черен фон и за основния панел
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.BLACK);
        contentPane.add(chartPanel, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    public void updateRTP(double rtp) {
        // Добавяме нова стойност към графиката
        rtpSeries.addOrUpdate(new Second(), rtp);
    }
}

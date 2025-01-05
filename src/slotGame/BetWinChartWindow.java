package slotGame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class BetWinChartWindow extends JFrame {
    private TimeSeries betSeries;
    private TimeSeries winSeries;

    public BetWinChartWindow() {
        setTitle("Bet/Win Chart");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Color.BLACK);

        betSeries = new TimeSeries("Bet");
        winSeries = new TimeSeries("Win");

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(betSeries);
        dataset.addSeries(winSeries);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null,
                null,
                null,
                dataset
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.BLACK);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);

        plot.getRenderer().setSeriesPaint(0, Color.RED);
        plot.getRenderer().setSeriesPaint(1, Color.GREEN);

        chart.removeLegend();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.BLACK);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.BLACK);
        contentPane.add(chartPanel, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    /**
     * Актуализира стойността на линията за Bet.
     */
    public void updateBet(double bet) {
        betSeries.addOrUpdate(new Second(), bet);
    }

    /**
     * Актуализира стойността на линията за Win.
     */
    public void updateWin(double win) {
        winSeries.addOrUpdate(new Second(), win);
    }

    /**
     * Актуализира линията за Win без промяна в стойността (запазва текущата стойност).
     */
    public void updateWinWithoutChange(double currentWin) {
        winSeries.addOrUpdate(new Second(), currentWin);
    }

}

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

public class RTPChartWindow extends JFrame {

    private TimeSeries rtpSeries;

    public RTPChartWindow() {
        setTitle("RTP Chart");
        setSize(355, 330);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Color.BLACK);

        rtpSeries = new TimeSeries("RTP");

        TimeSeriesCollection dataset = new TimeSeriesCollection(rtpSeries);
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

        chart.removeLegend();

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.BLACK);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.BLACK);
        contentPane.add(chartPanel, BorderLayout.CENTER);

        setContentPane(contentPane);
    }

    public void updateRTP(double rtp) {
        rtpSeries.addOrUpdate(new Second(), rtp);
    }
}

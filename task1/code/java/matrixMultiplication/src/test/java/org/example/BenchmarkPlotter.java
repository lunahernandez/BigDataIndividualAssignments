package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BenchmarkPlotter {

    public void plotBenchmarkComparison(Map<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> benchmarkData) {
        XYSeriesCollection dataset = getDataset(benchmarkData);

        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "Mean Time vs. Matrix Size for Different Functions",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        showChart(chart1);
    }

    private static XYSeriesCollection getDataset(Map<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> benchmarkData) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> entry : benchmarkData.entrySet()) {
            String benchmarkName = entry.getKey();
            XYSeries series = new XYSeries(benchmarkName);

            for (BenchmarkResultProcessor.Pair<Integer, Double> dataPoint : entry.getValue()) {
                series.add(dataPoint.getKey(), dataPoint.getValue());
            }

            dataset.addSeries(series);
        }
        return dataset;
    }

    private void showChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        setLabelAndTickFont(plot);
        setLegendAndTitleFont(chart);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JFrame frame = new JFrame("Mean Time vs. Matrix Size for Different Functions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void setLabelAndTickFont(XYPlot plot) {
        Font axisLabelFont = new Font("Tahoma", Font.BOLD, 16);
        plot.getDomainAxis().setLabelFont(axisLabelFont);
        plot.getRangeAxis().setLabelFont(axisLabelFont);

        Font axisTickFont = new Font("Tahoma", Font.PLAIN, 14);
        plot.getDomainAxis().setTickLabelFont(axisTickFont);
        plot.getRangeAxis().setTickLabelFont(axisTickFont);
    }

    private static void setLegendAndTitleFont(JFreeChart chart) {
        Font titleFont = new Font("Tahoma", Font.BOLD, 20);
        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            Font legendFont = new Font("Tahoma", Font.PLAIN, 16);
            legend.setItemFont(legendFont);
        }
    }

    public static void main(String[] args) {
        String csvFilePath = "data/java-benchmark-results.csv";

        BenchmarkResultProcessor processor = new BenchmarkResultProcessor();
        Map<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> benchmarkData =
                processor.readCsvAndGroupByBenchmark(csvFilePath);

        BenchmarkPlotter plotter = new BenchmarkPlotter();
        plotter.plotBenchmarkComparison(benchmarkData);
    }
}

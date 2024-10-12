package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class BenchmarkPlotter {

    public void plotBenchmarkComparison(Map<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> benchmarkData) {
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        XYSeriesCollection dataset2 = new XYSeriesCollection();

        for (Map.Entry<String, List<BenchmarkResultProcessor.Pair<Integer, Double>>> entry : benchmarkData.entrySet()) {
            String benchmarkName = entry.getKey();
            XYSeries series = new XYSeries(benchmarkName);

            for (BenchmarkResultProcessor.Pair<Integer, Double> dataPoint : entry.getValue()) {
                series.add(dataPoint.getKey(), dataPoint.getValue());
            }

            dataset1.addSeries(series);

            if (!benchmarkName.equals("multiplication2")) {
                dataset2.addSeries(series);
            }
        }

        JFreeChart chart1 = ChartFactory.createXYLineChart(
                "All Benchmarks",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset1,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        showChart(chart1, "All Benchmarks");

        JFreeChart chart2 = ChartFactory.createXYLineChart(
                "Benchmarks without multiplication2",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset2,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        showChart(chart2, "Benchmarks without multiplication2");
    }

    private void showChart(JFreeChart chart, String title) {
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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

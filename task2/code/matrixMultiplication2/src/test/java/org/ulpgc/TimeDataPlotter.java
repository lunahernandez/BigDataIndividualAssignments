package org.ulpgc;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeDataPlotter {

    public Map<String, Map<Double, List<Pair<Integer, Double>>>> readCsvAndGroupByBenchmarkAndSparsity(String csvFilePath) {
        Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                String benchmarkName = values[0];
                int matrixSize = Integer.parseInt(values[1]);
                double sparsity = Double.parseDouble(values[2]);
                double meanTime = Double.parseDouble(values[3]);

                benchmarkData
                        .computeIfAbsent(benchmarkName, k -> new HashMap<>())
                        .computeIfAbsent(sparsity, k -> new ArrayList<>())
                        .add(new Pair<>(matrixSize, meanTime));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return benchmarkData;
    }

    public void plotBenchmarkComparison(Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData) {
        XYSeriesCollection dataset = getDataset(benchmarkData);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Matrix Size vs Mean Time",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        styleChart(chart, benchmarkData);
        showChart(chart);

        for (Double sparsity : benchmarkData.values().iterator().next().keySet()) {
            plotSparsityChart(benchmarkData, sparsity);
        }
    }

    private void plotSparsityChart(Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData, double sparsity) {
        XYSeriesCollection dataset = getDatasetForSparsity(benchmarkData, sparsity);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Matrix Size vs Mean Time (Sparsity " + sparsity + ")",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        styleChart(chart, benchmarkData);
        showChart(chart);
    }

    private static XYSeriesCollection getDataset(Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkEntry : benchmarkData.entrySet()) {
            String benchmarkName = benchmarkEntry.getKey();

            for (Map.Entry<Double, List<Pair<Integer, Double>>> sparsityEntry : benchmarkEntry.getValue().entrySet()) {
                double sparsity = sparsityEntry.getKey();
                String seriesName = benchmarkName + " (Sparsity " + sparsity + ")";
                XYSeries series = new XYSeries(seriesName);

                for (Pair<Integer, Double> dataPoint : sparsityEntry.getValue()) {
                    series.add(dataPoint.getKey(), dataPoint.getValue());
                }

                dataset.addSeries(series);
            }
        }
        return dataset;
    }

    private static XYSeriesCollection getDatasetForSparsity(Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData, double sparsity) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkEntry : benchmarkData.entrySet()) {
            String benchmarkName = benchmarkEntry.getKey();
            if (benchmarkEntry.getValue().containsKey(sparsity)) {
                String seriesName = benchmarkName + " (Sparsity " + sparsity + ")";
                XYSeries series = new XYSeries(seriesName);

                for (Pair<Integer, Double> dataPoint : benchmarkEntry.getValue().get(sparsity)) {
                    series.add(dataPoint.getKey(), dataPoint.getValue());
                }

                dataset.addSeries(series);
            }
        }
        return dataset;
    }

    private void styleChart(JFreeChart chart, Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData) {
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        int seriesIndex = 0;
        float[] dashPatterns = {2.0f, 4.0f, 6.0f};

        for (Map.Entry<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkEntry : benchmarkData.entrySet()) {
            for (Double sparsity : benchmarkEntry.getValue().keySet()) {
                BasicStroke stroke = new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f,
                        new float[]{dashPatterns[(int) (sparsity * (dashPatterns.length - 1))]}, 0.0f
                );
                renderer.setSeriesStroke(seriesIndex, stroke);
                seriesIndex++;
            }
        }

        plot.setRenderer(renderer);
        setLabelAndTickFont(plot);
        setLegendAndTitleFont(chart);
    }

    private void showChart(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JFrame frame = new JFrame("Matrix Size vs Mean Time");
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

    public static class Pair<K, V> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        TimeDataPlotter plotter = new TimeDataPlotter();
        String csvFilePath = "task2/data/benchmark_results.csv";

        Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData = plotter.readCsvAndGroupByBenchmarkAndSparsity(csvFilePath);
        plotter.plotBenchmarkComparison(benchmarkData);
    }
}

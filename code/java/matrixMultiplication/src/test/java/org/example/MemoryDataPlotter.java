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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryDataPlotter {

    public Map<String, List<Pair<Integer, Double>>> readCsvAndGroupByBenchmark(String csvFilePath) {
        Map<String, List<Pair<Integer, Double>>> benchmarkData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                String benchmarkName = values[0];
                int matrixSize = Integer.parseInt(values[1]);
                double memoryUsage = Double.parseDouble(values[2]);

                benchmarkData
                        .computeIfAbsent(benchmarkName, k -> new ArrayList<>())
                        .add(new Pair<>(matrixSize, memoryUsage));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return benchmarkData;
    }


    public void plotBenchmarkComparison(Map<String, List<Pair<Integer, Double>>> benchmarkData) {
        XYSeriesCollection dataset = getDataset(benchmarkData);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Matrix Size vs Memory Usage",
                "Matrix Size (n)",
                "Average Memory Usage (MiB)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        showChart(chart);
    }

    private static XYSeriesCollection getDataset(Map<String, List<Pair<Integer, Double>>> benchmarkData) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, List<Pair<Integer, Double>>> entry : benchmarkData.entrySet()) {
            String benchmarkName = entry.getKey();
            XYSeries series = new XYSeries(benchmarkName);

            for (Pair<Integer, Double> dataPoint : entry.getValue()) {
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

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        JFrame frame = new JFrame("Matrix Size vs Memory Usage");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
        MemoryDataPlotter plotter = new MemoryDataPlotter();
        String csvFilePath = "data/java_memory_usage.csv";

        Map<String, List<Pair<Integer, Double>>> benchmarkData = plotter.readCsvAndGroupByBenchmark(csvFilePath);
        plotter.plotBenchmarkComparison(benchmarkData);
    }
}

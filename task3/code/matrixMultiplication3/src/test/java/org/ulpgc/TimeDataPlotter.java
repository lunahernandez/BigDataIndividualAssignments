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
import java.util.*;
import java.util.List;

public class TimeDataPlotter {

    public Map<String, Map<Double, List<Pair<Integer, Double>>>> readCsvAndGroupByThreads(String csvFilePath) {
        Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                addBenchmarkData(line, benchmarkData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return benchmarkData;
    }

    private static void addBenchmarkData(String line, Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData) {
        String[] values = line.split(",");
        String benchmarkName = values[0];
        int matrixSize = Integer.parseInt(values[1]);
        double numThreads = Double.parseDouble(values[2]);
        double meanTime = Double.parseDouble(values[3]);
        benchmarkData
                .computeIfAbsent(benchmarkName, k -> new HashMap<>())
                .computeIfAbsent(numThreads, k -> new ArrayList<>())
                .add(new Pair<>(matrixSize, meanTime));
    }

    public void plotComparison(Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, Map<Double, List<Pair<Integer, Double>>>> entry : benchmarkData.entrySet()) {
            String benchmarkName = entry.getKey();
            for (Map.Entry<Double, List<Pair<Integer, Double>>> threadEntry : entry.getValue().entrySet()) {
                double numThreads = threadEntry.getKey();
                String seriesName = benchmarkName + " (threads=" + (int) numThreads + ")";
                XYSeries series = new XYSeries(seriesName);

                for (Pair<Integer, Double> dataPoint : threadEntry.getValue()) {
                    series.add(dataPoint.getKey(), dataPoint.getValue());
                }

                dataset.addSeries(series);
            }
        }

        JFreeChart chart = getChart(dataset);
        styleChart(chart);
        showChart(chart);
    }

    private static JFreeChart getChart(XYSeriesCollection dataset) {
        return ChartFactory.createXYLineChart(
                "Matrix Size vs Execution Time",
                "Matrix Size (n)",
                "Mean Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
    }

    private void styleChart(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultStroke(new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        setFontStyles(chart, plot);
    }

    private void setFontStyles(JFreeChart chart, XYPlot plot) {
        Font axisLabelFont = new Font("Tahoma", Font.BOLD, 16);
        plot.getDomainAxis().setLabelFont(axisLabelFont);
        plot.getRangeAxis().setLabelFont(axisLabelFont);

        Font tickLabelFont = new Font("Tahoma", Font.PLAIN, 14);
        plot.getDomainAxis().setTickLabelFont(tickLabelFont);
        plot.getRangeAxis().setTickLabelFont(tickLabelFont);

        Font titleFont = new Font("Tahoma", Font.BOLD, 20);
        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(new Font("Tahoma", Font.PLAIN, 16));
        }
    }

    private void showChart(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 600));
        JFrame frame = new JFrame("Benchmark Comparison");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
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
        TimeDataPlotter plotter = new TimeDataPlotter();
        String csvFilePath = "task3/data/benchmark_results.csv";
        Map<String, Map<Double, List<Pair<Integer, Double>>>> benchmarkData = plotter.readCsvAndGroupByThreads(csvFilePath);
        plotter.plotComparison(benchmarkData);
    }
}

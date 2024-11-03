package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BenchmarkResultProcessor {

    public void processJsonToCsv(String jsonFilePath, String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
             FileWriter writer = new FileWriter(csvFilePath, StandardCharsets.UTF_8)) {

            writer.append("benchmark_name,matrix_size,mean_time,rounds,iterations,warmup\n");

            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String benchmarkName = jsonObject.getString("benchmark");
                int matrixSize = Integer.parseInt(jsonObject.getJSONObject("params").getString("matrixSize"));
                double averageTime = jsonObject.getJSONObject("primaryMetric").getDouble("score");
                int rounds = jsonObject.getInt("forks");
                int iterations = jsonObject.getInt("measurementIterations");
                int warmupIterations = jsonObject.getInt("warmupIterations");
                boolean warmup = warmupIterations > 0;
                String warmupStr = Boolean.toString(warmup);

                writer.append(benchmarkName)
                        .append(',')
                        .append(String.valueOf(matrixSize))
                        .append(',')
                        .append(String.valueOf(averageTime))
                        .append(',')
                        .append(String.valueOf(rounds))
                        .append(',')
                        .append(String.valueOf(iterations))
                        .append(',')
                        .append(warmupStr)
                        .append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<Pair<Integer, Double>>> readCsvAndGroupByBenchmark(String csvFilePath) {
        Map<String, List<Pair<Integer, Double>>> benchmarkData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                String benchmarkName = values[0];
                int matrixSize = Integer.parseInt(values[1]);
                double averageTime = Double.parseDouble(values[2]);

                benchmarkData
                        .computeIfAbsent(benchmarkName, k -> new ArrayList<>())
                        .add(new Pair<>(matrixSize, averageTime));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return benchmarkData;
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
        BenchmarkResultProcessor processor = new BenchmarkResultProcessor();
        processor.processJsonToCsv(
                "data/java-benchmark-results.json", "data/java-benchmark-results.csv");
    }
}

package org.ulpgc;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BenchmarkResultProcessor {

    public static void main(String[] args) {
        BenchmarkResultProcessor processor = new BenchmarkResultProcessor();
        processor.processJsonToCsv(
                "task3/data/benchmark_results.json", "task3/data/benchmark_results.csv");
    }

    public void processJsonToCsv(String jsonFilePath, String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
             FileWriter writer = new FileWriter(csvFilePath, StandardCharsets.UTF_8)) {

            writer.append("benchmark_name,matrix_size,num_threads,mean_time,mean_memory,rounds,iterations,warmup\n");

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

                double numThreads = jsonObject.getJSONObject("params").has("numThreads")
                        ? jsonObject.getJSONObject("params").getDouble("numThreads")
                        : -1;

                double averageTime = jsonObject.getJSONObject("primaryMetric").getDouble("score");
                double averageMemory = jsonObject.getJSONObject("secondaryMetrics").getJSONObject("·gc.alloc.rate.norm").getDouble("score");
                averageMemory = convertBytesToMB(averageMemory);
                int rounds = jsonObject.getInt("forks");
                int iterations = jsonObject.getInt("measurementIterations");
                int warmupIterations = jsonObject.getInt("warmupIterations");
                boolean warmup = warmupIterations > 0;
                String warmupStr = Boolean.toString(warmup);

                writer.append(benchmarkName)
                        .append(',')
                        .append(String.valueOf(matrixSize))
                        .append(',')
                        .append(numThreads == -1 ? "1.0" : String.valueOf(numThreads))
                        .append(',')
                        .append(String.valueOf(averageTime))
                        .append(',')
                        .append(String.valueOf(averageMemory))
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

    private double convertBytesToMB(double bytes) {
        return bytes / (1024 * 1024);
    }
}

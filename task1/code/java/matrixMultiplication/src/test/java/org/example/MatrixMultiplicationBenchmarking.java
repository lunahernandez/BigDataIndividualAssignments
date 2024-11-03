package org.example;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.example.ParallelMatrixMultiplication.multiplyMatricesParallel;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {


    @State(Scope.Thread)
    public static class Operands {
        public double[][] a;
        public double[][] b;

        @Param({"10", "50", "100", "500", "1000"})
        public int matrixSize;

        private long totalMemoryUsed = 0;
        private int iterationCount = 0;


        @Setup(Level.Trial)
        public void setup() {
            int n = matrixSize;
            a = new double[n][n];
            b = new double[n][n];
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextDouble();
                    b[i][j] = random.nextDouble();
                }
            }
        }

        @TearDown(Level.Trial)
        public void tearDown() throws IOException {
            if (iterationCount > 0) {
                double averageMemoryUsedMiB = (totalMemoryUsed / (double) iterationCount) / (1024 * 1024);

                File file = new File("memory_usage.csv");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    if (file.length() == 0) {
                        writer.write("name,matrix_size,average_memory_used_mib\n");
                    }
                    writer.write(currentBenchmark + "," + matrixSize + "," + averageMemoryUsedMiB + "\n");
                }

            }
        }

        private String currentBenchmark;

        public void recordMemoryUsage(long memoryUsed) {
            totalMemoryUsed += memoryUsed;
            iterationCount++;
        }

        public void setBenchmarkName(String benchmarkName) {
            this.currentBenchmark = benchmarkName;
        }
    }

    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    @Benchmark
    public void multiplication1(Operands operands) {
        operands.setBenchmarkName("multiplication1");
        long beforeMemory = getUsedMemory();
        new MatrixMultiplication().execute(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void multiplication2(Operands operands) {
        operands.setBenchmarkName("multiplication2");
        long beforeMemory = getUsedMemory();
        new MatrixMultiplication().blockMatrixMultiplication(operands.a, operands.b, 10);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void multiplication3(Operands operands) {
        operands.setBenchmarkName("multiplication3");
        long beforeMemory = getUsedMemory();
        new MatrixMultiplication().rowMajorMatrixMultiplication(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void multiplication4(Operands operands) {
        operands.setBenchmarkName("multiplication4");
        long beforeMemory = getUsedMemory();
        new MatrixMultiplication().columnMajorMatrixMultiplication(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void multiplication5(Operands operands) throws InterruptedException {
        operands.setBenchmarkName("multiplication5");
        long beforeMemory = getUsedMemory();
        multiplyMatricesParallel(operands.a, operands.b, 4);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }
}

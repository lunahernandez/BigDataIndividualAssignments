package org.ulpgc;

import org.openjdk.jmh.annotations.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.ulpgc.SparseMatrixCOOMul.*;
import static org.ulpgc.SparseMatrixCSCMul.convertToCSC;
import static org.ulpgc.SparseMatrixCSRMul.convertToCSR;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)
public class MatrixMultiplicationBenchmarking {


    @State(Scope.Thread)
    public static class Operands {
        public double[][] a;
        public double[][] b;

        @Param({"64", "128", "512", "1024", "2048"})
        public int matrixSize;

        @Param({"0.0", "0.5", "0.9"})
        public double sparsityThreshold;

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
                    if (random.nextDouble() < sparsityThreshold) {
                        a[i][j] = 0;
                        b[i][j] = 0;
                    } else {
                        a[i][j] = random.nextDouble();
                        b[i][j] = random.nextDouble();
                    }
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
                        writer.write("name,matrix_size,sparsity,average_memory_used_mib\n");
                    }
                    writer.write(currentBenchmark + "," + matrixSize + "," + sparsityThreshold + "," + averageMemoryUsedMiB + "\n");
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
    public void naiveMultiplication(Operands operands) {
        operands.setBenchmarkName("naiveMultiplication");
        long beforeMemory = getUsedMemory();
        new NaiveMatrixMultiplication().multiply(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void blockMultiplication(Operands operands) {
        operands.setBenchmarkName("blockMultiplication");
        long beforeMemory = getUsedMemory();
        BlockMatrixMultiplication.multiply(operands.a, operands.b, 10);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }


    @Benchmark
    public void CSRMultiplication(Operands operands) {
        operands.setBenchmarkName("CSRMultiplication");
        long beforeMemory = getUsedMemory();
        SparseMatrixCSRMul.CSRMatrix csrA = convertToCSR(operands.a);
        SparseMatrixCSRMul.CSRMatrix csrB = convertToCSR(operands.b);
        csrA.multiply(csrB);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void CSCMultiplication(Operands operands) {
        operands.setBenchmarkName("CSCMultiplication");
        long beforeMemory = getUsedMemory();
        SparseMatrixCSCMul.CSCMatrix cscA = convertToCSC(operands.a);
        SparseMatrixCSCMul.CSCMatrix cscB = convertToCSC(operands.b);
        cscA.multiply(cscB);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void COOMultiplication(Operands operands) {
        operands.setBenchmarkName("COOMultiplication");
        long beforeMemory = getUsedMemory();
        SparseMatrixCOOMul.COOMatrixByRow cooA = convertToCOOByRow(operands.a);
        SparseMatrixCOOMul.COOMatrixByColumn cooB = convertToCOOByColumn(operands.b);
        multiply(cooA, cooB);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }


    @Benchmark
    public void strassenMultiplication(Operands operands) {
        operands.setBenchmarkName("multiply");
        long beforeMemory = getUsedMemory();
        StrassenMatrixMultiplication.multiply(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

    @Benchmark
    public void loopUnrollingMultiplication(Operands operands) {
        operands.setBenchmarkName("loopUnrollingMultiplication");
        long beforeMemory = getUsedMemory();
        new LoopUnrollingMatrixMultiplication().multiply(operands.a, operands.b);
        long afterMemory = getUsedMemory();
        long memoryUsed = afterMemory - beforeMemory;

        operands.recordMemoryUsage(memoryUsed);
    }

}

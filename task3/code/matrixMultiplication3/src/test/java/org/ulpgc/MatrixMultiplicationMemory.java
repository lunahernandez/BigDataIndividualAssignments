package org.ulpgc;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class MatrixMultiplicationMemory {

    public static void main(String[] args) throws IOException, InterruptedException {
        int[] matrixSizes = {64, 128, 512, 1024, 2048};
        int[] listNumberOfThreads = {1, 2, 4, 8};

        File file = new File("task3/data/memory_usage.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("method,matrix_size,num_threads,average_memory_used_kib\n");

            for (int matrixSize : matrixSizes) {
                for (int nThreads : listNumberOfThreads) {
                    double averageMemoryNaive = measureMemoryForMethod(matrixSize, 1, "Naive");
                    double averageMemoryParallel = measureMemoryForMethod(matrixSize, nThreads, "Parallel");
                    double averageMemoryVectorized = measureMemoryForMethod(matrixSize, 1, "Vectorized");

                    writer.write(String.format(Locale.US, "NaiveMultiplication,%d,%d,%.15f\n",
                            matrixSize, 1, averageMemoryNaive));
                    writer.write(String.format(Locale.US, "ParallelMultiplication,%d,%d,%.15f\n",
                            matrixSize, nThreads, averageMemoryParallel));
                    writer.write(String.format(Locale.US, "VectorizedMultiplication,%d,%d,%.15f\n",
                            matrixSize, 1, averageMemoryVectorized));
                }
            }
        }
    }

    private static double measureMemoryForMethod(int matrixSize, int nThreads, String method) throws InterruptedException {
        int numExperiments = 5;
        double totalMemoryUsed = 0.0;

        for (int i = 0; i < numExperiments; i++) {
            totalMemoryUsed += measureMemoryForMethodOnce(matrixSize, nThreads, method);
        }

        return totalMemoryUsed / numExperiments;
    }

    private static double measureMemoryForMethodOnce(int matrixSize, int nThreads, String method) throws InterruptedException {
        double[][] a = new double[matrixSize][matrixSize];
        double[][] b = new double[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                a[i][j] = Math.random();
                b[i][j] = Math.random();
            }
        }

        System.gc();
        Thread.sleep(100);
        long beforeMemory = getUsedMemory();

        switch (method) {
            case "Naive":
                NaiveMatrixMultiplication.multiply(a, b);
                break;
            case "Parallel":
                ParallelMatrixMultiplication.multiplyMatricesParallel(a, b, nThreads);
                break;
            case "Vectorized":
                RealMatrix matrixA = MatrixUtils.createRealMatrix(a);
                RealMatrix matrixB = MatrixUtils.createRealMatrix(b);
                matrixA.multiply(matrixB);
                break;
            default:
                throw new IllegalArgumentException("Unknown multiplication method: " + method);
        }

        System.gc();
        Thread.sleep(100);
        long afterMemory = getUsedMemory();

        return Math.max(0.0, (afterMemory - beforeMemory) / 1024.0);
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}

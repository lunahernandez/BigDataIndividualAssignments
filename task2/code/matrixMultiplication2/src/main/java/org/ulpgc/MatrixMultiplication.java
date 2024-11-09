package org.ulpgc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MatrixMultiplication {

    public static void main(String[] args) throws IOException {
        int[] matrixSizes = {64, 128, 512, 1024, 2048};
        double[] sparsities = {0.0, 0.5, 0.9};

        File file = new File("memory_usage.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("method,matrix_size,sparsity,average_memory_used_mib\n");

            for (int matrixSize : matrixSizes) {
                for (double sparsity : sparsities) {
                    double averageMemoryNaive = measureMemoryForMethod(matrixSize, sparsity, "Naive");
                    double averageMemoryBlock = measureMemoryForMethod(matrixSize, sparsity, "Block");
                    double averageMemoryStrassen = measureMemoryForMethod(matrixSize, sparsity, "Strassen");
                    double averageMemoryLoopUnrolling = measureMemoryForMethod(matrixSize, sparsity, "LoopUnrolling");

                    // Write memory usage for each multiplication method
                    writer.write(String.format("NaiveMultiplication,%d,%.1f,%.15f\n", matrixSize, sparsity, averageMemoryNaive));
                    writer.write(String.format("BlockMultiplication,%d,%.1f,%.15f\n", matrixSize, sparsity, averageMemoryBlock));
                    writer.write(String.format("StrassenMultiplication,%d,%.1f,%.15f\n", matrixSize, sparsity, averageMemoryStrassen));
                    writer.write(String.format("LoopUnrollingMultiplication,%d,%.1f,%.15f\n", matrixSize, sparsity, averageMemoryLoopUnrolling));
                }
            }
        }

        System.out.println("CSV file generated successfully.");
    }

    private static double measureMemoryForMethod(int matrixSize, double sparsity, String method) {
        int numExperiments = 5;
        double totalMemoryUsed = 0.0;

        for (int i = 0; i < numExperiments; i++) {
            totalMemoryUsed += measureMemoryForMethodOnce(matrixSize, sparsity, method);
        }

        return totalMemoryUsed / numExperiments / (1024.0 * 1024.0); // Convert to MiB
    }

    private static double measureMemoryForMethodOnce(int matrixSize, double sparsity, String method) {
        double[][] a = new double[matrixSize][matrixSize];
        double[][] b = new double[matrixSize][matrixSize];

        // Populate matrices with given sparsity
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (Math.random() < sparsity) {
                    a[i][j] = 0;
                    b[i][j] = 0;
                } else {
                    a[i][j] = Math.random();
                    b[i][j] = Math.random();
                }
            }
        }

        long beforeMemory = getUsedMemory();

        switch (method) {
            case "Naive":
                NaiveMatrixMultiplication naive = new NaiveMatrixMultiplication();
                naive.multiply(a, b);
                break;
            case "Block":
                BlockMatrixMultiplication block = new BlockMatrixMultiplication();
                block.multiply(a, b, 100);
                break;
            case "Strassen":
                StrassenMatrixMultiplication strassen = new StrassenMatrixMultiplication();
                strassen.multiply(a, b);
                break;
            case "LoopUnrolling":
                LoopUnrollingMatrixMultiplication loopUnrolling = new LoopUnrollingMatrixMultiplication();
                loopUnrolling.multiply(a, b);
                break;
            default:
                throw new IllegalArgumentException("Unknown multiplication method: " + method);
        }

        long afterMemory = getUsedMemory();

        return (afterMemory - beforeMemory) / (1024.0 * 1024.0); // Convert to MiB
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}

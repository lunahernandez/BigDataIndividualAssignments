package org.ulpgc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import static org.ulpgc.SparseMatrixCOOMul.*;
import static org.ulpgc.SparseMatrixCSCMul.convertToCSC;
import static org.ulpgc.SparseMatrixCSRMul.convertToCSR;

public class MatrixMultiplicationMemory {

    public static void main(String[] args) throws IOException {
        int[] matrixSizes = {64, 128, 512, 1024, 2048};
        double[] sparsities = {0.0, 0.5, 0.9};

        File file = new File("memory_usage.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("method,matrix_size,sparsity,average_memory_used_mib\n");

            for (int matrixSize : matrixSizes) {
                for (double sparsity : sparsities) {
                    System.out.println("Evaluating matrixSize=" + matrixSize + " and sparsity=" + sparsity);
                    double averageMemoryNaive = measureMemoryForMethod(matrixSize, sparsity, "Naive");
                    double averageMemoryBlock = measureMemoryForMethod(matrixSize, sparsity, "Block");
                    double averageMemoryLoopUnrolling = measureMemoryForMethod(matrixSize, sparsity, "LoopUnrolling");
                    double averageMemoryCOO = measureMemoryForMethod(matrixSize, sparsity, "COO");
                    double averageMemoryCSC = measureMemoryForMethod(matrixSize, sparsity, "CSC");
                    double averageMemoryCSR = measureMemoryForMethod(matrixSize, sparsity, "CSR");

                    writer.write(String.format(Locale.US, "NaiveMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryNaive));
                    writer.write(String.format(Locale.US, "BlockMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryBlock));
                    writer.write(String.format(Locale.US, "LoopUnrollingMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryLoopUnrolling));
                    writer.write(String.format(Locale.US, "COOMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryCOO));
                    writer.write(String.format(Locale.US, "CSCMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryCSC));
                    writer.write(String.format(Locale.US, "CSRMultiplication,%d,%.1f,%.15f\n",
                            matrixSize, sparsity, averageMemoryCSR));
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

        return totalMemoryUsed / numExperiments / (1024.0 * 1024.0);
    }

    private static double measureMemoryForMethodOnce(int matrixSize, double sparsity, String method) {
        double[][] a = new double[matrixSize][matrixSize];
        double[][] b = new double[matrixSize][matrixSize];

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
                NaiveMatrixMultiplication.multiply(a, b);
                break;
            case "Block":
                BlockMatrixMultiplication.multiply(a, b, 100);
                break;
            case "LoopUnrolling":
                LoopUnrollingMatrixMultiplication loopUnrolling = new LoopUnrollingMatrixMultiplication();
                loopUnrolling.multiply(a, b);
                break;
            case "COO":
                SparseMatrixCOOMul.COOMatrixByRow cooA = convertToCOOByRow(a);
                SparseMatrixCOOMul.COOMatrixByColumn cooB = convertToCOOByColumn(b);
                multiply(cooA, cooB);
                break;
            case "CSC":
                SparseMatrixCSCMul.CSCMatrix cscA = convertToCSC(a);
                SparseMatrixCSCMul.CSCMatrix cscB = convertToCSC(b);
                cscA.multiply(cscB);
                break;
            case "CSR":
                SparseMatrixCSRMul.CSRMatrix csrA = convertToCSR(a);
                SparseMatrixCSRMul.CSRMatrix csrB = convertToCSR(b);
                csrA.multiply(csrB);
                break;
            default:
                throw new IllegalArgumentException("Unknown multiplication method: " + method);
        }

        long afterMemory = getUsedMemory();

        return (afterMemory - beforeMemory) / (1024.0 * 1024.0);
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
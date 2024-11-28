package org.ulpgc;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelMatrixMultiplication {

    static class MatrixMultiplicationTask extends Thread {
        private final double[][] A;
        private final double[][] B;
        private final double[][] C;
        private final int startRow;
        private final int endRow;

        public MatrixMultiplicationTask(double[][] A, double[][] B, double[][] C, int startRow, int endRow) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < B[0].length; j++) {
                    for (int k = 0; k < B.length; k++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
            }
        }
    }

    public static double[][] multiplyMatricesParallel(double[][] A, double[][] B, int numThreads) throws InterruptedException {
        if (A[0].length != B.length) {
            throw new IllegalArgumentException("Incompatible matrix dimensions for multiplication.");
        }

        int rowsA = A.length;
        int colsB = B[0].length;

        double[][] C = new double[rowsA][colsB];
        MatrixMultiplicationTask[] tasks = new MatrixMultiplicationTask[numThreads];
        int rowsPerThread = rowsA / numThreads;
        int remainingRows = rowsA % numThreads;

        int startRow = 0;
        for (int i = 0; i < numThreads; i++) {
            int endRow = startRow + rowsPerThread + (i < remainingRows ? 1 : 0);
            tasks[i] = new MatrixMultiplicationTask(A, B, C, startRow, endRow);
            tasks[i].start();
            startRow = endRow;
        }

        for (MatrixMultiplicationTask task : tasks) {
            task.join();
        }

        return C;
    }

    public static double[][] multiplyMatricesParallelWithExecutors(double[][] A, double[][] B, int numThreads) throws InterruptedException {
        if (A[0].length != B.length) {
            throw new IllegalArgumentException("Incompatible matrix dimensions for multiplication.");
        }

        int rowsA = A.length;
        int colsB = B[0].length;

        double[][] C = new double[rowsA][colsB];
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        int rowsPerThread = rowsA / numThreads;
        int remainingRows = rowsA % numThreads;

        int startRow = 0;
        for (int i = 0; i < numThreads; i++) {
            int endRow = startRow + rowsPerThread + (i < remainingRows ? 1 : 0);
            executor.execute(new MatrixMultiplicationTask(A, B, C, startRow, endRow));
            startRow = endRow;
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        return C;
    }


    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        double[][] matrixA = {
                {1, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 3},
                {4, 0, 5, 0}
        };

        double[][] matrixB = {
                {0, 0, 1, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 3},
                {0, 4, 0, 0}
        };

        System.out.println("Matrix A:");
        printMatrix(matrixA);

        System.out.println("\nMatrix B:");
        printMatrix(matrixB);

        System.out.println("\nParallel Multiplication (4 threads):");
        double[][] parallelResult = multiplyMatricesParallel(matrixA, matrixB, 4);
        printMatrix(parallelResult);
        double[][] parallelExecutorResult = multiplyMatricesParallelWithExecutors(matrixA, matrixB, 4);
        printMatrix(parallelExecutorResult);
    }
}

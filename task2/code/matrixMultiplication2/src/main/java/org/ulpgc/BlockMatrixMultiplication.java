package org.ulpgc;

import java.util.Random;

public class BlockMatrixMultiplication {
    public static void main(String[] args) {
        int n = 1024;
        double[][] a = new double[n][n];
        double[][] b = new double[n][n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextDouble();
                b[i][j] = random.nextDouble();
            }
        }

        System.out.println("Performing matrix multiplication with blocking...");
        double[][] c = multiply(a, b, 100);

        System.out.println("\nFinal result matrix c:");
        printMatrix(c);
    }

    public static double[][] multiply(double[][] a, double[][] b, int block_size) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
        for (int i = 0; i < n; i += block_size) {
            for (int j = 0; j < n; j += block_size) {
                for (int k = 0; k < n; k += block_size) {
                    multiplyBlock(a, b, c, i, j, k, n, block_size);
                }
            }
        }
        return c;
    }

    private static void multiplyBlock(double[][] a, double[][] b, double[][] c, int rowBlock, int colBlock, int kBlock, int n, int block_size) {

        for (int i = rowBlock; i < Math.min(rowBlock + block_size, n); i++) {
            for (int j = colBlock; j < Math.min(colBlock + block_size, n); j++) {
                double sum = 0;
                for (int k = kBlock; k < Math.min(kBlock + block_size, n); k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] += sum;
            }
        }
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] doubles : matrix) {
            for (double aDouble : doubles) {
                System.out.printf("%.2f ", aDouble);
            }
            System.out.println();
        }
    }
}

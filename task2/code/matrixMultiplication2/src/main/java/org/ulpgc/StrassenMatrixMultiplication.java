package org.ulpgc;

import java.util.Arrays;

public class StrassenMatrixMultiplication {

    public static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];

        if (n == 1) {
            result[0][0] = A[0][0] * B[0][0];
        } else {
            double[][] A11 = new double[n / 2][n / 2];
            double[][] A12 = new double[n / 2][n / 2];
            double[][] A21 = new double[n / 2][n / 2];
            double[][] A22 = new double[n / 2][n / 2];
            double[][] B11 = new double[n / 2][n / 2];
            double[][] B12 = new double[n / 2][n / 2];
            double[][] B21 = new double[n / 2][n / 2];
            double[][] B22 = new double[n / 2][n / 2];

            splitMatrix(A, A11, 0, 0);
            splitMatrix(A, A12, 0, n / 2);
            splitMatrix(A, A21, n / 2, 0);
            splitMatrix(A, A22, n / 2, n / 2);
            splitMatrix(B, B11, 0, 0);
            splitMatrix(B, B12, 0, n / 2);
            splitMatrix(B, B21, n / 2, 0);
            splitMatrix(B, B22, n / 2, n / 2);

            double[][] P1 = multiply(A11, subtractMatrix(B12, B22));
            double[][] P2 = multiply(addMatrix(A11, A12), B22);
            double[][] P3 = multiply(addMatrix(A21, A22), B11);
            double[][] P4 = multiply(A22, subtractMatrix(B21, B11));
            double[][] P5 = multiply(addMatrix(A11, A22), addMatrix(B11, B22));
            double[][] P6 = multiply(subtractMatrix(A12, A22), addMatrix(B21, B22));
            double[][] P7 = multiply(subtractMatrix(A11, A21), addMatrix(B11, B12));

            double[][] C11 = addMatrix(subtractMatrix(addMatrix(P5, P4), P2), P6);
            double[][] C12 = addMatrix(P1, P2);
            double[][] C21 = addMatrix(P3, P4);
            double[][] C22 = subtractMatrix(subtractMatrix(addMatrix(P5, P1), P3), P7);

            mergeMatrix(C11, result, 0, 0);
            mergeMatrix(C12, result, 0, n / 2);
            mergeMatrix(C21, result, n / 2, 0);
            mergeMatrix(C22, result, n / 2, n / 2);
        }

        return result;
    }

    public static void splitMatrix(double[][] parent, double[][] child, int rowOffset, int colOffset) {
        for (int i = 0; i < child.length; i++) {
            for (int j = 0; j < child.length; j++) {
                child[i][j] = parent[i + rowOffset][j + colOffset];
            }
        }
    }

    public static void mergeMatrix(double[][] child, double[][] parent, int rowOffset, int colOffset) {
        for (int i = 0; i < child.length; i++) {
            for (int j = 0; j < child.length; j++) {
                parent[i + rowOffset][j + colOffset] = child[i][j];
            }
        }
    }

    public static double[][] addMatrix(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] + B[i][j];
            }
        }
        return result;
    }

    public static double[][] subtractMatrix(double[][] A, double[][] B) {
        int n = A.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = A[i][j] - B[i][j];
            }
        }
        return result;
    }


    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }
}

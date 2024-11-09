package org.ulpgc;

public class LoopUnrollingMatrixMultiplication {
    public double[][] multiply(double[][] a, double[][] b) {
        int n = a.length;
        double[][] result = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j += 2) {
                result[i][j] = 0;
                result[i][j + 1] = 0;

                for (int k = 0; k < n; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                    result[i][j + 1] += a[i][k] * b[k][j + 1];
                }
            }
        }
        return result;
    }
}

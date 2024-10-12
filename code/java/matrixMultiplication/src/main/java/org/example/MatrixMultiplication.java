package org.example;

public class MatrixMultiplication {

    public void execute(double[][] a, double[][] b) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }

    public void blockMatrixMultiplication(double[][] a, double[][] b, int block_size) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
        for (int i = 0; i < n; i += block_size) {
            for (int j = 0; j < n; j += block_size) {
                for (int k = 0; k < n; k += block_size) {
                    multiplyBlock(a, b, i, j, k, block_size);
                }
            }
        }
    }

    private static void multiplyBlock(double[][] a, double[][] b, int rowBlock, int colBlock, int kBlock, int block_size) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
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


    public void rowMajorMatrixMultiplication(double[][] a, double[][] b) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                c[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    c[i][j] += a[i][k] * a[k][j];
                }
            }
        }
    }

    public void columnMajorMatrixMultiplication(double[][] a, double[][] b) {
        assert a.length == b.length;
        int n = a.length;
        double[][] c = new double[n][n];
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                c[i][j] = 0;
                for (int k = 0; k < n; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }
}

package org.ulpgc;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class VectorizedMatrixMultiplication {

    public static RealMatrix multiply(RealMatrix A, RealMatrix B) {
        if (A.getColumnDimension() != B.getRowDimension()) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }
        return A.multiply(B);
    }

    public static void printMatrix(RealMatrix matrix) {
        for (double[] row : matrix.getData()) {
            for (double value : row) {
                System.out.printf("%.2f ", value);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        double[][] dataA = {
                {1, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 3},
                {4, 0, 5, 0}
        };

        double[][] dataB = {
                {0, 0, 1, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 3},
                {0, 4, 0, 0}
        };

        RealMatrix A = MatrixUtils.createRealMatrix(dataA);
        RealMatrix B = MatrixUtils.createRealMatrix(dataB);

        System.out.println("Matrix A:");
        printMatrix(A);
        System.out.println("Matrix B:");
        printMatrix(B);

        RealMatrix result = multiply(A, B);

        System.out.println("Result of A x B:");
        printMatrix(result);
    }
}

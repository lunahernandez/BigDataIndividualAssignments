package org.ulpgc;

import java.util.Random;

import static org.ulpgc.SparseMatrixCOOMul.*;
import static org.ulpgc.SparseMatrixCSCMul.convertToCSC;
import static org.ulpgc.SparseMatrixCSRMul.convertToCSR;

public class MatrixMultiplication {


    public static void main(String[] args) {
        int n = 4;
        double[][] a = new double[n][n];
        double[][] b = new double[n][n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextDouble();
                b[i][j] = random.nextDouble();
            }
        }

        double[][] c1 = new NaiveMatrixMultiplication().multiply(a, b);
        double[][] c2 = BlockMatrixMultiplication.multiply(a, b, 2);
        double[][] c3 = StrassenMatrixMultiplication.multiply(a, b);
        double[][] c4 = new LoopUnrollingMatrixMultiplication().multiply(a, b);

        SparseMatrixCOOMul.COOMatrixByRow cooA = convertToCOOByRow(a);
        SparseMatrixCOOMul.COOMatrixByColumn cooB = convertToCOOByColumn(b);
        COOResultMatrix c5 = multiply(cooA, cooB);

        SparseMatrixCSRMul.CSRMatrix csrA = convertToCSR(a);
        SparseMatrixCSRMul.CSRMatrix csrB = convertToCSR(b);
        SparseMatrixCSRMul.CSRMatrix c6 = csrA.multiply(csrB);

        SparseMatrixCSCMul.CSCMatrix cscA = convertToCSC(a);
        SparseMatrixCSCMul.CSCMatrix cscB = convertToCSC(b);
        SparseMatrixCSCMul.CSCMatrix c7 = cscA.multiply(cscB);

        // Check if results are equals
        System.out.println("Matrix c1 and c2 are equal: " + matricesAreEqual(c1, c2));
        System.out.println("Matrix c1 and c3 are equal: " + matricesAreEqual(c1, c3));
        System.out.println("Matrix c1 and c4 are equal: " + matricesAreEqual(c1, c4));
        System.out.println("Matrix c1 in dense format:");
        printMatrix(c1);

        System.out.println("Matrix c5 in COO format:");
        c5.printCOO();

        System.out.println("Matrix c6 in dense format:");
        c6.printDenseMatrix();

        System.out.println("Matrix c7 in dense format:");
        c7.printDenseMatrix();


    }

    private static boolean matricesAreEqual(double[][] C1, double[][] C2) {
        for (int i = 0; i < C1.length; i++) {
            for (int j = 0; j < C1[i].length; j++) {
                if (Math.abs(C1[i][j] - C2[i][j]) > 1e-9) {
                    return false;
                }
            }
        }
        return true;
    }


    public static void printMatrix(double[][] matrix) {
        int n = matrix.length;
        for (double[] doubles : matrix) {
            for (int j = 0; j < n; j++) {
                System.out.print(doubles[j] + "\t");
            }
            System.out.println();
        }
    }
}

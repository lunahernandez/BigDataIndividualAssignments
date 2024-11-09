package org.ulpgc;

import java.util.*;

public class SparseMatrixCOOMul {

    static class COOMatrixByRow {
        List<List<double[]>> rows;
        int rowCount, colCount;

        COOMatrixByRow(int rowCount, int colCount) {
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.rows = new ArrayList<>(rowCount);

            for (int i = 0; i < rowCount; i++) {
                rows.add(new ArrayList<>());
            }
        }

        public void addValue(int row, int col, double value) {
            if (value != 0) {
                rows.get(row).add(new double[]{col, value});
            }
        }

        public void printCOO() {
            System.out.println("COO Representation (by rows):");
            for (int i = 0; i < rowCount; i++) {
                System.out.print("Row " + i + ": ");
                for (double[] entry : rows.get(i)) {
                    System.out.print("(" + (int) entry[0] + ", " + entry[1] + ") ");
                }
                System.out.println();
            }
        }
    }

    static class COOMatrixByColumn {
        List<List<double[]>> columns;
        int rowCount, colCount;

        COOMatrixByColumn(int rowCount, int colCount) {
            this.rowCount = rowCount;
            this.colCount = colCount;
            this.columns = new ArrayList<>(colCount);

            for (int i = 0; i < colCount; i++) {
                columns.add(new ArrayList<>());
            }
        }

        public void addValue(int row, int col, double value) {
            if (value != 0) {
                columns.get(col).add(new double[]{row, value});
            }
        }

        public void printCOO() {
            System.out.println("COO Representation (by columns):");
            for (int j = 0; j < colCount; j++) {
                System.out.print("Column " + j + ": ");
                for (double[] entry : columns.get(j)) {
                    System.out.print("(" + (int) entry[0] + ", " + entry[1] + ") ");
                }
                System.out.println();
            }
        }
    }

    static class COOResultMatrix {
        Map<String, Double> values;

        COOResultMatrix() {
            values = new HashMap<>();
        }

        public void addValue(int row, int col, double value) {
            if (value != 0) {
                String key = row + "," + col;
                values.put(key, values.getOrDefault(key, 0.0) + value);
            }
        }

        public void printCOO() {
            System.out.println("COO Result Matrix:");
            for (Map.Entry<String, Double> entry : values.entrySet()) {
                String[] keyParts = entry.getKey().split(",");
                int row = Integer.parseInt(keyParts[0]);
                int col = Integer.parseInt(keyParts[1]);
                System.out.println("(" + row + ", " + col + ", " + entry.getValue() + ")");
            }
        }
    }

    public static COOResultMatrix multiply(COOMatrixByRow A, COOMatrixByColumn B) {
        if (A.colCount != B.rowCount) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        COOResultMatrix result = new COOResultMatrix();

        for (int i = 0; i < A.rowCount; i++) {
            List<double[]> rowA = A.rows.get(i);

            for (int j = 0; j < B.colCount; j++) {
                List<double[]> colB = B.columns.get(j);

                int indexA = 0;
                int indexB = 0;

                while (indexA < rowA.size() && indexB < colB.size()) {
                    double[] a = rowA.get(indexA);
                    double[] b = colB.get(indexB);

                    if ((int) a[0] == (int) b[0]) {
                        double product = a[1] * b[1];
                        result.addValue(i, j, product);

                        indexA++;
                        indexB++;
                    } else if ((int) a[0] < (int) b[0]) {
                        indexA++;
                    } else {
                        indexB++;
                    }
                }
            }
        }

        return result;
    }


    public static COOMatrixByRow convertToCOOByRow(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        COOMatrixByRow cooMatrix = new COOMatrixByRow(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    cooMatrix.addValue(i, j, matrix[i][j]);
                }
            }
        }
        return cooMatrix;
    }

    public static COOMatrixByColumn convertToCOOByColumn(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        COOMatrixByColumn cooMatrix = new COOMatrixByColumn(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] != 0) {
                    cooMatrix.addValue(i, j, matrix[i][j]);
                }
            }
        }
        return cooMatrix;
    }

    public static COOMatrixByColumn convertByRowToByColumn(COOMatrixByRow rowMatrix) {
        COOMatrixByColumn colMatrix = new COOMatrixByColumn(rowMatrix.rowCount, rowMatrix.colCount);

        for (int i = 0; i < rowMatrix.rowCount; i++) {
            for (double[] tuple : rowMatrix.rows.get(i)) {
                colMatrix.addValue((int) tuple[0], i, tuple[1]);
            }
        }

        return colMatrix;
    }

    public static void main(String[] args) {
        int n = 2000;
        double[][] a = new double[n][n];
        double[][] b = new double[n][n];
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = random.nextDouble();
                b[i][j] = random.nextDouble();
            }
        }

        COOMatrixByRow cooA = convertToCOOByRow(a);
        COOMatrixByColumn cooB = convertToCOOByColumn(b);

        System.out.println("Matrix A in COO format:");
        cooA.printCOO();
        System.out.println("\nMatrix B in COO format:");
        cooB.printCOO();

        System.out.println("\nMultiplying Matrix A and Matrix B:");
        COOResultMatrix resultMatrix = multiply(cooA, cooB);

        System.out.println("\nResulting Matrix in COO format:");
        resultMatrix.printCOO();
    }
}

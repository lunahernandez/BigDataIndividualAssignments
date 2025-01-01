package org.ulpgc;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Arrays;

public class MatrixMultiplicationClient {

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        int[][] matrixA = {
                {1, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 0, 0, 3},
                {4, 0, 5, 0}
        };

        int[][] matrixB = {
                {0, 0, 1, 0},
                {2, 0, 0, 0},
                {0, 0, 0, 3},
                {0, 4, 0, 0}
        };

        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int colsB = matrixB[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication");
        }

        IMap<Integer, int[]> tasks = hazelcastInstance.getMap("tasks");
        IMap<Integer, int[]> results = hazelcastInstance.getMap("results");

        for (int i = 0; i < rowsA; i++) {
            tasks.put(i, matrixA[i]);
        }

        for (int i = 0; i < colsB; i++) {
            tasks.put(rowsA + i, getColumn(matrixB, i));
        }

        System.out.println("Matrix A:");
        for (int[] row : matrixA) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("Matrix B:");
        for (int[] row : matrixB) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("Waiting for results...");

        while (results.size() < rowsA) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int[][] resultMatrix = new int[rowsA][colsB];
        for (int i = 0; i < rowsA; i++) {
            int[] rowResult = results.get(i);
            resultMatrix[i] = rowResult;
        }

        System.out.println("Result:");
        for (int[] row : resultMatrix) {
            System.out.println(Arrays.toString(row));
        }

        hazelcastInstance.shutdown();
    }

    private static int[] getColumn(int[][] matrix, int colIndex) {
        int[] column = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        return column;
    }
}

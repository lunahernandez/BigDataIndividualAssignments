package org.ulpgc;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MatrixMultiplicationClient {

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService = hazelcastInstance.getExecutorService("executorService");

        int expectedMembers = 4;
        long timeoutMillis = 10000;
        waitForClusterMembers(hazelcastInstance, expectedMembers, timeoutMillis);


        int[][] matrixA = generateRandomMatrix(10, 10, 0, 10);
        int[][] matrixB = generateRandomMatrix(10, 10, 0, 10);

        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int rowsB = matrixB.length;
        int colsB = matrixB[0].length;

        assertMatricesDimensions(colsA, rowsB);
        printInitialMatrices(matrixA, matrixB);
        Integer[][] resultMatrix = getResultMatrix(hazelcastInstance, rowsA, colsB, matrixA, matrixB, executorService);
        printMatrixResult(rowsA, colsB, resultMatrix);

        hazelcastInstance.shutdown();
    }

    private static Integer[][] getResultMatrix(HazelcastInstance hazelcastInstance, int rowsA, int colsB,
                                               int[][] matrixA, int[][] matrixB, IExecutorService executorService) {
        List<Member> members = hazelcastInstance.getCluster().getMembers().stream().toList();

        Integer[][] resultMatrix = new Integer[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                MatrixMultiplicationTask task = new MatrixMultiplicationTask(matrixA[i], getColumn(matrixB, j));
                Member targetMember = members.get((i * colsB + j) % members.size());
                System.out.println("Sending task for position (" + i + "," + j + ") to member: " + targetMember.getAddress());
                Future<Integer> future = executorService.submitToMember(task, targetMember);
                getMatrixResults(future, resultMatrix, i, j);
            }
        }
        return resultMatrix;
    }

    private static void assertMatricesDimensions(int colsA, int rowsB) {
        if (colsA != rowsB) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication");
        }
    }

    public static int[][] generateRandomMatrix(int rows, int cols, int min, int max) {
        int[][] matrix = new int[rows][cols];
        Random random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = random.nextInt(max - min + 1) + min;
            }
        }

        return matrix;
    }

    private static void waitForClusterMembers(HazelcastInstance instance, int expectedMembers, long timeoutMillis) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (instance.getCluster().getMembers().size() >= expectedMembers) {
                return;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting for cluster members", e);
            }
        }
        throw new RuntimeException("Timeout waiting for cluster members to join");
    }


    private static void getMatrixResults(Future<Integer> future, Integer[][] resultMatrix, int i, int j) {
        try {
            Integer result = future.get();
            resultMatrix[i][j] = result;
            System.out.println("Result from task for position (" + i + "," + j + "): " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void printInitialMatrices(int[][] matrixA, int[][] matrixB) {
        System.out.println("Matrix A:");
        for (int[] row : matrixA) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("Matrix B:");
        for (int[] row : matrixB) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void printMatrixResult(int rowsA, int colsB, Integer[][] resultMatrix) {
        System.out.println("Matrix Result:");
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                System.out.print(resultMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int[] getColumn(int[][] matrix, int colIndex) {
        int[] column = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        return column;
    }
}

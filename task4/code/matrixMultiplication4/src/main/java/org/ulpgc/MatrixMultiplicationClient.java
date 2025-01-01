package org.ulpgc;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MatrixMultiplicationClient {

    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IExecutorService executorService = hazelcastInstance.getExecutorService("executorService");

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

        System.out.println("Matrix A:");
        for (int[] row : matrixA) {
            System.out.println(Arrays.toString(row));
        }

        System.out.println("Matrix B:");
        for (int[] row : matrixB) {
            System.out.println(Arrays.toString(row));
        }

        List<Member> members = hazelcastInstance.getCluster().getMembers().stream().toList();

        Integer[][] resultMatrix = new Integer[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                MatrixMultiplicationTask task = new MatrixMultiplicationTask(matrixA[i], getColumn(matrixB, j), i, j);

                Member targetMember = members.get((i * colsB + j) % members.size());

                System.out.println("Sending task for position (" + i + "," + j + ") to member: " + targetMember.getAddress());

                Future<Integer> future = executorService.submitToMember(task, targetMember);

                try {
                    Integer result = future.get();
                    resultMatrix[i][j] = result;
                    System.out.println("Result from task for position (" + i + "," + j + "): " + result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Matrix Result:");
        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                System.out.print(resultMatrix[i][j] + " ");
            }
            System.out.println();
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

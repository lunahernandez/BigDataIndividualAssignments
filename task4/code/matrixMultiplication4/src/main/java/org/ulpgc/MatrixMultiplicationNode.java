package org.ulpgc;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Arrays;
import java.util.Map;

public class MatrixMultiplicationNode {
    public static void main(String[] args) {
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();

        IMap<Integer, int[]> tasks = hazelcastInstance.getMap("tasks");
        IMap<Integer, int[]> results = hazelcastInstance.getMap("results");

        int totalTasks = tasks.size();
        int rowsA = totalTasks / 2;
        int colsB = totalTasks - rowsA;

        System.out.println("Number of rows in A: " + rowsA);
        System.out.println("Number of columns in B: " + colsB);

        while (true) {
            try {
                for (Map.Entry<Integer, int[]> task : tasks.entrySet()) {
                    int key = task.getKey();
                    int[] rowOrColumn = task.getValue();

                    if (key < rowsA) {
                        System.out.println("Node processing row " + key + ": " + Arrays.toString(rowOrColumn));
                        int[] rowResult = new int[colsB];

                        for (int j = 0; j < colsB; j++) {
                            int[] col = tasks.get(rowsA + j);

                            while (col == null) {
                                col = tasks.get(rowsA + j);
                                Thread.sleep(100);
                            }

                            rowResult[j] = multiply(rowOrColumn, col);
                        }

                        results.put(key, rowResult);
                        tasks.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int multiply(int[] row, int[] col) {
        int sum = 0;
        for (int i = 0; i < row.length; i++) {
            sum += row[i] * col[i];
        }
        System.out.println("Multiplying row: " + Arrays.toString(row) + " with column: " + Arrays.toString(col) + " = " + sum);
        return sum;
    }
}

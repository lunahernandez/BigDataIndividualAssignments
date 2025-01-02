package org.ulpgc;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class MatrixMultiplicationTask implements Callable<Integer>, Serializable {

    private final int[] rowA;
    private final int[] columnB;

    public MatrixMultiplicationTask(int[] rowA, int[] columnB) {
        this.rowA = rowA;
        this.columnB = columnB;
    }

    @Override
    public Integer call() {
        int result = 0;
        for (int k = 0; k < rowA.length; k++) {
            result += rowA[k] * columnB[k];
        }

        return result;
    }
}

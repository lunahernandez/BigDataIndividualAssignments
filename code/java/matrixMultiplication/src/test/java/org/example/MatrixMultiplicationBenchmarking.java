package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MatrixMultiplicationBenchmarking {


    @State(Scope.Thread)
    public static class Operands {
        public double[][] a;
        public double[][] b;

        @Param({"10", "100", "500"})
        public int matrixSize;

        @Setup
        public void setup() {
            int n = matrixSize;
            a = new double[n][n];
            b = new double[n][n];
            Random random = new Random();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    a[i][j] = random.nextDouble();
                    b[i][j] = random.nextDouble();
                }
            }
        }
    }


    @Benchmark
    public void multiplication(Operands operands) {
        new MatrixMultiplication().execute(operands.a, operands.b);
    }
}









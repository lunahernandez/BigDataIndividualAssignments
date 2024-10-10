package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands{
        private final int n = 10;
        private final double[][] a = new double[n][n];
        private final double[][] b = new double[n][n];

        @Setup
        public void setup() {
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
    public void multiplication(Operands operands){
        new MatrixMultiplication().execute(operands.a, operands.b);
    }
}











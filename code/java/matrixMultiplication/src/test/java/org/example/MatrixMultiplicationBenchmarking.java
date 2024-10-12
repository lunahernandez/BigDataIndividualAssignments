package org.example;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.example.ParallelMatrixMultiplication.multiplyMatricesParallel;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {
        public double[][] a;
        public double[][] b;

        @Param({"10", "50", "100" , "500", "1000"})
        public int matrixSize;

        @Setup(Level.Trial)
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
    public void multiplication1(Operands operands) {
        new MatrixMultiplication().execute(operands.a, operands.b);
    }

    @Benchmark
    public void multiplication2(Operands operands) {
        new MatrixMultiplication().blockMatrixMultiplication(operands.a, operands.b, 10);
    }

    @Benchmark
    public void multiplication3(Operands operands) {
        new MatrixMultiplication().rowMajorMatrixMultiplication(operands.a, operands.b);
    }

    @Benchmark
    public void multiplication4(Operands operands) {
        new MatrixMultiplication().columnMajorMatrixMultiplication(operands.a, operands.b);
    }

    @Benchmark
    public void multiplication5(Operands operands) throws InterruptedException {
        multiplyMatricesParallel(operands.a, operands.b, 4);
    }
}

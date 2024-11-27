package org.ulpgc;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class GlobalMatrixState {
        public double[][] a;
        public double[][] b;

        @Param({"64", "128", "512", "1024", "2048"})
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

    @State(Scope.Thread)
    public static class ParallelBenchmarkState {
        @Param({"2", "4", "8"})
        public int numThreads;
    }

    @Benchmark
    public double[][] naiveMatrixMultiplication(GlobalMatrixState state) {
        return NaiveMatrixMultiplication.multiply(state.a, state.b);
    }

    @Benchmark
    public double[][] parallelMatrixMultiplication(GlobalMatrixState matrixState, ParallelBenchmarkState parallelState)
            throws InterruptedException {
        return ParallelMatrixMultiplication.multiplyMatricesParallel(
                matrixState.a,
                matrixState.b,
                parallelState.numThreads
        );
    }

    @Benchmark
    public double[][] parallelExecutorsMatrixMultiplication(GlobalMatrixState matrixState, ParallelBenchmarkState parallelState)
            throws InterruptedException {
        return ParallelMatrixMultiplication.multiplyMatricesParallelWithExecutors(
                matrixState.a,
                matrixState.b,
                parallelState.numThreads
        );
    }

    @Benchmark
    public double[][] vectorizedMatrixMultiplication(GlobalMatrixState matrixState) {

        RealMatrix A = MatrixUtils.createRealMatrix(matrixState.a);
        RealMatrix B = MatrixUtils.createRealMatrix(matrixState.b);
        return VectorizedMatrixMultiplication.multiply(A, B).getData();
    }

}

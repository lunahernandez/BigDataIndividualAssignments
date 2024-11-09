package org.ulpgc;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.ulpgc.SparseMatrixCOOMul.*;
import static org.ulpgc.SparseMatrixCSCMul.convertToCSC;
import static org.ulpgc.SparseMatrixCSRMul.convertToCSR;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 5, time = 1)
public class MatrixMultiplicationBenchmarking {

    @State(Scope.Thread)
    public static class Operands {
        public double[][] a;
        public double[][] b;

        @Param({"64", "128", "512", "1024", "2048"})
        public int matrixSize;

        @Param({"0.0", "0.5", "0.9"})
        public double sparsityThreshold;

        @Setup(Level.Trial)
        public void setup() {
            int n = matrixSize;
            a = new double[n][n];
            b = new double[n][n];
            Random random = new Random();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (random.nextDouble() < sparsityThreshold) {
                        a[i][j] = 0;
                        b[i][j] = 0;
                    } else {
                        a[i][j] = random.nextDouble();
                        b[i][j] = random.nextDouble();
                    }
                }
            }
        }
    }

    @Benchmark
    public void naiveMultiplication(Operands operands) {
        new NaiveMatrixMultiplication().multiply(operands.a, operands.b);
    }

    @Benchmark
    public void blockMultiplication(Operands operands) {
        BlockMatrixMultiplication.multiply(operands.a, operands.b, 10);
    }

    @Benchmark
    public void CSRMultiplication(Operands operands) {
        SparseMatrixCSRMul.CSRMatrix csrA = convertToCSR(operands.a);
        SparseMatrixCSRMul.CSRMatrix csrB = convertToCSR(operands.b);
        csrA.multiply(csrB);
    }

    @Benchmark
    public void CSCMultiplication(Operands operands) {
        SparseMatrixCSCMul.CSCMatrix cscA = convertToCSC(operands.a);
        SparseMatrixCSCMul.CSCMatrix cscB = convertToCSC(operands.b);
        cscA.multiply(cscB);
    }

    @Benchmark
    public void COOMultiplication(Operands operands) {
        SparseMatrixCOOMul.COOMatrixByRow cooA = convertToCOOByRow(operands.a);
        SparseMatrixCOOMul.COOMatrixByColumn cooB = convertToCOOByColumn(operands.b);
        multiply(cooA, cooB);
    }

    @Benchmark
    public void strassenMultiplication(Operands operands) {
        StrassenMatrixMultiplication.multiply(operands.a, operands.b);
    }

    @Benchmark
    public void loopUnrollingMultiplication(Operands operands) {
        new LoopUnrollingMatrixMultiplication().multiply(operands.a, operands.b);
    }
}

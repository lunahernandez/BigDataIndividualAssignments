import csv
import random
import time

from memory_profiler import profile
import pytest
from matrix_multiplication import (matrix_multiplication,
                                   matrix_multiplication_numpy_dot,
                                   matrix_multiplication_numpy_matmul)


def read_matrix_sizes_from_csv(filename, delimiter=','):
    sizes = []
    with open(filename, newline='') as csvfile:
        csvreader = csv.reader(csvfile, delimiter=delimiter)
        for row in csvreader:
            sizes.append(int(row[0]))
    return sizes


matrix_sizes = read_matrix_sizes_from_csv('data/matrix_sizes.csv')


@pytest.mark.parametrize("n", matrix_sizes[:-2])
@profile
def test_matrix_multiplication_1(benchmark, n):
    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication, args=(a, b, n,),
                       iterations=100, rounds=10)


@pytest.mark.parametrize("n", matrix_sizes[:-2])
@profile
def test_matrix_multiplication_2(benchmark, n):
    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication, args=(a, b, n,),
                       iterations=100, rounds=10, warmup_rounds=5)


@pytest.mark.parametrize("n", matrix_sizes)
@profile
def test_matrix_multiplication_3(benchmark, n):
    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication_numpy_dot, args=(a, b,),
                       iterations=100, rounds=10, warmup_rounds=5)


@pytest.mark.parametrize("n", matrix_sizes)
@profile
def test_matrix_multiplication_4(benchmark, n):
    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication_numpy_matmul, args=(a, b,),
                       iterations=100, rounds=10, warmup_rounds=5)

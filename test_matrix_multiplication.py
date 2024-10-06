import csv
import random
import pytest
from matrix_multiplication import matrix_multiplication


def read_matrix_sizes_from_csv(filename, delimiter=','):
    sizes = []
    with open(filename, newline='') as csvfile:
        csvreader = csv.reader(csvfile, delimiter=delimiter)
        for row in csvreader:
            sizes.append(int(row[0]))
    return sizes


matrix_sizes = read_matrix_sizes_from_csv('matrix_sizes.csv')


@pytest.mark.parametrize("n", matrix_sizes)
def test_matrix_multiplication_pedantic(benchmark, n):
    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication, args=(a, b, n,),
                       iterations=100, rounds=10)

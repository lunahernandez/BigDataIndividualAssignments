import csv
import random

import numpy as np
from memory_profiler import profile


@profile
def matrix_multiplication(a, b, n):
    c = [[0 for _ in range(n)] for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                c[i][j] += a[i][k] * b[k][j]
    return c


@profile
def matrix_multiplication_numpy_dot(a, b):
    c = np.dot(a, b)
    return c


@profile
def matrix_multiplication_numpy_matmul(a, b):
    c = np.matmul(a, b)
    return c


def main():
    sizes = []
    with open('data/matrix_sizes.csv', newline='') as csvfile:
        csvreader = csv.reader(csvfile, delimiter=',')
        for row in csvreader:
            sizes.append(int(row[0]))
    for n in sizes:
        a = [[random.random() for _ in range(n)] for _ in range(n)]
        b = [[random.random() for _ in range(n)] for _ in range(n)]
        if n < 500:
            matrix_multiplication(a, b, n)
        matrix_multiplication_numpy_dot(a, b)
        matrix_multiplication_numpy_matmul(a, b)


if __name__ == "__main__":
    main()

import numpy as np


def matrix_multiplication(a, b, n):
    c = [[0 for _ in range(n)] for _ in range(n)]
    for i in range(n):
        for j in range(n):
            for k in range(n):
                c[i][j] += a[i][k] * b[k][j]
    return c


def matrix_multiplication_numpy_dot(a, b):
    c = np.dot(a, b)
    return c


def matrix_multiplication_numpy_matmul(a, b):
    c = np.matmul(a, b)
    return c

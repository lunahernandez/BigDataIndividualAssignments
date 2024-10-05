import random
from matrix_multiplication import matrix_multiplication


def test_matrix_multiplication(benchmark):
    n = 10

    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark(matrix_multiplication, a, b, n)


def test_matrix_multiplication_pedantic(benchmark):
    n = 10

    a = [[random.random() for _ in range(n)] for _ in range(n)]
    b = [[random.random() for _ in range(n)] for _ in range(n)]

    benchmark.pedantic(matrix_multiplication, args=(a, b, n,),
                       iterations=100, rounds=10)

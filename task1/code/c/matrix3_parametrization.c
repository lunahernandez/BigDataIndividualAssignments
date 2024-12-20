#include <stdio.h>
#include <stdlib.h>

#define BLOCK_SIZE 32 

// Function to multiply matrices using block multiplication
void matrix_multiply(double** a, double** b, double** c, int size) {
    // Initialize result matrix c to 0
    for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
            c[i][j] = 0; 
        }
    }

    // Block matrix multiplication
    for (int i = 0; i < size; i += BLOCK_SIZE) {
        for (int j = 0; j < size; j += BLOCK_SIZE) {
            for (int k = 0; k < size; k += BLOCK_SIZE) {
                // Multiply the blocks
                for (int ii = i; ii < i + BLOCK_SIZE && ii < size; ++ii) {
                    for (int jj = j; jj < j + BLOCK_SIZE && jj < size; ++jj) {
                        double sum = 0;
                        for (int kk = k; kk < k + BLOCK_SIZE && kk < size; ++kk) {
                            sum += a[ii][kk] * b[kk][jj]; 
                        }
                        c[ii][jj] += sum;
                    }
                }
            }
        }
    }
}

// Function to allocate memory for a dynamic matrix
double** allocate_matrix(int size) {
    double** matrix = (double**) malloc(size * sizeof(double*));
    for (int i = 0; i < size; ++i) {
        matrix[i] = (double*) malloc(size * sizeof(double));
    }
    return matrix;
}

// Function to free memory for a matrix
void free_matrix(double** matrix, int size) {
    for (int i = 0; i < size; ++i) {
        free(matrix[i]);
    }
    free(matrix);
}

// Function to fill a matrix with random values
void fill_matrix_with_random_values(double** matrix, int size) {
    for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
            matrix[i][j] = (double) rand() / RAND_MAX; 
        }
    }
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        printf("Usage: %s <matrix_size>\n", argv[0]);
        return -1;
    }

    int size = atoi(argv[1]);

    if (size <= 0) {
        printf("Matrix size must be a positive integer.\n");
        return -1;
    }

    double** a = allocate_matrix(size);
    double** b = allocate_matrix(size);
    double** c = allocate_matrix(size);

    fill_matrix_with_random_values(a, size);
    fill_matrix_with_random_values(b, size);

    matrix_multiply(a, b, c, size);
    
    free_matrix(a, size);
    free_matrix(b, size);
    free_matrix(c, size);

    return 0;
}


set terminal pngcairo size 800,600 
set output '../data/c_benchmark.png'
set datafile separator ","
set title "Matrix Size vs. Execution Time (ms)"
set xlabel "Matrix Size"
set ylabel "Execution Time (ms)"
set grid

set key inside top left 
set key autotitle columnheader  
set style data linespoints

set style line 1 linecolor rgb "red" lw 2 pt 7  
set style line 2 linecolor rgb "blue" lw 2 pt 7 
set style line 3 linecolor rgb "green" lw 2 pt 7

plot \
    '< grep "./matrix1_parametrization" ../data/results.csv' using 2:3 title "matrix1\\_parametrization" with linespoints ls 1, \
    '< grep "./matrix2_parametrization" ../data/results.csv' using 2:3 title "matrix2\\_parametrization" with linespoints ls 2, \
    '< grep "./matrix3_parametrization" ../data/results.csv' using 2:3 title "matrix3\\_parametrization" with linespoints ls 3


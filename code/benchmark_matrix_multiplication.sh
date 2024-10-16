#!/bin/bash

# Check if the correct number of arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <path_to_matrix_multiply_executable> <sizes_csv_file>"
    exit 1
fi

# Get the command-line arguments
matrix_multiply_executable=$1
sizes_csv_file=$2

# Check if results.csv exists and is not empty, otherwise add the header
if [ ! -s ../data/results.csv ]; then
    echo "Function Name,Matrix Size,Execution Time (milliseconds),Peak Memory Usage (KB)" > ../data/results.csv
fi

# Function to read sizes from a CSV file
read_sizes_from_csv() {
    local filename=$1
    local -n sizes_array=$2  # Use nameref to modify the array in the caller's scope
    sizes_array=()  # Clear the array

    while IFS=, read -r size; do
        # Skip empty lines and trim whitespace
        if [[ ! -z "$size" ]]; then
            sizes_array+=("$size")
        fi
    done < "$filename"
}

# Read sizes from the specified CSV file
read_sizes_from_csv "$sizes_csv_file" sizes

# Loop through each size and collect performance data
for size in "${sizes[@]}"; do

    # Run the program with perf to get execution time
    output=$(perf stat --log-fd 1 -e cycles,instructions,cache-misses "$matrix_multiply_executable" "$size" 2>&1)

    # Extract execution time from 'seconds time elapsed'
    execution_time=$(echo "$output" | grep 'seconds time elapsed' | awk '{print $1}')
    execution_time_fixed=$(echo "$execution_time" | sed 's/,/./')
    execution_time_ms=$(echo "$execution_time_fixed * 1000" | bc)

    # Run the program with /usr/bin/time to get peak memory usage
    output=$(/usr/bin/time -v "$matrix_multiply_executable" "$size" 2>&1)

    # Extract peak memory usage
    peak_memory=$(echo "$output" | grep 'Maximum resident set size' | awk '{print $6}')
    peak_memory_mib=$(echo "scale=2; $peak_memory / 1024" | bc)
    
    # Append results to CSV
    echo "$matrix_multiply_executable,$size,$execution_time_ms,$peak_memory_mib" >> ../data/results.csv
done


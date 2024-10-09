import csv
import json


def load_json_data(filepath):
    """Load data from a JSON file."""
    with open(filepath, 'r') as file:
        return json.load(file)


def write_csv_data(filepath, benchmarks):
    """Write benchmarks data to a CSV file."""
    fields_names = ['name', 'n', 'mean_time', 'rounds', 'iterations', 'warmup']

    with open(filepath, 'w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fields_names)
        writer.writeheader()
        for benchmark in benchmarks:
            writer.writerow(convert_benchmark_to_dict(benchmark))


def convert_benchmark_to_dict(benchmark):
    """Converts a JSON object to a dictionary."""
    return {
        'name': benchmark['name'],
        'n': benchmark['param'],
        'mean_time': benchmark['stats']['mean'] * 1000,  # tiempo en ms
        'rounds': benchmark['stats']['rounds'],
        'iterations': benchmark['stats']['iterations'],
        'warmup': benchmark['options']['warmup']
    }


def main():
    json_filepath = '../../data/0001_benchmarks.json'
    csv_filepath = '../../data/0001_benchmarks.csv'

    data = load_json_data(json_filepath)
    benchmarks = data['benchmarks']

    write_csv_data(csv_filepath, benchmarks)


if __name__ == "__main__":
    main()

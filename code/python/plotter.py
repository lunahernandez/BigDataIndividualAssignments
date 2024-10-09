import pandas as pd
import matplotlib.pyplot as plt


def load_data(filename):
    """Load data from a CSV file."""
    df = pd.read_csv(filename)
    return df


def process_data(df):
    """Process the data to ensure the mean times are numeric."""
    df['mean_time'] = pd.to_numeric(df['mean_time'], errors='coerce')
    return df


def plot_data(df):
    """Plot all data grouped by benchmark name."""
    unique_names = df['name'].unique()

    plt.figure(figsize=(10, 6))

    for name in unique_names:
        group = df[df['name'] == name]
        group = group.sort_values(by='n')
        plt.plot(group['n'], group['mean_time'], marker='o', linestyle='-', label=name)

    plt.title('Mean Time vs. Matrix Size for Different Functions')
    plt.xlabel('Matrix Size (n)')
    plt.ylabel('Mean Time (ms)')
    plt.grid()
    plt.xticks(df['n'].unique())
    plt.legend(title='Function Name', loc='best')
    plt.show()


def main():
    filename = '../../data/0001_benchmarks.csv'
    df = load_data(filename)
    df = process_data(df)
    plot_data(df)


if __name__ == "__main__":
    main()

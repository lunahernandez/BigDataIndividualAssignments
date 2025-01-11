# Distributed Execution of matrix multiplication
**Course:** Big Data

**Academic Year:** 2024-2025 

**Degree:** Data Science and Engineering

**School:** Escuela de Ingeniería Informática

**University:** Universidad de Las Palmas de Gran Canaria

## Summary of Functionality
This project shows the process of distributed matrix multiplication using Hazelcast. The system is developed to run locally with nodes created in Docker containers. The project consists of three main Java classes, each of which provides a specific function in the distributed execution process

### MatrixMultiplicationNode.java
This class initialises a new instance of Hazelcast within a node. It:
- Automatically connects to the existing Hazelcast cluster.
- Keeps the node available to receive and process tasks.
- Represents a 'worker' node in the cluster.

### MatrixMultiplicationClient.java
This class acts as a client that:
- Generates matrix multiplication tasks by splitting the operation into smaller subtasks.
- Sends these tasks to the nodes of the Hazelcast cluster.
- Collects the results from the nodes and displays the final matrix.

### MatrixMultiplicationTask.java
This Java class represents a matrix multiplication task. This task refers to a partial matrix multiplication. One row and one column shall be multiplied.

## Prerequisites
1. **Java and Maven**: Ensure that Java (version 8+) and Maven are installed.
2. **Docker**: Install Docker and Docker Desktop for managing containers.
3. **Project Download**: Clone or download the project to your local machine.

## Run the Project
1. **Create Docker Network**
To enable communication between containers, create a custom network:
```bash
docker network create hazelcast-network
```

2. **Build and Deploy**
Then, you have to create the images and the containers. To do this, you can use docker-compose. Use the following code to remove any image or container with the same names if exist, create .jar files and the use docker-compose to create the images and the containers.

```bash
docker rm hazelcast-node-1 hazelcast-node-2 hazelcast-node-3 matrix-multiplication-client 
docker rmi matrixmultiplication4-matrix-multiplication-client matrixmultiplication4-hazelcast-node-1 matrixmultiplication4-hazelcast-node-2 matrixmultiplication4-hazelcast-node-3
mvn clean install
docker-compose build
docker-compose up
```

The code above will create 4 nodes. One of them will represent the "client" which makes the matrix multiplication query and the one that receives the results. Multiplication tasks will be distributed between the existing nodes by Hazelcast.

## Experiments and Observations
The project was executed locally with multiple nodes as described above. Matrix multiplication tasks were distributed among the worker nodes. Although this distributed approach introduces overhead (e.g. data serialisation, task submission and result aggregation), it provides significant scalability advantages.

Here you have a brief comparison with naive and parallel approaches:
- **Execution time**: Distributed execution is expected to take longer locally compared to naive or parallel multiplication due to the added overhead of inter-node communication.
- **Scalability**: The main benefit lies in the ability to scale horizontally by adding more nodes, including physical devices, to the cluster.
- **Memory efficiency**: Distributed execution reduces memory usage on individual nodes, making it suitable for large-scale problems that would otherwise exceed the memory limits of a single machine.


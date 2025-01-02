# Distributed Execution of matrix multiplication
**Course:** Big Data

**Academic Year:** 2024-2025 

**Degree:** Data Science and Engineering

**School:** Escuela de Ingeniería Informática

**University:** Universidad de Las Palmas de Gran Canaria

## Summary of Functionality

### MatrixMultiplicationNode.java

### MatrixMultiplicationClient.java

### MatrixMultiplicationTask.java

## Run the Project
To run the project, you have to do these steps before:
1. **Download the project**. Download the project and store it in your computer.
2. **Open Docker Desktop**. Start Docker Desktop application.

Once you have that, you have to create a network to connect the nodes.
```bash
docker network create hazelcast-network
```
Then, you have to create the images and the containers. To do this, you can use docker-compose. Use the following code to remove any image or container with the same names if exist, create .jar files and the use docker-compose to create the images and the containers.

```bash
docker rm hazelcast-node-1 hazelcast-node-2 hazelcast-node-3 matrix-multiplication-client 
docker rmi matrixmultiplication4-matrix-multiplication-client matrixmultiplication4-hazelcast-node-1 matrixmultiplication4-hazelcast-node-2 matrixmultiplication4-hazelcast-node-3
mvn clean install
docker-compose build
docker-compose up
```

## Experiments and Results

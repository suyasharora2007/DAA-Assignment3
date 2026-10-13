# Design and Analysis of Algorithms — Assignment 3

## 1. Introduction

This project implements and evaluates graph algorithms using Java and Maven. The objective is to study the correctness, time complexity, and operational behaviour of graph algorithms on graphs of different sizes and densities.

The implementation includes Disjoint Set Union (DSU), Kruskal's algorithm, Prim's algorithm, Kosaraju's strongly connected components algorithm, Kahn's topological sorting algorithm, and DAG shortest paths.

## 2. Implementation

### 2.1 Graph Representation

Graphs are represented using adjacency lists. Each edge stores its source vertex, destination vertex, and integer weight. The graph implementation supports both directed and undirected graphs.

### 2.2 Disjoint Set Union

The DSU implementation uses path compression and union by size. It supports set initialization, finding representatives, merging sets, and determining the number of connected components.

### 2.3 Minimum Spanning Forest

Kruskal's algorithm sorts edges by weight and uses DSU to avoid adding edges that create cycles. Prim's algorithm grows a tree using a priority queue. Both algorithms are compared using their resulting total weights.

For disconnected graphs, the expected output is a minimum spanning forest rather than a single spanning tree.

### 2.4 Strongly Connected Components

Kosaraju's algorithm uses two depth-first search passes. The implementation uses an explicit stack to avoid recursion-depth problems on large graphs.

### 2.5 Topological Sorting

Kahn's algorithm uses vertex indegrees and a queue to construct a topological ordering. A graph is identified as cyclic if the resulting ordering does not contain all vertices.

### 2.6 DAG Shortest Paths

The shortest-path algorithm processes vertices in topological order. Every vertex with indegree zero is initialized with distance zero, representing a virtual super-source. Edge relaxation supports negative edge weights, provided the graph remains acyclic. Parent pointers are maintained to reconstruct paths.

## 3. Experimental Methodology

The benchmark runner uses the following vertex counts:

- 1,000
- 3,162
- 10,000
- 31,623
- 100,000

Sparse and dense graph configurations are considered using edge-count targets of approximately \(4n\) and \(20n\), respectively, subject to the limits of the graph type.

Runtime is measured using Java's `System.nanoTime()`. The benchmark performs five runs for the algorithms where repeated timing is implemented and uses the median runtime.

The benchmark output is stored in `results/benchmark.csv`. The CSV schema includes algorithm name, graph type, vertex count, edge count, runtime, DSU operations, edge scans, comparisons, maximum depth, and result.

## 4. Results and Discussion

The generated CSV contains 50 data rows, with 10 rows each for Kruskal, Prim, Kosaraju, Kahn, and DAG shortest paths.

The CSV should be analysed to compare runtime growth across graph sizes and densities. Kruskal's performance depends on edge sorting and DSU operations, while Prim's performance depends on priority-queue operations. Kosaraju, Kahn, and DAG shortest paths are expected to exhibit linear-time behaviour in the number of vertices and edges.

The recorded operation counters should be interpreted according to their definitions in the implementation. Before final submission, counter coverage, graph-generation rules, and all benchmark cases must be verified against the assignment specification.

**Insert verified tables and plots here after validating the final benchmark data. Do not report unverified measurements.**

## 5. Correctness and Testing

The project includes JUnit tests for graph representation, DSU operations, minimum spanning trees, strongly connected components, topological sorting, DAG shortest paths, graph loading, and graph generation.

The final validation should confirm that:

- Kruskal and Prim return equal minimum spanning forest weights.
- Disconnected graphs are handled correctly.
- Kosaraju correctly identifies strongly connected components and handles large graphs without recursive stack overflow.
- Kahn's algorithm detects cycles and returns valid topological orders for DAGs.
- DAG shortest paths support negative edge weights and reconstruct valid paths.
- Graph generators produce the required graph types and edge counts.

## 6. Conclusion

The project provides implementations of fundamental graph algorithms and a benchmark framework for comparing their behaviour on graphs of varying sizes and densities.

The final evaluation should be based on verified test results, validated operation counters, the assignment's required graph-generation procedure, and the measured CSV results. The report should be updated with actual tables and plots before submission.

## 7. Reproducibility

Run the test suite with:

```bash
mvn clean test
```

Compile the project with:

```bash
mvn compile
```

Run the benchmark from the project root after compilation with:

```bash
java -cp target/classes daa.benchmark.BenchmarkRunner
```

Benchmark output is written to `results/benchmark.csv`.
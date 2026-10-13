
package daa.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Graph {

    public static class Edge {
        public final int from;
        public final int to;
        public final int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private final int vertices;
    private final boolean directed;
    private final List<List<Edge>> adjacencyList;
    private final List<Edge> edges;

    public Graph(int vertices, boolean directed) {
        if (vertices < 0) {
            throw new IllegalArgumentException("Vertices cannot be negative");
        }

        this.vertices = vertices;
        this.directed = directed;
        this.adjacencyList = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        checkVertex(from);
        checkVertex(to);

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adjacencyList.get(from).add(edge);

        if (!directed) {
            adjacencyList.get(to).add(new Edge(to, from, weight));
        }
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                    "Invalid vertex: " + vertex
            );
        }
    }

    public int getVertices() {
        return vertices;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Edge> getNeighbors(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    public int getEdgeCount() {
        return edges.size();
    }
}

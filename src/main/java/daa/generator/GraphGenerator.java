
package daa.generator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import daa.graph.Graph;

public final class GraphGenerator {

    private GraphGenerator() {
    }

    public static Graph connectedUndirected(
            int vertices, int extraEdges, long seed) {

        validate(vertices, extraEdges);

        long maxEdges = (long) vertices * (vertices - 1) / 2;

        if (extraEdges > maxEdges - (vertices - 1L)) {
            throw new IllegalArgumentException("Too many edges requested");
        }

        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        for (int v = 1; v < vertices; v++) {
            int parent = random.nextInt(v);
            addUniqueUndirectedEdge(
                    graph, used, parent, v, random.nextInt(100) + 1);
        }

        int remaining = extraEdges;

        while (remaining > 0) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from == to) {
                continue;
            }

            long key = undirectedKey(from, to);

            if (used.add(key)) {
                graph.addEdge(from, to, random.nextInt(100) + 1);
                remaining--;
            }
        }

        return graph;
    }

    public static Graph directed(int vertices, int edges, long seed) {
        validate(vertices, edges);

        long maxEdges = (long) vertices * (vertices - 1);

        if (edges > maxEdges) {
            throw new IllegalArgumentException("Too many directed edges");
        }

        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        while (used.size() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from != to && used.add(directedKey(from, to))) {
                graph.addEdge(from, to, random.nextInt(100) + 1);
            }
        }

        return graph;
    }

    public static Graph dag(int vertices, int edges, long seed) {
        validate(vertices, edges);

        long maxEdges = (long) vertices * (vertices - 1) / 2;

        if (edges > maxEdges) {
            throw new IllegalArgumentException("Too many DAG edges");
        }

        Graph graph = new Graph(vertices, true);
        Random random = new Random(seed);
        Set<Long> used = new HashSet<>();

        while (used.size() < edges) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from < to && used.add(directedKey(from, to))) {
                graph.addEdge(from, to, random.nextInt(100) + 1);
            }
        }

        return graph;
    }

    private static void addUniqueUndirectedEdge(
            Graph graph, Set<Long> used, int from, int to, int weight) {

        used.add(undirectedKey(from, to));
        graph.addEdge(from, to, weight);
    }

    private static long undirectedKey(int from, int to) {
        int low = Math.min(from, to);
        int high = Math.max(from, to);
        return ((long) low << 32) | (high & 0xffffffffL);
    }

    private static long directedKey(int from, int to) {
        return ((long) from << 32) | (to & 0xffffffffL);
    }

    private static void validate(int vertices, int edges) {
        if (vertices < 1 || edges < 0) {
            throw new IllegalArgumentException(
                    "Vertices must be positive and edges non-negative");
        }
    }
}

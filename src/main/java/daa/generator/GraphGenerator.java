package daa.generator;

import daa.graph.Graph;

import java.util.Random;

public final class GraphGenerator {

    private GraphGenerator() {}

    /**
     * Generates the assignment-specified connected undirected graph.
     * The supplied Random must be seeded with 42 for reference results.
     */
    public static Graph connectedUndirected(int n, int m, Random rnd) {
        validateUndirected(n, m);
        if (m < n - 1) {
            throw new IllegalArgumentException("A connected graph needs at least n-1 edges");
        }

        Graph graph = new Graph(n, false);
        int[] perm = shuffledIdentity(n, rnd);

        for (int i = 1; i < n; i++) {
            int a = perm[rnd.nextInt(i)];
            int b = perm[i];
            graph.addEdge(a, b, 1 + rnd.nextInt(1000));
        }

        while (graph.getEdgeCount() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            graph.addEdge(a, b, 1 + rnd.nextInt(1000));
        }

        return graph;
    }

    /** Generates the assignment-specified directed graph. */
    public static Graph directed(int n, int m, Random rnd) {
        validateDirected(n, m);
        Graph graph = new Graph(n, true);

        while (graph.getEdgeCount() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }
            graph.addEdge(a, b, 1 + rnd.nextInt(1000));
        }

        return graph;
    }

    /** Generates the assignment-specified DAG using a random rank ordering. */
    public static Graph dag(int n, int m, Random rnd) {
        validateDirected(n, m);
        long maxEdges = (long) n * (n - 1) / 2;
        if (m > maxEdges) {
            throw new IllegalArgumentException("A DAG cannot have more than n(n-1)/2 edges");
        }

        Graph graph = new Graph(n, true);
        int[] rank = shuffledIdentity(n, rnd);

        while (graph.getEdgeCount() < m) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            while (b == a) {
                b = rnd.nextInt(n);
            }

            if (rank[a] > rank[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            graph.addEdge(a, b, 1 + rnd.nextInt(1000));
        }

        return graph;
    }

    private static int[] shuffledIdentity(int n, Random rnd) {
        int[] perm = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int temp = perm[i];
            perm[i] = perm[j];
            perm[j] = temp;
        }

        return perm;
    }

    private static void validateUndirected(int n, int m) {
        if (n < 1 || m < 0) {
            throw new IllegalArgumentException("Invalid graph size");
        }
        long max = (long) n * (n - 1) / 2;
        // The assignment generator permits parallel edges, so the practical
        // upper bound for m is not the simple-graph maximum.
        if (m < n - 1) {
            throw new IllegalArgumentException("Connected graph needs at least n-1 edges");
        }
    }

    private static void validateDirected(int n, int m) {
        if (n < 1 || m < 0) {
            throw new IllegalArgumentException("Invalid graph size");
        }
        long max = (long) n * (n - 1);
        if (m > max) {
            throw new IllegalArgumentException("Too many directed edges");
        }
    }
}

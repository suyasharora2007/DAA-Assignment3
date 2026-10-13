
package daa.generator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import daa.graph.Graph;

public class GraphGenerator {

    private GraphGenerator() {
    }

    public static Graph connectedUndirected(
            int vertices, int extraEdges, long seed) {

        if (vertices < 1 || extraEdges < 0) {
            throw new IllegalArgumentException(
                    "Vertices must be positive and extraEdges non-negative"
            );
        }

        long maxEdges = (long) vertices * (vertices - 1) / 2;

        if (extraEdges > maxEdges - (vertices - 1L)) {
            throw new IllegalArgumentException(
                    "Too many edges requested"
            );
        }

        Graph graph = new Graph(vertices, false);
        Random random = new Random(seed);
        Set<Long> edgeKeys = new HashSet<>();

        // Create a connected spanning tree.
        for (int v = 1; v < vertices; v++) {
            int parent = random.nextInt(v);
            int weight = random.nextInt(100) + 1;

            graph.addEdge(parent, v, weight);
            edgeKeys.add(edgeKey(parent, v));
        }

        // Add unique extra edges.
        while (extraEdges > 0) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);

            if (from == to) {
                continue;
            }

            long key = edgeKey(from, to);

            if (edgeKeys.add(key)) {
                int weight = random.nextInt(100) + 1;
                graph.addEdge(from, to, weight);
                extraEdges--;
            }
        }

        return graph;
    }

    private static long edgeKey(int from, int to) {
        int low = Math.min(from, to);
        int high = Math.max(from, to);

        return ((long) low << 32) | (high & 0xffffffffL);
    }
}

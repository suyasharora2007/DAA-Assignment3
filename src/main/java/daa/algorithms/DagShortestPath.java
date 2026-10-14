
package daa.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import daa.graph.Graph;

public class DagShortestPath {

    public static class Result {
        public final long[] distances;
        public final int[] parent;
        public final List<Integer> topologicalOrder;
        public final long edgeScans;
        public final long comparisons;

        public Result(
                long[] distances,
                int[] parent,
                List<Integer> topologicalOrder,
                long edgeScans,
                long comparisons) {
            this.distances = distances;
            this.parent = parent;
            this.topologicalOrder = topologicalOrder;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }

        public List<Integer> getPathTo(int target) {
            if (target < 0 || target >= distances.length) {
                throw new IllegalArgumentException(
                        "Target vertex is out of range.");
            }

            if (distances[target] == Long.MAX_VALUE) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();
            int current = target;

            while (current != -1) {
                path.add(current);
                current = parent[current];
            }

            Collections.reverse(path);
            return path;
        }
    }

    public static Result run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "DAG shortest path requires a directed graph.");
        }

        Kahn.Result topologicalResult = Kahn.run(graph);

        if (topologicalResult.hasCycle) {
            throw new IllegalArgumentException(
                    "Shortest paths require a directed acyclic graph.");
        }

        int n = graph.getVertices();

        long[] distances = new long[n];
        int[] parent = new int[n];

        for (int i = 0; i < n; i++) {
            distances[i] = Long.MAX_VALUE;
            parent[i] = -1;
        }

        // Every source vertex starts with distance zero.
        int[] indegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                indegree[edge.to]++;
            }
        }

        for (int vertex = 0; vertex < n; vertex++) {
            if (indegree[vertex] == 0) {
                distances[vertex] = 0;
            }
        }

        long edgeScans = 0;
        long comparisons = 0;

        // Relax edges in topological order.
        for (int u : topologicalResult.order) {
            comparisons++;

            if (distances[u] == Long.MAX_VALUE) {
                continue;
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                edgeScans++;

                int v = edge.to;
                long candidate = distances[u] + edge.weight;

                comparisons++;

                if (candidate < distances[v]) {
                    distances[v] = candidate;
                    parent[v] = u;
                }
            }
        }

        return new Result(
                distances,
                parent,
                topologicalResult.order,
                edgeScans,
                comparisons);
    }
}

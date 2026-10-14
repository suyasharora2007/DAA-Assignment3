package daa.algorithms;

import daa.graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Kahn {
    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long edgeScans;
        public final long comparisons;

        public Result(List<Integer> order, boolean hasCycle,
                      long edgeScans, long comparisons) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    public static Result run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Kahn's algorithm requires a directed graph.");
        }

        int n = graph.getVertices();
        int[] indegree = new int[n];

        // Build indegrees. This is O(V+E), but is intentionally not counted
        // as an algorithmic edge scan so edgeScans reports the m edges
        // processed by the topological-order pass, as required by the brief.
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                indegree[edge.to]++;
            }
        }

        Deque<Integer> queue = new ArrayDeque<>();
        long comparisons = 0;
        for (int vertex = 0; vertex < n; vertex++) {
            comparisons++;
            if (indegree[vertex] == 0) queue.offer(vertex);
        }

        List<Integer> order = new ArrayList<>(n);
        long edgeScans = 0;

        while (!queue.isEmpty()) {
            int u = queue.remove();
            order.add(u);

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                edgeScans++;
                indegree[edge.to]--;
                comparisons++;
                if (indegree[edge.to] == 0) queue.offer(edge.to);
            }
        }

        comparisons++;
        boolean hasCycle = order.size() != n;
        return new Result(order, hasCycle, edgeScans, comparisons);
    }
}

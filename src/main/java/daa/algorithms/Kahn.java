
package daa.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import daa.graph.Graph;

public class Kahn {

    public static class Result {
        public final List<Integer> order;
        public final boolean hasCycle;
        public final long edgeScans;

        public Result(List<Integer> order, boolean hasCycle,
                      long edgeScans) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.edgeScans = edgeScans;
        }
    }

    public static Result run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kahn requires a directed graph"
            );
        }

        int n = graph.getVertices();
        int[] inDegree = new int[n];

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                inDegree[edge.to]++;
            }
        }

        Queue<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> order = new ArrayList<>();
        long edgeScans = 0;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                edgeScans++;
                inDegree[edge.to]--;

                if (inDegree[edge.to] == 0) {
                    queue.offer(edge.to);
                }
            }
        }

        boolean hasCycle = order.size() != n;

        return new Result(order, hasCycle, edgeScans);
    }
}


package daa.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import daa.graph.Graph;

public class Prim {

    public static class Result {
        public final long totalWeight;
        public final List<Graph.Edge> edges;
        public final int components;
        public final long edgeScans;
        public final long comparisons;

        public Result(long totalWeight, List<Graph.Edge> edges,
                      int components, long edgeScans,
                      long comparisons) {
            this.totalWeight = totalWeight;
            this.edges = edges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
        }
    }

    public static Result run(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim requires an undirected graph"
            );
        }

        int n = graph.getVertices();
        boolean[] visited = new boolean[n];

        PriorityQueue<Graph.Edge> queue = new PriorityQueue<>(
                Comparator.comparingInt(edge -> edge.weight)
        );

        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;
        long comparisons = 0;
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            components++;
            visited[start] = true;

            for (Graph.Edge edge : graph.getNeighbors(start)) {
                queue.offer(edge);
            }

            while (!queue.isEmpty()) {
                Graph.Edge edge = queue.poll();
                comparisons++;

                if (visited[edge.to]) {
                    continue;
                }

                visited[edge.to] = true;
                mstEdges.add(edge);
                totalWeight += edge.weight;

                for (Graph.Edge next : graph.getNeighbors(edge.to)) {
                    edgeScans++;

                    if (!visited[next.to]) {
                        queue.offer(next);
                    }
                }
            }
        }

        return new Result(
                totalWeight,
                mstEdges,
                components,
                edgeScans,
                comparisons
        );
    }
}

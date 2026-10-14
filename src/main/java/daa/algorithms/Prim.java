
package daa.algorithms;

import java.util.ArrayList;
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

        public Result(
                long totalWeight,
                List<Graph.Edge> edges,
                int components,
                long edgeScans,
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

        List<Graph.Edge> mstEdges = new ArrayList<>();
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>(
            (a, b) -> {
                int weightCompare = Integer.compare(a.weight, b.weight);

                if (weightCompare != 0) {
                    return weightCompare;
                }

                int fromCompare = Integer.compare(a.from, b.from);

                if (fromCompare != 0) {
                    return fromCompare;
                }

                return Integer.compare(a.to, b.to);
            }
        );

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
                edgeScans++;
                comparisons++;

                if (!visited[edge.to]) {
                    pq.offer(edge);
                }
            }

            while (!pq.isEmpty()) {
                Graph.Edge edge = pq.poll();
                int next = edge.to;

                comparisons++;

                if (visited[next]) {
                    continue;
                }

                visited[next] = true;
                mstEdges.add(edge);
                totalWeight += edge.weight;

                for (Graph.Edge adjacent : graph.getNeighbors(next)) {
                    edgeScans++;
                    comparisons++;

                    if (!visited[adjacent.to]) {
                        pq.offer(adjacent);
                    }
                }
            }
        }

        return new Result(
            totalWeight, mstEdges, components, edgeScans, comparisons
        );
    }
}

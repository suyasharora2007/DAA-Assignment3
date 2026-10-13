
package daa.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import daa.graph.Graph;

public class DagShortestPath {

    public static class Result {
        public final long[] distances;
        public final int[] parent;
        public final List<Integer> topologicalOrder;
        public final long edgeScans;

        public Result(long[] distances, int[] parent,
                      List<Integer> topologicalOrder, long edgeScans) {
            this.distances = distances;
            this.parent = parent;
            this.topologicalOrder = topologicalOrder;
            this.edgeScans = edgeScans;
        }

        public List<Integer> getPathTo(int target) {
            if (target < 0 || target >= distances.length) {
                throw new IllegalArgumentException("Invalid target");
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
                    "DAG shortest paths require a directed graph"
            );
        }

        int n = graph.getVertices();
        Kahn.Result topo = Kahn.run(graph);

        if (topo.hasCycle) {
            throw new IllegalArgumentException(
                    "Shortest paths require a DAG"
            );
        }

        long[] distances = new long[n];
        int[] parent = new int[n];
        int[] inDegree = new int[n];

        Arrays.fill(distances, Long.MAX_VALUE);
        Arrays.fill(parent, -1);

        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getNeighbors(u)) {
                inDegree[edge.to]++;
            }
        }

        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                distances[v] = 0;
            }
        }

        long edgeScans = 0;

        for (int u : topo.order) {
            if (distances[u] == Long.MAX_VALUE) {
                continue;
            }

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                edgeScans++;
                long candidate = distances[u] + edge.weight;

                if (candidate < distances[edge.to]) {
                    distances[edge.to] = candidate;
                    parent[edge.to] = u;
                }
            }
        }

        return new Result(distances, parent, topo.order, edgeScans);
    }
}

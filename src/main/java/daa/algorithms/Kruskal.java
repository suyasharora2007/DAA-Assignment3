
package daa.algorithms;

import java.util.ArrayList;
import java.util.List;

import daa.graph.Graph;

public class Kruskal {

    public static class Result {
        public final long totalWeight;
        public final List<Graph.Edge> edges;
        public final int components;
        public final long edgeScans;
        public final long comparisons;
        public final long dsuOps;

        public Result(long totalWeight, List<Graph.Edge> edges,
                      int components, long edgeScans,
                      long comparisons, long dsuOps) {
            this.totalWeight = totalWeight;
            this.edges = edges;
            this.components = components;
            this.edgeScans = edgeScans;
            this.comparisons = comparisons;
            this.dsuOps = dsuOps;
        }
    }

    public static Result run(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal requires an undirected graph"
            );
        }

        List<Graph.Edge> sortedEdges =
                new ArrayList<>(graph.getEdges());

        long[] comparisons = {0};

        sortedEdges.sort((a, b) -> {
            comparisons[0]++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(graph.getVertices());
        List<Graph.Edge> mstEdges = new ArrayList<>();
        long totalWeight = 0;
        long edgeScans = 0;

        for (Graph.Edge edge : sortedEdges) {
            edgeScans++;

            if (dsu.find(edge.from) != dsu.find(edge.to)) {
                dsu.union(edge.from, edge.to);
                mstEdges.add(edge);
                totalWeight += edge.weight;
            }

            if (mstEdges.size() == graph.getVertices() - 1) {
                break;
            }
        }

        return new Result(
                totalWeight,
                mstEdges,
                dsu.components(),
                edgeScans,
                comparisons[0],
                dsu.getDsuOps()
        );
    }
}


package daa.benchmark;

import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.generator.GraphGenerator;
import daa.graph.Graph;

public class MstBenchmark {

    public static void main(String[] args) {
        int[] sizes = {100, 500, 1000};

        System.out.printf(
                "%-10s %-15s %-15s %-15s%n",
                "Vertices", "Edges", "Kruskal (ms)", "Prim (ms)"
        );

        for (int vertices : sizes) {
            int extraEdges = Math.min(
                    vertices * 3,
                    (int) Math.min(
                            Integer.MAX_VALUE,
                            (long) vertices * (vertices - 1) / 2
                                    - (vertices - 1L)
                    )
            );

            Graph graph = GraphGenerator.connectedUndirected(
                    vertices, vertices - 1 + extraEdges, new java.util.Random(42)
            );

            long start = System.nanoTime();
            Kruskal.Result kruskal = Kruskal.run(graph);
            long kruskalTime = System.nanoTime() - start;

            start = System.nanoTime();
            Prim.Result prim = Prim.run(graph);
            long primTime = System.nanoTime() - start;

            if (kruskal.totalWeight != prim.totalWeight) {
                throw new IllegalStateException(
                        "MST weights do not match for " + vertices
                                + " vertices"
                );
            }

            System.out.printf(
                    "%-10d %-15d %-15.3f %-15.3f%n",
                    vertices,
                    graph.getEdges().size(),
                    kruskalTime / 1_000_000.0,
                    primTime / 1_000_000.0
            );
        }
    }
}


package daa.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.generator.GraphGenerator;
import daa.graph.Graph;

public class BenchmarkRunner {

    private static final int[] SIZES = {
        1000, 3162, 10000, 31623, 100000
    };

    private static final int RUNS = 5;

    public static void main(String[] args) throws IOException {
        Path output = Path.of("results", "benchmark.csv");
        Files.createDirectories(output.getParent());

        StringBuilder csv = new StringBuilder();
        csv.append(
            "algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,"
            + "comparisons,max_depth,result\n"
        );

        for (int n : SIZES) {
            for (int multiplier : new int[]{4, 20}) {
                int m = Math.min(
                    multiplier * n,
                    n * (n - 1) / 2
                );

                System.out.printf(
                    "Preparing n=%d, density=%s%n",
                    n,
                    multiplier == 4 ? "sparse" : "dense"
                );

                benchmarkMst(csv, n, m);
                benchmarkDirected(csv, n, m);
            }
        }

        Files.writeString(output, csv.toString());
        System.out.println("CSV written to: " + output);
    }

    private static void benchmarkMst(
            StringBuilder csv, int n, int m) {

        Graph graph = GraphGenerator.connectedUndirected(
            n, m - (n - 1), 42L + n + m
        );

        long[] kruskalTimes = new long[RUNS];
        long[] primTimes = new long[RUNS];

        Kruskal.Result kruskalResult = null;
        Prim.Result primResult = null;

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            kruskalResult = Kruskal.run(graph);
            kruskalTimes[i] = System.nanoTime() - start;

            start = System.nanoTime();
            primResult = Prim.run(graph);
            primTimes[i] = System.nanoTime() - start;
        }

        if (kruskalResult.totalWeight != primResult.totalWeight) {
            throw new IllegalStateException(
                "Kruskal and Prim weights do not match"
            );
        }

        append(csv, "Kruskal", "undirected", n, m,
            median(kruskalTimes), kruskalResult.dsuOps,
            kruskalResult.edgeScans, kruskalResult.comparisons,
            0, kruskalResult.totalWeight);

        append(csv, "Prim", "undirected", n, m,
            median(primTimes), 0, primResult.edgeScans,
            primResult.comparisons, 0, primResult.totalWeight);
    }

    private static void benchmarkDirected(
            StringBuilder csv, int n, int m) {

        int dagEdges = Math.min(m, n * (n - 1) / 2);

        Graph directed = GraphGenerator.directed(
            n, Math.min(m, n * (n - 1)), 100L + n + m
        );

        long start = System.nanoTime();
        Kosaraju.Result scc = Kosaraju.run(directed);
        long sccTime = System.nanoTime() - start;

        append(csv, "Kosaraju", "directed", n,
            directed.getEdgeCount(), sccTime, 0,
            scc.edgeScans, 0, scc.maxDepth, scc.components);

        Graph dag = GraphGenerator.dag(
            n, dagEdges, 200L + n + m
        );

        long[] kahnTimes = new long[RUNS];
        long[] pathTimes = new long[RUNS];

        Kahn.Result kahnResult = null;
        DagShortestPath.Result pathResult = null;

        for (int i = 0; i < RUNS; i++) {
            start = System.nanoTime();
            kahnResult = Kahn.run(dag);
            kahnTimes[i] = System.nanoTime() - start;

            start = System.nanoTime();
            pathResult = DagShortestPath.run(dag);
            pathTimes[i] = System.nanoTime() - start;
        }

        append(csv, "Kahn", "dag", n, dag.getEdgeCount(),
            median(kahnTimes), 0, kahnResult.edgeScans,
            0, 0, kahnResult.hasCycle ? "cycle" : "acyclic");

        append(csv, "DagShortestPath", "dag", n,
            dag.getEdgeCount(), median(pathTimes), 0,
            pathResult.edgeScans, 0, 0, "completed");
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    private static void append(
            StringBuilder csv, String algorithm, String graph,
            int n, int m, long nanos, long dsuOps,
            long edgeScans, long comparisons, int maxDepth,
            Object result) {

        csv.append(String.format(
            Locale.US,
            "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%s%n",
            algorithm, graph, n, m, nanos / 1_000_000.0,
            dsuOps, edgeScans, comparisons, maxDepth, result
        ));
    }
}

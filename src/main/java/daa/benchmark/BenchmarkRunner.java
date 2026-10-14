package daa.benchmark;

import daa.algorithms.DagShortestPath;
import daa.algorithms.Kahn;
import daa.algorithms.Kosaraju;
import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.generator.GraphGenerator;
import daa.graph.Graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class BenchmarkRunner {

    private static final int[] SIZES = {1000, 3162, 10000, 31623, 100000};
    private static final int[] DENSITIES = {4, 20};
    private static final int RUNS = 5;

    private static final long[] REF_MST = {
        147970, 30363, 468911, 97589, 1486139,
        309550, 4715515, 968517, 15012094, 3056681
    };

    private static final int[] REF_SCC = {
        27, 1, 137, 1, 373, 1, 1188, 1, 3923, 1
    };

    private static final long[] REF_DAG_SUM = {
        396274, 252411, 1271911, 723157, 3941828,
        2381342, 12551891, 7376612, 40096539, 23512087
    };

    public static void main(String[] args) throws IOException {
        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());

        StringBuilder csv = new StringBuilder();
        csv.append("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result\n");

        int referenceIndex = 0;

        for (int n : SIZES) {
            for (int multiplier : DENSITIES) {
                int m = multiplier * n;
                System.out.printf("Preparing n=%d, density=%s%n", n,
                        multiplier == 4 ? "sparse" : "dense");

                Random mstRandom = new Random(42);
                Random directedRandom = new Random(42);
                Random dagRandom = new Random(42);

                long mstWeight = benchmarkMst(csv, n, m, mstRandom);
                int sccCount = benchmarkKosaraju(csv, n, m, directedRandom);
                long dagSum = benchmarkDag(csv, n, m, dagRandom);

                if (mstWeight != REF_MST[referenceIndex]) {
                    throw new IllegalStateException("MST reference mismatch at n=" + n + ", m=" + m
                            + ": expected " + REF_MST[referenceIndex] + ", got " + mstWeight);
                }
                if (sccCount != REF_SCC[referenceIndex]) {
                    throw new IllegalStateException("SCC reference mismatch at n=" + n + ", m=" + m
                            + ": expected " + REF_SCC[referenceIndex] + ", got " + sccCount);
                }
                if (dagSum != REF_DAG_SUM[referenceIndex]) {
                    throw new IllegalStateException("DAG reference mismatch at n=" + n + ", m=" + m
                            + ": expected " + REF_DAG_SUM[referenceIndex] + ", got " + dagSum);
                }

                System.out.printf("Reference OK: MST=%d, SCC=%d, DAG distance sum=%d%n",
                        mstWeight, sccCount, dagSum);
                referenceIndex++;
            }
        }

        String data = csv.toString();
        Files.writeString(output, data);
        // Keep the earlier project filename as a compatibility copy.
        Files.writeString(Path.of("results", "benchmark.csv"), data);
        System.out.println("CSV written to: " + output);
        System.out.println("Rows written: 50");
    }

    private static long benchmarkMst(StringBuilder csv, int n, int m, Random rnd) {
        Graph graph = GraphGenerator.connectedUndirected(n, m, rnd);
        long[] kruskalTimes = new long[RUNS];
        long[] primTimes = new long[RUNS];
        Kruskal.Result kruskal = null;
        Prim.Result prim = null;

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            kruskal = Kruskal.run(graph);
            kruskalTimes[i] = System.nanoTime() - start;

            start = System.nanoTime();
            prim = Prim.run(graph);
            primTimes[i] = System.nanoTime() - start;

            if (kruskal.totalWeight != prim.totalWeight) {
                throw new IllegalStateException("Kruskal/Prim mismatch at n=" + n + ", m=" + m);
            }
            if (kruskal.edges.size() != n - 1 || prim.edges.size() != n - 1) {
                throw new IllegalStateException("Connected MST must contain n-1 edges");
            }
        }

        append(csv, "Kruskal", "undirected", n, m, median(kruskalTimes),
                kruskal.dsuOps, kruskal.edgeScans, kruskal.comparisons, 0, kruskal.totalWeight);
        append(csv, "Prim", "undirected", n, m, median(primTimes),
                0, prim.edgeScans, prim.comparisons, 0, prim.totalWeight);
        return kruskal.totalWeight;
    }

    private static int benchmarkKosaraju(StringBuilder csv, int n, int m, Random rnd) {
        Graph graph = GraphGenerator.directed(n, m, rnd);
        long[] times = new long[RUNS];
        Kosaraju.Result result = null;

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            result = Kosaraju.run(graph);
            times[i] = System.nanoTime() - start;
        }

        if (result.edgeScans != 2L * m) {
            throw new IllegalStateException("Kosaraju must scan exactly 2m edges");
        }

        append(csv, "Kosaraju", "directed", n, m, median(times),
                0, result.edgeScans, 0, result.maxDepth, result.components.size());
        return result.components.size();
    }

    private static long benchmarkDag(StringBuilder csv, int n, int m, Random rnd) {
        Graph graph = GraphGenerator.dag(n, m, rnd);
        long[] kahnTimes = new long[RUNS];
        long[] pathTimes = new long[RUNS];
        Kahn.Result kahn = null;
        DagShortestPath.Result path = null;

        for (int i = 0; i < RUNS; i++) {
            long start = System.nanoTime();
            kahn = Kahn.run(graph);
            kahnTimes[i] = System.nanoTime() - start;

            start = System.nanoTime();
            path = DagShortestPath.run(graph);
            pathTimes[i] = System.nanoTime() - start;
        }

        if (kahn.hasCycle || kahn.order.size() != n || !isValidTopologicalOrder(graph, kahn.order)) {
            throw new IllegalStateException("Kahn returned an invalid DAG order");
        }
        if (kahn.edgeScans != m) {
            throw new IllegalStateException("Kahn must scan exactly m edges during processing");
        }
        if (path.edgeScans != m) {
            throw new IllegalStateException("DAG relaxation must scan exactly m edges");
        }

        long distanceSum = 0;
        for (long distance : path.distances) {
            if (distance != Long.MAX_VALUE) {
                distanceSum += distance;
            }
        }

        append(csv, "Kahn", "dag", n, m, median(kahnTimes),
                0, kahn.edgeScans, kahn.comparisons, 0, kahn.order.size());
        append(csv, "DagShortestPath", "dag", n, m, median(pathTimes),
                0, path.edgeScans, path.comparisons, 0, distanceSum);
        return distanceSum;
    }

    private static boolean isValidTopologicalOrder(Graph graph, java.util.List<Integer> order) {
        if (order.size() != graph.getVertices()) return false;
        int[] pos = new int[graph.getVertices()];
        for (int i = 0; i < order.size(); i++) pos[order.get(i)] = i;
        for (Graph.Edge edge : graph.getEdges()) {
            if (pos[edge.from] >= pos[edge.to]) return false;
        }
        return true;
    }

    private static long median(long[] values) {
        long[] sorted = values.clone();
        Arrays.sort(sorted);
        return sorted[sorted.length / 2];
    }

    private static void append(StringBuilder csv, String algorithm, String graph,
                               int n, int m, long nanos, long dsuOps,
                               long edgeScans, long comparisons, int maxDepth,
                               Object result) {
        csv.append(String.format(Locale.US,
                "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%s%n",
                algorithm, graph, n, m, nanos / 1_000_000.0,
                dsuOps, edgeScans, comparisons, maxDepth, result));
    }
}

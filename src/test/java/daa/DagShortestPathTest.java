package daa;

import daa.algorithms.DagShortestPath;
import daa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DagShortestPathTest {
    @Test
    void calculatesShortestPathsWithNegativeWeights() {
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 4); graph.addEdge(0, 2, 2); graph.addEdge(2, 1, -5);
        graph.addEdge(1, 3, 3); graph.addEdge(2, 3, 6);
        DagShortestPath.Result result = DagShortestPath.run(graph);
        assertEquals(0, result.distances[0]);
        assertEquals(-3, result.distances[1]);
        assertEquals(2, result.distances[2]);
        assertEquals(0, result.distances[3]);
        assertEquals(0, result.distances[4]);
    }

    @Test
    void restoresPath() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 2); graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 10); graph.addEdge(2, 3, 1);
        DagShortestPath.Result result = DagShortestPath.run(graph);
        assertEquals(6, result.distances[3]);
        assertEquals(List.of(0, 1, 2, 3), result.getPathTo(3));
    }

    @Test
    void matchesBellmanFordOnRandomDagWithNegativeWeights() {
        Random rnd = new Random(123);
        for (int trial = 0; trial < 100; trial++) {
            int n = 2 + rnd.nextInt(25);
            Graph g = new Graph(n, true);
            for (int u = 0; u < n; u++) {
                for (int v = u + 1; v < n; v++) {
                    if (rnd.nextDouble() < 0.15) g.addEdge(u, v, -20 + rnd.nextInt(41));
                }
            }
            DagShortestPath.Result r = DagShortestPath.run(g);
            long[] expected = bellmanFordVirtualSource(g);
            assertArrayEquals(expected, r.distances, "trial " + trial);
        }
    }

    @Test
    void rejectsCyclicGraph() {
        Graph graph = new Graph(2, true);
        graph.addEdge(0, 1, 1); graph.addEdge(1, 0, 1);
        assertThrows(IllegalArgumentException.class, () -> DagShortestPath.run(graph));
    }

    private static long[] bellmanFordVirtualSource(Graph g) {
        int n = g.getVertices();
        long[] d = new long[n];
        Arrays.fill(d, Long.MAX_VALUE);
        int[] indegree = new int[n];
        for (Graph.Edge e : g.getEdges()) indegree[e.to]++;
        for (int i = 0; i < n; i++) if (indegree[i] == 0) d[i] = 0;

        for (int pass = 0; pass < n - 1; pass++) {
            boolean changed = false;
            for (Graph.Edge e : g.getEdges()) {
                if (d[e.from] != Long.MAX_VALUE && d[e.from] + e.weight < d[e.to]) {
                    d[e.to] = d[e.from] + e.weight;
                    changed = true;
                }
            }
            if (!changed) break;
        }
        return d;
    }
}

package daa;

import daa.algorithms.Kosaraju;
import daa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class KosarajuTest {
    @Test
    void findsKnownComponents() {
        Graph g = new Graph(8, true);
        g.addEdge(0,1,1); g.addEdge(1,2,1); g.addEdge(2,0,1);
        g.addEdge(2,3,1); g.addEdge(3,4,1); g.addEdge(4,5,1);
        g.addEdge(5,3,1); g.addEdge(6,5,1); g.addEdge(6,7,1);
        Kosaraju.Result r = Kosaraju.run(g);
        assertEquals(4, r.components.size());
    }

    @Test
    void matchesBruteForceOnRandomGraphs() {
        Random rnd = new Random(99);
        for (int trial = 0; trial < 30; trial++) {
            int n = 2 + rnd.nextInt(60);
            Graph g = new Graph(n, true);
            int m = rnd.nextInt(n * n / 2 + 1);
            for (int i = 0; i < m; i++) {
                int u = rnd.nextInt(n), v = rnd.nextInt(n);
                g.addEdge(u, v, 1);
            }
            assertEquals(bruteScc(g), normalized(Kosaraju.run(g).components), "trial " + trial);
        }
    }

    @Test
    void stackSafeOn100000VertexPath() {
        int n = 100_000;
        Graph g = new Graph(n, true);
        for (int i = 0; i < n - 1; i++) g.addEdge(i, i + 1, 1);
        Kosaraju.Result r = assertDoesNotThrow(() -> Kosaraju.run(g));
        assertEquals(n, r.components.size());
        assertTrue(r.maxDepth >= n);
        assertEquals(2L * (n - 1), r.edgeScans);
    }

    private static Set<Set<Integer>> bruteScc(Graph g) {
        int n = g.getVertices();
        Set<Set<Integer>> result = new HashSet<>();
        for (int s = 0; s < n; s++) {
            Set<Integer> forward = reachable(g, s, false);
            Set<Integer> backward = reachable(g, s, true);
            Set<Integer> component = new HashSet<>(forward);
            component.retainAll(backward);
            result.add(component);
        }
        return result;
    }

    private static Set<Integer> reachable(Graph g, int start, boolean reverse) {
        Set<Integer> seen = new HashSet<>();
        ArrayDeque<Integer> q = new ArrayDeque<>();
        seen.add(start); q.add(start);
        while (!q.isEmpty()) {
            int u = q.remove();
            for (Graph.Edge e : g.getEdges()) {
                int from = reverse ? e.to : e.from;
                int to = reverse ? e.from : e.to;
                if (from == u && seen.add(to)) q.add(to);
            }
        }
        return seen;
    }

    private static Set<Set<Integer>> normalized(List<List<Integer>> components) {
        Set<Set<Integer>> out = new HashSet<>();
        for (List<Integer> c : components) out.add(new HashSet<>(c));
        return out;
    }
}

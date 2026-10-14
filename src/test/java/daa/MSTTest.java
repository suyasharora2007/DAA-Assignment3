package daa;

import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.generator.GraphGenerator;
import daa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MSTTest {
    @Test
    void kruskalFindsMinimumSpanningTree() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 10); graph.addEdge(0, 2, 6); graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15); graph.addEdge(2, 3, 4);
        Kruskal.Result result = Kruskal.run(graph);
        assertEquals(19, result.totalWeight);
        assertEquals(3, result.edges.size());
        assertEquals(1, result.components);
    }

    @Test
    void handlesDisconnectedGraph() {
        Graph graph = new Graph(4, false);
        graph.addEdge(0, 1, 2); graph.addEdge(2, 3, 3);
        Kruskal.Result k = Kruskal.run(graph);
        Prim.Result p = Prim.run(graph);
        assertEquals(5, k.totalWeight);
        assertEquals(k.totalWeight, p.totalWeight);
        assertEquals(2, k.edges.size());
        assertEquals(2, p.edges.size());
        assertEquals(2, k.components);
        assertEquals(2, p.components);
    }

    @Test
    void rejectsDirectedGraph() {
        Graph graph = new Graph(2, true);
        graph.addEdge(0, 1, 1);
        assertThrows(IllegalArgumentException.class, () -> Kruskal.run(graph));
        assertThrows(IllegalArgumentException.class, () -> Prim.run(graph));
    }

    @Test
    void kruskalAndPrimAgreeOn100RandomConnectedGraphs() {
        Random rnd = new Random(12345);
        for (int t = 0; t < 100; t++) {
            int n = 2 + rnd.nextInt(30);
            int max = n * (n - 1) / 2;
            int m = (n - 1) + rnd.nextInt(Math.max(1, max - n + 2));
            Graph graph = GraphGenerator.connectedUndirected(n, m, rnd);
            Kruskal.Result k = Kruskal.run(graph);
            Prim.Result p = Prim.run(graph);
            assertEquals(k.totalWeight, p.totalWeight, "trial " + t);
            assertEquals(n - 1, k.edges.size());
            assertEquals(n - 1, p.edges.size());
            assertEquals(1, k.components);
            assertEquals(1, p.components);
        }
    }
}

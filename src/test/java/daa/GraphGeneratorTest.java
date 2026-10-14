package daa;

import daa.generator.GraphGenerator;
import daa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GraphGeneratorTest {
    @Test
    void generatesConnectedGraphWithExpectedEdgeCount() {
        Graph graph = GraphGenerator.connectedUndirected(10, 14, new Random(42));
        assertEquals(10, graph.getVertices());
        assertFalse(graph.isDirected());
        assertEquals(14, graph.getEdgeCount());
    }

    @Test
    void sameSeedProducesSameEdges() {
        Graph a = GraphGenerator.connectedUndirected(20, 50, new Random(42));
        Graph b = GraphGenerator.connectedUndirected(20, 50, new Random(42));
        assertEquals(a.getEdgeCount(), b.getEdgeCount());
        for (int i = 0; i < a.getEdges().size(); i++) {
            Graph.Edge x = a.getEdges().get(i), y = b.getEdges().get(i);
            assertEquals(x.from, y.from);
            assertEquals(x.to, y.to);
            assertEquals(x.weight, y.weight);
        }
    }

    @Test
    void generatesDirectedGraphWithExpectedEdgeCount() {
        Graph graph = GraphGenerator.directed(10, 20, new Random(42));
        assertTrue(graph.isDirected());
        assertEquals(20, graph.getEdgeCount());
    }

    @Test
    void generatesDagWithoutBackwardEdges() {
        Graph graph = GraphGenerator.dag(20, 50, new Random(42));
        assertTrue(graph.isDirected());
        assertEquals(50, graph.getEdgeCount());

        // A graph generated from a rank ordering must be acyclic.
        KahnResultHelper.assertAcyclic(graph);
    }

    @Test
    void rejectsImpossibleConnectedGraph() {
        assertThrows(IllegalArgumentException.class,
                () -> GraphGenerator.connectedUndirected(3, 2, new Random(42)));
    }

    private static class KahnResultHelper {
        static void assertAcyclic(Graph graph) {
            int[] indegree = new int[graph.getVertices()];
            for (Graph.Edge e : graph.getEdges()) indegree[e.to]++;
            java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
            for (int i = 0; i < indegree.length; i++) if (indegree[i] == 0) q.add(i);
            int count = 0;
            while (!q.isEmpty()) {
                int u = q.remove(); count++;
                for (Graph.Edge e : graph.getNeighbors(u)) if (--indegree[e.to] == 0) q.add(e.to);
            }
            assertEquals(graph.getVertices(), count);
        }
    }
}

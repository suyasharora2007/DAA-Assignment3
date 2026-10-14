package daa;

import daa.algorithms.Kahn;
import daa.graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortTest {
    @Test
    void returnsValidTopologicalOrder() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1); graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1); graph.addEdge(2, 3, 1);
        Kahn.Result result = Kahn.run(graph);
        assertFalse(result.hasCycle);
        assertEquals(4, result.order.size());
        int[] position = new int[4];
        for (int i = 0; i < result.order.size(); i++) position[result.order.get(i)] = i;
        for (Graph.Edge e : graph.getEdges()) assertTrue(position[e.from] < position[e.to]);
        assertEquals(4, result.edgeScans);
    }

    @Test
    void detectsCycle() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1); graph.addEdge(1, 2, 1); graph.addEdge(2, 0, 1);
        Kahn.Result result = Kahn.run(graph);
        assertTrue(result.hasCycle);
        assertTrue(result.order.size() < 3);
    }

    @Test
    void handlesEmptyGraph() {
        Graph graph = new Graph(0, true);
        Kahn.Result result = Kahn.run(graph);
        assertFalse(result.hasCycle);
        assertTrue(result.order.isEmpty());
    }
}

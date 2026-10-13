
package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import daa.graph.Graph;

class GraphTest {

    @Test
    void createsUndirectedGraph() {
        Graph graph = new Graph(3, false);
        graph.addEdge(0, 1, 10);

        assertEquals(3, graph.getVertices());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getNeighbors(0).size());
        assertEquals(1, graph.getNeighbors(1).size());
        assertEquals(10, graph.getNeighbors(1).get(0).weight);
    }

    @Test
    void createsDirectedGraph() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);

        assertEquals(1, graph.getNeighbors(0).size());
        assertEquals(0, graph.getNeighbors(1).size());
        assertTrue(graph.isDirected());
    }

    @Test
    void supportsSelfLoopsAndParallelEdges() {
        Graph graph = new Graph(2, false);
        graph.addEdge(0, 0, 3);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 1, 7);

        assertEquals(3, graph.getEdgeCount());
        assertEquals(4, graph.getNeighbors(0).size());
    }

    @Test
    void rejectsInvalidVertices() {
        Graph graph = new Graph(3, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> graph.addEdge(0, 3, 5)
        );
    }
}

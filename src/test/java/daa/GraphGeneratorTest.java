
package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import daa.generator.GraphGenerator;
import daa.graph.Graph;

class GraphGeneratorTest {

    @Test
    void generatesConnectedGraphWithExpectedEdgeCount() {
        int vertices = 10;
        int extraEdges = 5;

        Graph graph = GraphGenerator.connectedUndirected(
                vertices, extraEdges, 42L
        );

        assertEquals(vertices, graph.getVertices());
        assertFalse(graph.isDirected());
        assertEquals(vertices - 1 + extraEdges, graph.getEdges().size());
    }

    @Test
    void sameSeedProducesSameEdges() {
        Graph first = GraphGenerator.connectedUndirected(10, 5, 42L);
        Graph second = GraphGenerator.connectedUndirected(10, 5, 42L);

        assertEquals(first.getEdges().size(), second.getEdges().size());

        for (int i = 0; i < first.getEdges().size(); i++) {
            Graph.Edge a = first.getEdges().get(i);
            Graph.Edge b = second.getEdges().get(i);

            assertEquals(a.from, b.from);
            assertEquals(a.to, b.to);
            assertEquals(a.weight, b.weight);
        }
    }

    @Test
    void rejectsImpossibleEdgeCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> GraphGenerator.connectedUndirected(3, 3, 42L)
        );
    }
    
    @Test
    void generatesDirectedGraphWithExpectedEdgeCount() {
        Graph graph = GraphGenerator.directed(10, 20, 42L);

        assertEquals(10, graph.getVertices());
        assertTrue(graph.isDirected());
        assertEquals(20, graph.getEdges().size());
    }

    @Test
    void generatesDagWithoutBackwardEdges() {
        Graph graph = GraphGenerator.dag(10, 20, 42L);

        assertTrue(graph.isDirected());
        assertEquals(20, graph.getEdges().size());

        for (Graph.Edge edge : graph.getEdges()) {
            assertTrue(edge.from < edge.to);
        }
    }

}

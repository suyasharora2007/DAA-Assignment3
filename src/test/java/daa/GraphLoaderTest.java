
package daa;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import daa.graph.Graph;
import daa.graph.GraphLoader;

class GraphLoaderTest {

    @Test
    void loadsConnectedUndirectedGraph() throws Exception {
        Graph graph = GraphLoader.load(
                Path.of("data", "connected.txt")
        );

        assertEquals(4, graph.getVertices());
        assertFalse(graph.isDirected());
        assertEquals(5, graph.getEdges().size());
    }

    @Test
    void loadsDisconnectedGraph() throws Exception {
        Graph graph = GraphLoader.load(
                Path.of("data", "disconnected.txt")
        );

        assertEquals(4, graph.getVertices());
        assertFalse(graph.isDirected());
        assertEquals(2, graph.getEdges().size());
    }

    @Test
    void loadsDirectedGraph() throws Exception {
        Graph graph = GraphLoader.load(
                Path.of("data", "cyclic.txt")
        );

        assertEquals(3, graph.getVertices());
        assertTrue(graph.isDirected());
        assertEquals(3, graph.getEdges().size());
    }
}


package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import daa.algorithms.DagShortestPath;
import daa.graph.Graph;

class DagShortestPathTest {

    @Test
    void calculatesShortestPathsWithNegativeWeights() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 2);
        graph.addEdge(2, 1, -5);
        graph.addEdge(1, 3, 3);
        graph.addEdge(2, 3, 6);

        DagShortestPath.Result result =
                DagShortestPath.run(graph);

        assertEquals(0, result.distances[0]);
        assertEquals(-3, result.distances[1]);
        assertEquals(2, result.distances[2]);
        assertEquals(0, result.distances[3]);
        assertEquals(0, result.distances[4]);
    }

    @Test
    void restoresPath() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(0, 2, 10);
        graph.addEdge(2, 3, 1);

        DagShortestPath.Result result =
                DagShortestPath.run(graph);

        assertEquals(6, result.distances[3]);
        assertEquals(
                java.util.List.of(0, 1, 2, 3),
                result.getPathTo(3)
        );
    }

    @Test
    void rejectsCyclicGraph() {
        Graph graph = new Graph(2, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> DagShortestPath.run(graph)
        );
    }
}

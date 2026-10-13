
package daa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import daa.algorithms.Kruskal;
import daa.algorithms.Prim;
import daa.graph.Graph;

class MSTTest {

    @Test
    void kruskalFindsMinimumSpanningTree() {
        Graph graph = new Graph(4, false);

        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 2, 6);
        graph.addEdge(0, 3, 5);
        graph.addEdge(1, 3, 15);
        graph.addEdge(2, 3, 4);

        Kruskal.Result result = Kruskal.run(graph);

        assertEquals(19, result.totalWeight);
        assertEquals(3, result.edges.size());
        assertEquals(1, result.components);
    }

    @Test
    void handlesDisconnectedGraph() {
        Graph graph = new Graph(4, false);

        graph.addEdge(0, 1, 2);
        graph.addEdge(2, 3, 3);

        Kruskal.Result result = Kruskal.run(graph);

        assertEquals(5, result.totalWeight);
        assertEquals(2, result.edges.size());
        assertEquals(2, result.components);
    }

    @Test
    void rejectsDirectedGraph() {
        Graph graph = new Graph(2, true);
        graph.addEdge(0, 1, 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> Kruskal.run(graph)
        );
    }
    @Test
void primMatchesKruskal() {
    Graph graph = new Graph(4, false);

    graph.addEdge(0, 1, 10);
    graph.addEdge(0, 2, 6);
    graph.addEdge(0, 3, 5);
    graph.addEdge(1, 3, 15);
    graph.addEdge(2, 3, 4);

    Kruskal.Result kruskal = Kruskal.run(graph);
    Prim.Result prim = Prim.run(graph);

    assertEquals(kruskal.totalWeight, prim.totalWeight);
    assertEquals(kruskal.edges.size(), prim.edges.size());
    assertEquals(kruskal.components, prim.components);
}

@Test
void primHandlesDisconnectedGraph() {
    Graph graph = new Graph(4, false);

    graph.addEdge(0, 1, 2);
    graph.addEdge(2, 3, 3);

    Prim.Result result = Prim.run(graph);

    assertEquals(5, result.totalWeight);
    assertEquals(2, result.components);
    assertEquals(2, result.edges.size());
}
}

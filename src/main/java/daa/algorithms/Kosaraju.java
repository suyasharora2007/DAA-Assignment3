
package daa.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import daa.graph.Graph;

public class Kosaraju {

    public static class Result {
        public final List<List<Integer>> components;
        public final long edgeScans;
        public final int maxDepth;

        public Result(
                List<List<Integer>> components,
                long edgeScans,
                int maxDepth) {
            this.components = components;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }
    }

    private static class Frame {
        final int vertex;
        int nextNeighbor;

        Frame(int vertex) {
            this.vertex = vertex;
        }
    }

    public static Result run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                "Kosaraju requires a directed graph"
            );
        }

        int n = graph.getVertices();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);

        long edgeScans = 0;
        int maxDepth = 0;

        // First DFS pass: compute finishing order iteratively.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            Deque<Frame> stack = new ArrayDeque<>();
            visited[start] = true;
            stack.push(new Frame(start));
            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                List<Graph.Edge> neighbors =
                    graph.getNeighbors(frame.vertex);

                if (frame.nextNeighbor < neighbors.size()) {
                    Graph.Edge edge =
                        neighbors.get(frame.nextNeighbor++);

                    edgeScans++;
                    int next = edge.to;

                    if (!visited[next]) {
                        visited[next] = true;
                        stack.push(new Frame(next));
                        maxDepth = Math.max(maxDepth, stack.size());
                    }
                } else {
                    finishOrder.add(frame.vertex);
                    stack.pop();
                }
            }
        }

        // Build the transposed graph.
        Graph reversed = new Graph(n, true);

        for (Graph.Edge edge : graph.getEdges()) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
        }

        // Second DFS pass: collect strongly connected components.

        visited = new boolean[n];
        List<List<Integer>> components = new ArrayList<>();

        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int start = finishOrder.get(i);

            if (visited[start]) {
                continue;
            }

            List<Integer> component = new ArrayList<>();
            Deque<Integer> stack = new ArrayDeque<>();

            stack.push(start);
            visited[start] = true;
            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {
                int current = stack.pop();
                component.add(current);

                for (Graph.Edge edge : reversed.getNeighbors(current)) {
                    edgeScans++;

                    if (!visited[edge.to]) {
                        visited[edge.to] = true;
                        stack.push(edge.to);
                        maxDepth = Math.max(maxDepth, stack.size());
                    }
                }
            }

            components.add(component);
        }

        return new Result(components, edgeScans, maxDepth);
    }
}

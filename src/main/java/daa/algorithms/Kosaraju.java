
package daa.algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import daa.graph.Graph;

public class Kosaraju {

    public static class Result {
        public final List<List<Integer>> components;
        public final long edgeScans;
        public final int maxDepth;

        public Result(List<List<Integer>> components,
                      long edgeScans, int maxDepth) {
            this.components = components;
            this.edgeScans = edgeScans;
            this.maxDepth = maxDepth;
        }

        public int count() {
            return components.size();
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
        int n = graph.getVertices();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>();
        long edgeScans = 0;
        int maxDepth = 0;

        // First pass: iterative DFS and finishing order.
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            Deque<Frame> stack = new ArrayDeque<>();
            stack.push(new Frame(start));
            visited[start] = true;
            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {
                Frame frame = stack.peek();
                List<Graph.Edge> neighbors =
                        graph.getNeighbors(frame.vertex);

                if (frame.nextNeighbor < neighbors.size()) {
                    Graph.Edge edge =
                            neighbors.get(frame.nextNeighbor++);
                    edgeScans++;

                    if (!visited[edge.to]) {
                        visited[edge.to] = true;
                        stack.push(new Frame(edge.to));
                        maxDepth = Math.max(maxDepth, stack.size());
                    }
                } else {
                    finishOrder.add(frame.vertex);
                    stack.pop();
                }
            }
        }

        // Build the reversed graph.
        Graph reversed = new Graph(n, true);

        for (Graph.Edge edge : graph.getEdges()) {
            reversed.addEdge(edge.to, edge.from, edge.weight);
        }

        // Second pass: explore in reverse finishing order.
        Collections.reverse(finishOrder);
        visited = new boolean[n];

        List<List<Integer>> components = new ArrayList<>();

        for (int start : finishOrder) {
            if (visited[start]) {
                continue;
            }

            List<Integer> component = new ArrayList<>();
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            visited[start] = true;
            maxDepth = Math.max(maxDepth, stack.size());

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                component.add(vertex);

                for (Graph.Edge edge : reversed.getNeighbors(vertex)) {
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

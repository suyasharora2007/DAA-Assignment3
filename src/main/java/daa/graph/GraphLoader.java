
package daa.graph;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GraphLoader {

    public static Graph load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String header = reader.readLine();

            if (header == null) {
                throw new IOException("Graph file is empty");
            }

            String[] parts = header.trim().split("\\s+");

            if (parts.length != 3) {
                throw new IOException(
                        "Header must be: n m directed|undirected"
                );
            }

            int vertices = Integer.parseInt(parts[0]);
            int edgeCount = Integer.parseInt(parts[1]);

            boolean directed;
            if (parts[2].equalsIgnoreCase("directed")) {
                directed = true;
            } else if (parts[2].equalsIgnoreCase("undirected")) {
                directed = false;
            } else {
                throw new IOException("Invalid graph type");
            }

            if (vertices < 0 || edgeCount < 0) {
                throw new IOException("Negative graph size");
            }

            Graph graph = new Graph(vertices, directed);

            for (int i = 0; i < edgeCount; i++) {
                String line = reader.readLine();

                if (line == null) {
                    throw new IOException("Missing edge at line " + (i + 2));
                }

                String[] edge = line.trim().split("\\s+");

                if (edge.length != 3) {
                    throw new IOException("Invalid edge at line " + (i + 2));
                }

                int from = Integer.parseInt(edge[0]);
                int to = Integer.parseInt(edge[1]);
                int weight = Integer.parseInt(edge[2]);

                graph.addEdge(from, to, weight);
            }

            return graph;
        }  catch (IllegalArgumentException e)  {
            throw new IOException("Invalid graph data: " + e.getMessage(), e);
        }
    }
}

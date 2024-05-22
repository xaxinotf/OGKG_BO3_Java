package com.example.ogkg_bo3;

import java.util.ArrayList;
import java.util.List;

public class MinimizeTriangles {
    public static List<Point> buildPolygonFromTriangles(List<Triangle> triangles) {
        List<Edge> boundaryEdges = new ArrayList<>();

        for (Triangle triangle : triangles) {
            for (Edge edge : triangle.edges()) {
                if (!isEdgeShared(edge, triangles)) {
                    boundaryEdges.add(edge);
                }
            }
        }

        if (boundaryEdges.isEmpty()) {
            throw new IllegalStateException("No boundary edges found");
        }

        List<Point> polygon = new ArrayList<>();
        Edge startEdge = boundaryEdges.get(0);
        boundaryEdges.remove(0);
        polygon.add(startEdge.p1);
        polygon.add(startEdge.p2);

        while (!boundaryEdges.isEmpty()) {
            Edge nextEdge = null;
            for (Edge edge : boundaryEdges) {
                if (edge.p1.equals(polygon.get(polygon.size() - 1))) {
                    nextEdge = edge;
                    polygon.add(edge.p2);
                    break;
                } else if (edge.p2.equals(polygon.get(polygon.size() - 1))) {
                    nextEdge = edge;
                    polygon.add(edge.p1);
                    break;
                }
            }

            if (nextEdge != null) {
                boundaryEdges.remove(nextEdge);
            } else {
                throw new IllegalStateException("Cannot find next boundary edge");
            }
        }

        return polygon;
    }

    private static boolean isEdgeShared(Edge edge, List<Triangle> triangles) {
        int count = 0;
        for (Triangle triangle : triangles) {
            if (triangle.hasEdge(edge)) {
                count++;
            }
        }
        System.out.println("Edge " + edge + " shared by " + count + " triangles.");
        return count > 1;
    }
}

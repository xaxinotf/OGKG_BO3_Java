package com.example.ogkg_bo3;

import java.util.ArrayList;
import java.util.List;

public class DelaunayTriangulation {
    public static List<Triangle> triangulate(List<Point> points) {
        List<Triangle> triangles = new ArrayList<>();

        // Step 1: Create a super triangle
        Point p1 = new Point(-10000, -10000);
        Point p2 = new Point(10000, -10000);
        Point p3 = new Point(0, 10000);
        Triangle superTriangle = new Triangle(p1, p2, p3);
        triangles.add(superTriangle);
        System.out.println("Super triangle created: " + p1 + ", " + p2 + ", " + p3);

        // Step 2: Add points one by one
        for (Point point : points) {
            List<Triangle> badTriangles = new ArrayList<>();
            List<Edge> polygon = new ArrayList<>();

            for (Triangle triangle : triangles) {
                if (isPointInCircumcircle(point, triangle)) {
                    badTriangles.add(triangle);
                }
            }

            for (Triangle triangle : badTriangles) {
                for (Edge edge : triangle.edges()) {
                    if (!isEdgeShared(edge, badTriangles)) {
                        polygon.add(edge);
                    }
                }
            }

            triangles.removeAll(badTriangles);
            System.out.println("Bad triangles removed. Remaining triangles: " + triangles.size());

            for (Edge edge : polygon) {
                triangles.add(new Triangle(edge.p1, edge.p2, point));
            }
            System.out.println("Triangles after adding new ones: " + triangles.size());
        }

        triangles.removeIf(triangle -> triangle.sharesVertex(superTriangle));
        System.out.println("Final number of triangles: " + triangles.size());

        return triangles;
    }

    private static boolean isPointInCircumcircle(Point point, Triangle triangle) {
        double ax = triangle.p1.x - point.x;
        double ay = triangle.p1.y - point.y;
        double bx = triangle.p2.x - point.x;
        double by = triangle.p2.y - point.y;
        double cx = triangle.p3.x - point.x;
        double cy = triangle.p3.y - point.y;

        double det_ab = ax * by - ay * bx;
        double det_bc = bx * cy - by * cx;
        double det_ca = cx * ay - cy * ax;

        double a_squared = ax * ax + ay * ay;
        double b_squared = bx * bx + by * by;
        double c_squared = cx * cx + cy * cy;

        double det = a_squared * det_bc + b_squared * det_ca + c_squared * det_ab;
        System.out.println("Determinant for point " + point + " in triangle " + triangle + ": " + det + " (det_ab: " + det_ab + ", det_bc: " + det_bc + ", det_ca: " + det_ca + ")");
        return det > 0;
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

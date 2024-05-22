package com.example.ogkg_bo3;

import java.util.Arrays;
import java.util.List;

public class Triangle {
    public Point p1;
    public Point p2;
    public Point p3;

    public Triangle(Point p1, Point p2, Point p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public List<Edge> edges() {
        return Arrays.asList(new Edge(p1, p2), new Edge(p2, p3), new Edge(p3, p1));
    }

    public boolean hasEdge(Edge edge) {
        return edges().contains(edge);
    }

    public boolean sharesVertex(Triangle triangle) {
        return p1.equals(triangle.p1) || p1.equals(triangle.p2) || p1.equals(triangle.p3) ||
                p2.equals(triangle.p1) || p2.equals(triangle.p2) || p2.equals(triangle.p3) ||
                p3.equals(triangle.p1) || p3.equals(triangle.p2) || p3.equals(triangle.p3);
    }

    @Override
    public String toString() {
        return "Triangle(" + p1 + ", " + p2 + ", " + p3 + ")";
    }
}

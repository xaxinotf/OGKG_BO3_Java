package com.example.ogkg_bo3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Scale;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SmallestEnclosingPolygon extends Application {

    private List<Point> points = new ArrayList<>();
    private Canvas canvas = new Canvas(1600, 1200); // Розширена область
    private Scale scaleTransform = new Scale();
    private static final int MAX_POINTS = 12000;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        HBox controls = new HBox();
        Button manualInputButton = new Button("Manual Input");
        Label label = new Label("Number of Points:");
        TextField pointCountField = new TextField("100");
        Button generatePointsButton = new Button("Generate Points");
        Button findPolygonButton = new Button("Find Polygon");

        manualInputButton.setOnAction(e -> enableManualInput(gc));
        generatePointsButton.setOnAction(e -> generatePoints(gc, pointCountField));
        findPolygonButton.setOnAction(e -> findAndDrawPolygon(gc));

        controls.getChildren().addAll(manualInputButton, label, pointCountField, generatePointsButton, findPolygonButton);
        root.setTop(controls);

        StackPane canvasContainer = new StackPane();
        canvasContainer.getChildren().add(canvas);
        canvas.getTransforms().add(scaleTransform);

        ScrollPane scrollPane = new ScrollPane(canvasContainer);
        scrollPane.setPannable(true);
        root.setCenter(scrollPane);

        canvas.setOnScroll(this::handleScroll);

        primaryStage.setTitle("Smallest Enclosing Simple Polygon");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void enableManualInput(GraphicsContext gc) {
        canvas.setOnMouseClicked(e -> {
            points.add(new Point(e.getX(), e.getY()));
            drawPoints(gc);
        });
    }

    private void generatePoints(GraphicsContext gc, TextField pointCountField) {
        int numPoints;
        try {
            numPoints = Integer.parseInt(pointCountField.getText());
            if (numPoints > MAX_POINTS) {
                numPoints = MAX_POINTS;
            } else if (numPoints < 1) {
                numPoints = 1;
            }
        } catch (NumberFormatException e) {
            numPoints = 100; // Default value if input is invalid
        }

        Random rand = new Random();
        points.clear();
        for (int i = 0; i < numPoints; i++) {
            points.add(new Point(rand.nextDouble() * canvas.getWidth(), rand.nextDouble() * canvas.getHeight()));
        }
        drawPoints(gc);
    }

    private void drawPoints(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        for (Point point : points) {
            gc.fillOval(point.x - 2, point.y - 2, 4, 4);
        }
    }

    private void findAndDrawPolygon(GraphicsContext gc) {
        // Step 1: Find the smallest enclosing simple polygon
        List<Point> polygon = findSmallestEnclosingPolygon(points);

        // Step 2: Draw the polygon
        drawPolygon(gc, polygon);
    }

    private List<Point> findSmallestEnclosingPolygon(List<Point> points) {
        List<Point> smallestPolygon = null;
        double smallestArea = Double.MAX_VALUE;

        List<List<Point>> permutations = generatePermutations(points);
        for (List<Point> permutation : permutations) {
            if (isSimplePolygon(permutation)) {
                double area = polygonArea(permutation);
                if (area < smallestArea) {
                    smallestArea = area;
                    smallestPolygon = new ArrayList<>(permutation);
                }
            }
        }

        return smallestPolygon != null ? smallestPolygon : new ArrayList<>(points);
    }

    private List<List<Point>> generatePermutations(List<Point> points) {
        List<List<Point>> result = new ArrayList<>();
        generatePermutationsHelper(points, 0, result);
        return result;
    }

    private void generatePermutationsHelper(List<Point> points, int index, List<List<Point>> result) {
        if (index == points.size() - 1) {
            result.add(new ArrayList<>(points));
            return;
        }

        for (int i = index; i < points.size(); i++) {
            Collections.swap(points, i, index);
            generatePermutationsHelper(points, index + 1, result);
            Collections.swap(points, i, index);
        }
    }

    private boolean isSimplePolygon(List<Point> polygon) {
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                if (i == 0 && j == n - 1) {
                    continue; // Ignore adjacent edges
                }
                if (segmentsIntersect(
                        polygon.get(i), polygon.get((i + 1) % n),
                        polygon.get(j), polygon.get((j + 1) % n))) {
                    return false;
                }
            }
        }
        return true;
    }

    private double polygonArea(List<Point> polygon) {
        double area = 0.0;
        int n = polygon.size();
        for (int i = 0; i < n; i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % n);
            area += p1.x * p2.y - p2.x * p1.y;
        }
        return Math.abs(area) / 2.0;
    }

    private boolean segmentsIntersect(Point p1, Point p2, Point p3, Point p4) {
        double d1 = direction(p3, p4, p1);
        double d2 = direction(p3, p4, p2);
        double d3 = direction(p1, p2, p3);
        double d4 = direction(p1, p2, p4);

        if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) &&
                ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
            return true;
        }

        return (d1 == 0 && onSegment(p3, p4, p1)) ||
                (d2 == 0 && onSegment(p3, p4, p2)) ||
                (d3 == 0 && onSegment(p1, p2, p3)) ||
                (d4 == 0 && onSegment(p1, p2, p4));
    }

    private double direction(Point p1, Point p2, Point p3) {
        return (p3.x - p1.x) * (p2.y - p1.y) - (p2.x - p1.x) * (p3.y - p1.y);
    }

    private boolean onSegment(Point p1, Point p2, Point p) {
        return Math.min(p1.x, p2.x) <= p.x && p.x <= Math.max(p1.x, p2.x) &&
                Math.min(p1.y, p2.y) <= p.y && p.y <= Math.max(p1.y, p2.y);
    }

    private void drawPolygon(GraphicsContext gc, List<Point> polygon) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        for (int i = 0; i < polygon.size(); i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % polygon.size());
            gc.strokeLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    private void handleScroll(ScrollEvent event) {
        double scaleFactor = (event.getDeltaY() > 0) ? 1.1 : 0.9;
        scaleTransform.setX(scaleTransform.getX() * scaleFactor);
        scaleTransform.setY(scaleTransform.getY() * scaleFactor);
    }
}

package com.code.foldervisualizer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class BarChartVisualizationStrategy implements VisualizationStrategy {

    private final Pane pane;
    private static final double ROW_HEIGHT = 25;
    private static final double MAX_BAR_WIDTH = 600;

    public BarChartVisualizationStrategy(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void visualize(FSItem root) {
        pane.getChildren().clear();

        long max = findMaxSize(root);
        if (max <= 0) max = 1; // avoid divide by zero

        double totalHeight = drawBars(root, 0, 10, max);

        pane.setPrefHeight(totalHeight + ROW_HEIGHT);
        pane.setPrefWidth(MAX_BAR_WIDTH + 80);
    }

    private long findMaxSize(FSItem item) {
        long m = item.getSize();
        if (item instanceof FolderItem folder) {
            for (FSItem child : folder.getChildren()) {
                long childMax = findMaxSize(child);
                if (childMax > m) {
                    m = childMax;
                }
            }
        }
        return m;
    }
    private double drawBars(FSItem item, int depth, double y, long max) {
        double indent = depth * 20;
        double width = (double) item.getSize() / max * MAX_BAR_WIDTH;

// color based on type (folder vs file)
        Color fillColor;

        if (item instanceof FolderItem) {
            fillColor = Color.web("#8FAADC");   // Folder color (blue)
        } else {
            fillColor = Color.web("#F4B084");   // File color (orange)
        }

        Rectangle bar = new Rectangle(indent, y, width, ROW_HEIGHT - 5);
        bar.setFill(fillColor);
        bar.setStroke(Color.BLACK);


        String label = FSItem.formatSize(item.getSize()) + " " + item.getName();
        Text text = new Text(indent + 5, y + ROW_HEIGHT - 8, label);

        pane.getChildren().addAll(bar, text);

        double currentY = y + ROW_HEIGHT;

        if (item instanceof FolderItem folder) {
            // copy children and sort them by size (largest first)
            java.util.List<FSItem> kids = new java.util.ArrayList<>(folder.getChildren());
            kids.sort(java.util.Comparator.comparingLong(FSItem::getSize).reversed());

            for (FSItem child : kids) {
                currentY = drawBars(child, depth + 1, currentY, max);
            }
        }
        return currentY;
    }

}

package com.code.foldervisualizer;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TreeVisualizationStrategy implements VisualizationStrategy {

    private final Pane pane;

    // layout constants
    private static final double INDENT_X = 80;   // horizontal step per level
    private static final double ROW_H    = 35;   // vertical distance between items
    private static final double BOX_W    = 140;
    private static final double BOX_H    = 22;

    public TreeVisualizationStrategy(Pane pane) {
        this.pane = pane;
    }

    @Override
    public void visualize(FSItem root) {
        pane.getChildren().clear();
        double totalH = drawItem(root, 0, 20, false, 0, 0);
        pane.setPrefHeight(totalH + 40);
        pane.setPrefWidth(800);
    }

    private double drawItem(FSItem item,
                            int depth,
                            double y,
                            boolean drawLink,
                            double parentX,
                            double parentY) {

        double x = 40 + depth * INDENT_X;
        double centerY = y + ROW_H / 2.0;

        // draw orthogonal link from parent to this node
        if (drawLink) {
            Line vert = new Line(parentX, parentY, parentX, centerY);
            vert.setStroke(Color.LIGHTGRAY);

            Line horiz = new Line(parentX, centerY, x, centerY);
            horiz.setStroke(Color.LIGHTGRAY);

            pane.getChildren().addAll(vert, horiz);
        }

        // folders = grey box, files = blue box
        Color fillColor = (item instanceof FolderItem)
                ? Color.LIGHTGRAY
                : Color.LIGHTBLUE;

        Rectangle box = new Rectangle(x, centerY - BOX_H / 2.0, BOX_W, BOX_H);
        box.setFill(fillColor);
        box.setStroke(Color.DARKGRAY);

        String labelText = item.getName(); // tree = name only
        Text label = new Text(labelText);

        // text is always black; folders bold, files normal
        label.setFill(Color.BLACK);
        if (item instanceof FolderItem) {
            label.setFont(Font.font("System", FontWeight.BOLD, 11));
        } else {
            label.setFont(Font.font("System", FontWeight.NORMAL, 11));
        }

        label.setX(x + 5);
        label.setY(centerY + 4);

        pane.getChildren().addAll(box, label);

        double currentY = y + ROW_H;

        if (item instanceof FolderItem folder) {
            double boxRightCenterX = x + BOX_W; // where links start on the right side
            for (FSItem child : folder.getChildren()) {
                currentY = drawItem(
                        child,
                        depth + 1,
                        currentY,
                        true,
                        boxRightCenterX,
                        centerY
                );
            }
        }

        return currentY;
    }
}

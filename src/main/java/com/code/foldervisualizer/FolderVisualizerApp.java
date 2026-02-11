package com.code.foldervisualizer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.ToggleButton;

public class FolderVisualizerApp extends Application {



    private FolderItem rootFolder;
    private Pane drawPane;
    private ScrollPane scrollPane;
    private TreeView<String> folderTree;
    private Button browseBtn;
    private ToggleGroup modeGroup;
    private RadioButton treeBtn;
    private RadioButton barBtn;

    @Override
    public void start(Stage stage) {
        // ----- (D) Visualization Type: Tree / Bar Chart -----
        treeBtn = new RadioButton("Tree");
        barBtn  = new RadioButton("Bar Chart");

        modeGroup = new ToggleGroup();
        treeBtn.setToggleGroup(modeGroup);
        barBtn.setToggleGroup(modeGroup);
        treeBtn.setSelected(true);

        // make them look like “real” buttons with color
        updateToggleStyles();

        HBox topBar = new HBox(10, treeBtn, barBtn);
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 8, 8, 20));
        topBar.setStyle("-fx-background-color: #eeeeee;");

        // ----- (A) Folders Area + (C) Browse Button -----
        folderTree = new TreeView<>();
        folderTree.setShowRoot(true);

        browseBtn = new Button("Browse");
        browseBtn.setMaxWidth(Double.MAX_VALUE);

        BorderPane leftPane = new BorderPane();
        leftPane.setCenter(folderTree);
        leftPane.setBottom(browseBtn);
        BorderPane.setMargin(browseBtn, new Insets(5));
        leftPane.setPrefWidth(250);

        // ----- (B) Visualization Area -----
        drawPane = new Pane();
        scrollPane = new ScrollPane(drawPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // center pane = top buttons + visualization
        BorderPane centerPane = new BorderPane();
        centerPane.setTop(topBar);
        centerPane.setCenter(scrollPane);

        // ----- Root layout -----
        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(centerPane);

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Folder Size Visualizer");
        stage.show();

        // default visualization strategy = Tree view
        FSItem.setVisualizer(new TreeVisualizationStrategy(drawPane));

        // events
        browseBtn.setOnAction(e -> onBrowse(stage));
        modeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            onModeChanged();
            updateToggleStyles();
        });
    }





    private void onBrowse(Stage stage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a folder");
        File chosen = chooser.showDialog(stage);
        if (chosen == null) return;

        Path selectedPath = chosen.toPath();
        rootFolder = buildFolder(selectedPath);

        // ONE call: compute sizes for entire structure
        rootFolder.calculateSize();

        // build the left TreeView from the same structure
        TreeItem<String> rootItem = buildTreeItem(rootFolder);
        rootItem.setExpanded(true);
        folderTree.setRoot(rootItem);

        // ONE call: visualize entire structure
        drawPane.getChildren().clear();
        rootFolder.visualize();
    }
    private TreeItem<String> buildTreeItem(FolderItem folder) {
        TreeItem<String> item = new TreeItem<>(folder.getName());
        for (FSItem child : folder.getChildren()) {
            if (child instanceof FolderItem f) {
                item.getChildren().add(buildTreeItem(f));
            } else {
                item.getChildren().add(new TreeItem<>(child.getName()));
            }
        }
        return item;
    }

    private FolderItem buildFolder(Path folderPath) {
        FolderItem folder = new FolderItem(folderPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) {
                    folder.addChild(buildFolder(p));
                } else {
                    folder.addChild(new FileItem(p));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folder;
    }

    private void onModeChanged() {
        if (rootFolder == null) return;

        Toggle selected = modeGroup.getSelectedToggle();
        if (selected == treeBtn) {
            FSItem.setVisualizer(new TreeVisualizationStrategy(drawPane));
        } else {
            FSItem.setVisualizer(new BarChartVisualizationStrategy(drawPane));
        }

        drawPane.getChildren().clear();
        rootFolder.visualize();
    }

    // style for the Tree / Bar Chart toggle buttons
    private static final String TOGGLE_BASE_STYLE =
            "-fx-background-color: #e0e0e0; " +
                    "-fx-text-fill: black; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 5 18 5 18;";

    private static final String TOGGLE_SELECTED_STYLE =
            "-fx-background-color: #4a90e2; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 13px; " +
                    "-fx-padding: 5 18 5 18;";

    private void updateToggleStyles() {
        if (treeBtn != null) {
            treeBtn.setStyle(treeBtn.isSelected() ? TOGGLE_SELECTED_STYLE : TOGGLE_BASE_STYLE);
        }
        if (barBtn != null) {
            barBtn.setStyle(barBtn.isSelected() ? TOGGLE_SELECTED_STYLE : TOGGLE_BASE_STYLE);
        }
    }


    public static void main(String[] args) {
        launch();
    }
}

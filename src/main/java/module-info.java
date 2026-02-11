module com.code.foldervisualizer {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.code.foldervisualizer to javafx.fxml;
    exports com.code.foldervisualizer;
}

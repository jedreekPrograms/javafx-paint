package pl.paint;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public final class DocumentActions {
    private final Stage owner;
    private final DrawingCanvas canvas;
    private final ToolController tools;
    private final DrawingStorage storage = new DrawingStorage();

    public DocumentActions(Stage owner, DrawingCanvas canvas, ToolController tools) {
        this.owner = owner;
        this.canvas = canvas;
        this.tools = tools;
    }

    public void save() {
        File file = chooser().showSaveDialog(owner);
        if (file == null) return;
        tools.select();
        try {
            storage.save(file.toPath(), canvas.getShapes());
        } catch (IOException | IllegalArgumentException exception) {
            error("Nie można zapisać rysunku.", exception);
        }
    }

    private FileChooser chooser() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Rysunek JSON", "*.json"));
        return chooser;
    }

    private void error(String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
        alert.initOwner(owner);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}

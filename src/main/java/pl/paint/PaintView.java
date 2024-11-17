package pl.paint;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public final class PaintView extends BorderPane {
    private final DrawingCanvas canvas = new DrawingCanvas();
    private final ToolController tools = new ToolController(canvas);

    public PaintView(Stage owner) {
        Menu file = new Menu("Pliki");
        Menu info = new Menu("Info");
        MenuItem about = new MenuItem("O programie");
        about.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "GeometryEditorFX — edytor figur w JavaFX.", ButtonType.OK);
            alert.initOwner(owner);
            alert.setHeaderText("GeometryEditorFX");
            alert.showAndWait();
        });
        info.getItems().add(about);
        ToolBar toolbar = new ToolBar();

        Button select = new Button("Wybierz");
        select.setOnAction(event -> tools.select());
        toolbar.getItems().add(select);
        Button ellipse = new Button("Okrąg");
ellipse.setOnAction(event -> tools.activate(new EllipseTool(canvas)));
toolbar.getItems().add(ellipse);
Button rectangle = new Button("Prostokąt");
rectangle.setOnAction(event -> tools.activate(new RectangleTool(canvas)));
toolbar.getItems().add(rectangle);

        setTop(new VBox(new MenuBar(file, info), toolbar));
        setCenter(canvas);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(canvas.widthProperty());
        clip.heightProperty().bind(canvas.heightProperty());
        canvas.setClip(clip);

    }

    public DrawingCanvas getCanvas() {
        return canvas;
    }
}

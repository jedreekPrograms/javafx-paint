package pl.paint.ui;



import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

public final class DrawingCanvas extends Pane {
    public DrawingCanvas() {
        setStyle("-fx-background-color: white;");
        setFocusTraversable(true);
        setPickOnBounds(true);
    }

    public List<Shape> getShapes() {
        return getChildren().stream().filter(Shape.class::isInstance).map(Shape.class::cast).toList();
    }

    public void addShape(Shape shape) {
        getChildren().add(shape);
    }

    public void removeShape(Shape shape) {
        getChildren().remove(shape);
    }

    public void replaceShapes(List<Shape> shapes) {
        getChildren().setAll(shapes);
    }
}

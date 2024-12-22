package pl.paint.shapes;



import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public final class EditableEllipse extends Ellipse {
    public EditableEllipse(double x, double y, double radiusX, double radiusY) {
        super(x, y, radiusX, radiusY);
        setFill(Color.RED);
        ShapeInteractions.install(this);
    }
}

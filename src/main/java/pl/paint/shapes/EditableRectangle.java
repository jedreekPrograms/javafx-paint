package pl.paint.shapes;



import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public final class EditableRectangle extends Rectangle {
    public EditableRectangle(double x, double y, double width, double height) {
        super(x, y, width, height);
        setFill(Color.RED);
        ShapeInteractions.install(this);
    }
}

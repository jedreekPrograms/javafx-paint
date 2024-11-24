package pl.paint;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public final class EditablePolygon extends Polygon {
    public EditablePolygon() {
        setFill(Color.BLUE);
        ShapeInteractions.install(this);
    }
}

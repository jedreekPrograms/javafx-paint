package pl.paint.persistence;

import pl.paint.shapes.EditableEllipse;
import pl.paint.shapes.EditableRectangle;
import pl.paint.shapes.EditablePolygon;
import pl.paint.shapes.ShapeType;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/** JSON representation; original field names are retained for existing drawings. */
public final class ShapeData {
    ShapeType type;
    double x, y, radiusX, radiusY, width, height, rotateAngle;
    double translateX, translateY;
    double scaleX = 1, scaleY = 1;
    List<Double> points = new ArrayList<>();
    String colorHex;

    public static ShapeData fromShape(Shape shape) {
        ShapeData data = new ShapeData();
        if (shape instanceof EditableRectangle rectangle) {
            data.type = ShapeType.RECTANGLE;
            data.x = rectangle.getX();
            data.y = rectangle.getY();
            data.width = rectangle.getWidth();
            data.height = rectangle.getHeight();
        } else if (shape instanceof EditableEllipse ellipse) {
            data.type = ShapeType.ELLIPSE;
            data.x = ellipse.getCenterX();
            data.y = ellipse.getCenterY();
            data.radiusX = ellipse.getRadiusX();
            data.radiusY = ellipse.getRadiusY();
        } else if (shape instanceof EditablePolygon polygon) {
            data.type = ShapeType.POLYGON;
            data.points = new ArrayList<>(polygon.getPoints());
        } else {
            throw new IllegalArgumentException("Unsupported shape class");
        }
        data.rotateAngle = shape.getRotate();
        data.translateX = shape.getTranslateX();
        data.translateY = shape.getTranslateY();
        data.scaleX = shape.getScaleX();
        data.scaleY = shape.getScaleY();
        if (shape.getFill() instanceof Color color) data.colorHex = color.toString();
        data.validate();
        return data;
    }

    private void validate() {
        if (type == null) throw new IllegalArgumentException("Missing or unknown shape type");
        for (double value : new double[]{x, y, radiusX, radiusY, width, height,
                rotateAngle, translateX, translateY, scaleX, scaleY}) {
            if (!Double.isFinite(value)) throw new IllegalArgumentException("Shape values must be finite");
        }
        if (scaleX <= 0 || scaleY <= 0) throw new IllegalArgumentException("Scale must be positive");
        switch (type) {
            case RECTANGLE -> {
                if (width <= 0 || height <= 0) throw new IllegalArgumentException("Rectangle size must be positive");
            }
            case ELLIPSE -> {
                if (radiusX <= 0 || radiusY <= 0) throw new IllegalArgumentException("Ellipse radii must be positive");
            }
            case POLYGON -> {
                if (points == null || points.size() < 6 || points.size() % 2 != 0)
                    throw new IllegalArgumentException("Polygon needs at least three coordinate pairs");
                for (Double point : points) {
                    if (point == null || !Double.isFinite(point))
                        throw new IllegalArgumentException("Polygon coordinates must be finite");
                }
            }
        }
        if (colorHex != null) Color.web(colorHex);
    }

    public Shape toShape() {
        validate();
        Shape shape = switch (type) {
            case RECTANGLE -> new EditableRectangle(x, y, width, height);
            case ELLIPSE -> new EditableEllipse(x, y, radiusX, radiusY);
            case POLYGON -> {
                EditablePolygon polygon = new EditablePolygon();
                polygon.getPoints().addAll(points);
                yield polygon;
            }
        };
        shape.setRotate(rotateAngle);
        shape.setTranslateX(translateX);
        shape.setTranslateY(translateY);
        shape.setScaleX(scaleX);
        shape.setScaleY(scaleY);
        if (colorHex != null) shape.setFill(Color.web(colorHex));
        return shape;
    }
}

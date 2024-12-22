package pl.paint.shapes;



import javafx.geometry.Bounds;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public final class ShapeTransforms {
    public static final double MIN_SIZE = 2;
    public static final double MAX_SIZE = 100_000;

    private ShapeTransforms() { }

    public static void move(Shape shape, double dx, double dy) {
        if (!Double.isFinite(dx) || !Double.isFinite(dy)) return;
        double x = shape.getTranslateX() + dx;
        double y = shape.getTranslateY() + dy;
        if (Double.isFinite(x) && Double.isFinite(y)) {
            shape.setTranslateX(x);
            shape.setTranslateY(y);
        }
    }

    public static void scale(Shape shape, double requestedFactor) {
        if (!Double.isFinite(requestedFactor) || requestedFactor <= 0) return;
        Bounds bounds = shape.getBoundsInLocal();
        double smaller = Math.min(bounds.getWidth(), bounds.getHeight());
        double larger = Math.max(bounds.getWidth(), bounds.getHeight());
        if (smaller <= 0 || !Double.isFinite(larger)) return;
        double minimumFactor = MIN_SIZE / smaller;
        double maximumFactor = MAX_SIZE / larger;
        if (minimumFactor > maximumFactor) return;
        double factor = Math.clamp(requestedFactor, minimumFactor, maximumFactor);
        if (shape instanceof Rectangle rectangle) {
            double cx = rectangle.getX() + rectangle.getWidth() / 2;
            double cy = rectangle.getY() + rectangle.getHeight() / 2;
            rectangle.setWidth(rectangle.getWidth() * factor);
            rectangle.setHeight(rectangle.getHeight() * factor);
            rectangle.setX(cx - rectangle.getWidth() / 2);
            rectangle.setY(cy - rectangle.getHeight() / 2);
        } else if (shape instanceof Ellipse ellipse) {
            ellipse.setRadiusX(ellipse.getRadiusX() * factor);
            ellipse.setRadiusY(ellipse.getRadiusY() * factor);
        } else if (shape instanceof Polygon polygon) {
            double cx = bounds.getMinX() + bounds.getWidth() / 2;
            double cy = bounds.getMinY() + bounds.getHeight() / 2;
            var points = polygon.getPoints();
            for (int i = 0; i < points.size(); i += 2) {
                points.set(i, cx + (points.get(i) - cx) * factor);
                points.set(i + 1, cy + (points.get(i + 1) - cy) * factor);
            }
        }
    }

    public static void rotate(Shape shape, double degrees) {
        if (Double.isFinite(degrees)) shape.setRotate((shape.getRotate() + degrees) % 360);
    }
}

package pl.paint;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public final class PolygonTool implements DrawingTool {
    private final DrawingCanvas canvas;
    private final EditablePolygon shape = new EditablePolygon();
    private boolean finished;

    public PolygonTool(DrawingCanvas canvas) { this.canvas = canvas; }

    @Override
    public void clicked(MouseEvent event) {
        Point2D point = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        var points = shape.getPoints();
        if (points.size() >= 6) {
            var first = new Point2D(points.get(0), points.get(1));
            if (point.distance(first) <= 10 || event.getClickCount() > 1) {
                finished = true;
                return;
            }
        }
        if (points.isEmpty()) canvas.addShape(shape);
        points.addAll(point.getX(), point.getY());
    }

    @Override
    public void cancel() {
        if (!finished) canvas.removeShape(shape);
        finished = true;
    }

    @Override
    public boolean isFinished() { return finished; }
}

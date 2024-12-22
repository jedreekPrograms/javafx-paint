package pl.paint.tools;

import pl.paint.ui.DrawingCanvas;
import pl.paint.shapes.EditableRectangle;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public final class RectangleTool implements DrawingTool {
    private final DrawingCanvas canvas;
    private EditableRectangle shape;
    private Point2D start;
    private boolean finished;

    public RectangleTool(DrawingCanvas canvas) { this.canvas = canvas; }

    @Override
    public void pressed(MouseEvent event) {
        start = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        shape = new EditableRectangle(start.getX(), start.getY(), 0, 0);
        canvas.addShape(shape);
    }

    @Override
    public void dragged(MouseEvent event) {
        if (shape == null) return;
        Point2D point = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        shape.setX(Math.min(start.getX(), point.getX()));
        shape.setY(Math.min(start.getY(), point.getY()));
        shape.setWidth(Math.abs(point.getX() - start.getX()));
        shape.setHeight(Math.abs(point.getY() - start.getY()));
    }

    @Override
    public void released(MouseEvent event) {
        if (shape != null && shape.getWidth() >= 2 && shape.getHeight() >= 2) finished = true;
        else cancel();
    }

    @Override
    public void cancel() {
        if (!finished && shape != null) canvas.removeShape(shape);
        shape = null;
        finished = true;
    }

    @Override
    public boolean isFinished() { return finished; }
}

package pl.paint.tools;

import pl.paint.ui.DrawingCanvas;
import pl.paint.shapes.EditableEllipse;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

public final class EllipseTool implements DrawingTool {
    private final DrawingCanvas canvas;
    private EditableEllipse shape;
    private Point2D center;
    private boolean finished;

    public EllipseTool(DrawingCanvas canvas) { this.canvas = canvas; }

    @Override
    public void pressed(MouseEvent event) {
        center = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        shape = new EditableEllipse(center.getX(), center.getY(), 0, 0);
        canvas.addShape(shape);
    }

    @Override
    public void dragged(MouseEvent event) {
        if (shape == null) return;
        double radius = center.distance(canvas.sceneToLocal(event.getSceneX(), event.getSceneY()));
        shape.setRadiusX(radius);
        shape.setRadiusY(radius);
    }

    @Override
    public void released(MouseEvent event) {
        if (shape != null && shape.getRadiusX() >= 2) finished = true;
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

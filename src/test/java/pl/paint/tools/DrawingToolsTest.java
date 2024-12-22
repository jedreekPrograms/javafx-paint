package pl.paint.tools;

import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import org.junit.jupiter.api.Test;
import pl.paint.ui.DrawingCanvas;
import pl.paint.shapes.*;
import static org.junit.jupiter.api.Assertions.*;

class DrawingToolsTest {
    private MouseEvent mouse(DrawingCanvas canvas, double x, double y) {
        return new MouseEvent(MouseEvent.MOUSE_CLICKED, x, y, x, y, MouseButton.PRIMARY,
                1, false, false, false, false, true, false, false, false, false, true,
                new PickResult(canvas, x, y));
    }

    @Test void circleUsesDistanceFromItsCenter() {
        var canvas = new DrawingCanvas();
        var tool = new EllipseTool(canvas);
        tool.pressed(mouse(canvas, 100, 100));
        tool.dragged(mouse(canvas, 130, 140));
        tool.released(mouse(canvas, 130, 140));
        var shape = (EditableEllipse) canvas.getShapes().getFirst();
        assertTrue(tool.isFinished());
        assertEquals(50, shape.getRadiusX());
        assertEquals(50, shape.getRadiusY());
        assertEquals(100, shape.getCenterX());
    }

    @Test void clickWithoutDraggingDoesNotLeaveAnEmptyCircle() {
        var canvas = new DrawingCanvas();
        var tool = new EllipseTool(canvas);
        tool.pressed(mouse(canvas, 100, 100));
        tool.released(mouse(canvas, 100, 100));
        assertTrue(canvas.getShapes().isEmpty());
    }

    @Test void rectangleCanBeDrawnBackwards() {
        var canvas = new DrawingCanvas();
        var tool = new RectangleTool(canvas);
        tool.pressed(mouse(canvas, 100, 100));
        tool.dragged(mouse(canvas, 20, 30));
        tool.released(mouse(canvas, 20, 30));
        var shape = (EditableRectangle) canvas.getShapes().getFirst();
        assertEquals(20, shape.getX());
        assertEquals(30, shape.getY());
        assertEquals(80, shape.getWidth());
        assertEquals(70, shape.getHeight());
    }

    @Test void zeroHeightRectangleIsDiscarded() {
        var canvas = new DrawingCanvas();
        var tool = new RectangleTool(canvas);
        tool.pressed(mouse(canvas, 10, 10));
        tool.dragged(mouse(canvas, 100, 10));
        tool.released(mouse(canvas, 100, 10));
        assertTrue(canvas.getShapes().isEmpty());
    }

    @Test void polygonClosesNearItsFirstVertexWithoutDuplicatingIt() {
        var canvas = new DrawingCanvas();
        var tool = new PolygonTool(canvas);
        tool.clicked(mouse(canvas, 10, 10));
        tool.clicked(mouse(canvas, 100, 10));
        tool.clicked(mouse(canvas, 100, 100));
        tool.clicked(mouse(canvas, 12, 12));
        assertTrue(tool.isFinished());
        assertEquals(6, ((EditablePolygon) canvas.getShapes().getFirst()).getPoints().size());
    }

    @Test void cancellingPolygonRemovesUnfinishedShape() {
        var canvas = new DrawingCanvas();
        var tool = new PolygonTool(canvas);
        tool.clicked(mouse(canvas, 10, 10));
        tool.cancel();
        assertTrue(canvas.getShapes().isEmpty());
    }

    @Test void changingToolCancelsPreviousPreviewAndSelectionCancelsNext() {
        var canvas = new DrawingCanvas();
        var controller = new ToolController(canvas);
        var rectangle = new RectangleTool(canvas);
        controller.activate(rectangle);
        rectangle.pressed(mouse(canvas, 10, 10));
        var ellipse = new EllipseTool(canvas);
        controller.activate(ellipse);
        assertTrue(canvas.getShapes().isEmpty());
        ellipse.pressed(mouse(canvas, 20, 20));
        controller.select();
        assertTrue(canvas.getShapes().isEmpty());
    }

    @Test void cancellingAfterCompletionKeepsFinishedShape() {
        var canvas = new DrawingCanvas();
        var tool = new RectangleTool(canvas);
        tool.pressed(mouse(canvas, 0, 0));
        tool.dragged(mouse(canvas, 50, 40));
        tool.released(mouse(canvas, 50, 40));
        tool.cancel();
        assertEquals(1, canvas.getShapes().size());
    }
}

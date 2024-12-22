package pl.paint.tools;

import pl.paint.ui.DrawingCanvas;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public final class ToolController {
    private final DrawingCanvas canvas;
    private DrawingTool active;

    public ToolController(DrawingCanvas canvas) {
        this.canvas = canvas;
        canvas.addEventFilter(MouseEvent.ANY, this::handle);
        canvas.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                select();
                event.consume();
            }
        });
    }

    public void activate(DrawingTool tool) {
        select();
        active = tool;
        canvas.requestFocus();
    }

    public void select() {
        if (active != null) active.cancel();
        active = null;
    }

    private void handle(MouseEvent event) {
        if (active == null) return;
        if (event.getButton() == MouseButton.SECONDARY) {
            select();
            event.consume();
            return;
        }
        if (event.getButton() != MouseButton.PRIMARY) return;
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) active.pressed(event);
        else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) active.dragged(event);
        else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) active.released(event);
        else if (event.getEventType() == MouseEvent.MOUSE_CLICKED) active.clicked(event);
        if (active.isFinished()) active = null;
        event.consume();
    }
}

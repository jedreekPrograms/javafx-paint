package pl.paint.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import pl.paint.shapes.EditableRectangle;
import static org.junit.jupiter.api.Assertions.*;

/** Exercises the actual JavaFX window under a desktop or Xvfb. */
@EnabledIfEnvironmentVariable(named = "DISPLAY", matches = ".+")
class DesktopSmokeTest {
    @BeforeAll static void startToolkit() throws Exception {
        CompletableFuture<Void> started = new CompletableFuture<>();
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            started.complete(null);
        });
        started.get(15, TimeUnit.SECONDS);
    }

    @AfterAll static void stopToolkit() { Platform.exit(); }

    private static <T> T onFx(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }

    private static Button button(PaintView view, String text) {
        return view.lookupAll(".button").stream().filter(Button.class::isInstance)
                .map(Button.class::cast).filter(value -> text.equals(value.getText()))
                .findFirst().orElseThrow();
    }

    private static void mouse(DrawingCanvas canvas, EventType<MouseEvent> type, double x, double y) {
        var point = canvas.localToScene(x, y);
        Event.fireEvent(canvas, new MouseEvent(type, point.getX(), point.getY(), x, y,
                MouseButton.PRIMARY, 1, false, false, false, false,
                type != MouseEvent.MOUSE_RELEASED, false, false, false, false, true,
                new PickResult(canvas, x, y)));
    }

    @Test void windowSupportsDrawingAndKeyboardRotation() throws Exception {
        onFx(() -> {
            Stage stage = new Stage();
            try {
                PaintView view = new PaintView(stage);
                stage.setScene(new Scene(view, 720, 540));
                stage.show();
                view.applyCss();
                view.layout();
                var canvas = view.getCanvas();
                assertTrue(canvas.getWidth() > 400);
                assertTrue(canvas.getHeight() > 300);
                button(view, "Prostokąt").fire();
                mouse(canvas, MouseEvent.MOUSE_PRESSED, 40, 50);
                mouse(canvas, MouseEvent.MOUSE_DRAGGED, 180, 130);
                mouse(canvas, MouseEvent.MOUSE_RELEASED, 180, 130);
                assertEquals(1, canvas.getShapes().size());
                var rectangle = (EditableRectangle) canvas.getShapes().getFirst();
                assertEquals(140, rectangle.getWidth(), 0.01);
                assertEquals(80, rectangle.getHeight(), 0.01);
                Event.fireEvent(rectangle, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.RIGHT,
                        false, false, false, false));
                assertEquals(15, rectangle.getRotate());
                assertNotNull(stage.getScene().snapshot(null));
                return null;
            } finally {
                stage.close();
            }
        });
    }

    @Test void changingToolbarToolAndEscapeRemoveOnlyThePreview() throws Exception {
        onFx(() -> {
            Stage stage = new Stage();
            try {
                PaintView view = new PaintView(stage);
                stage.setScene(new Scene(view, 720, 540));
                stage.show();
                view.applyCss();
                view.layout();
                var canvas = view.getCanvas();
                var original = new EditableRectangle(10, 10, 20, 30);
                canvas.addShape(original);
                button(view, "Prostokąt").fire();
                mouse(canvas, MouseEvent.MOUSE_PRESSED, 80, 90);
                assertEquals(2, canvas.getShapes().size());
                button(view, "Okrąg").fire();
                assertEquals(1, canvas.getShapes().size());
                mouse(canvas, MouseEvent.MOUSE_PRESSED, 160, 160);
                Event.fireEvent(canvas, new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE,
                        false, false, false, false));
                assertEquals(java.util.List.of(original), canvas.getShapes());
                return null;
            } finally {
                stage.close();
            }
        });
    }
}

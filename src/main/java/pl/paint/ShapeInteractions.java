package pl.paint;

import javafx.geometry.Point2D;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Shape;

public final class ShapeInteractions {
    private ShapeInteractions() { }

    public static void install(Shape shape) {
        shape.setFocusTraversable(true);
        shape.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
                ShapeTransforms.rotate(shape, event.getCode() == KeyCode.LEFT ? -15 : 15);
                event.consume();
            }
        });
        shape.setOnContextMenuRequested(event -> {
            Color initial = shape.getFill() instanceof Color color ? color : Color.RED;
            ColorPicker picker = new ColorPicker(initial);
            ContextMenu menu = new ContextMenu(new CustomMenuItem(picker, false));
            picker.setOnAction(action -> {
                shape.setFill(picker.getValue());
                menu.hide();
            });
            menu.show(shape, event.getScreenX(), event.getScreenY());
            event.consume();
        });
        Point2D[] previous = new Point2D[1];
        shape.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY) return;
            shape.requestFocus();
            previous[0] = shape.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
            event.consume();
        });
        shape.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown() || previous[0] == null) return;
            Point2D current = shape.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
            ShapeTransforms.move(shape, current.getX() - previous[0].getX(),
                    current.getY() - previous[0].getY());
            previous[0] = current;
            event.consume();
        });
        shape.setOnMouseReleased(event -> previous[0] = null);
        shape.setOnScroll(event -> {
            ShapeTransforms.scale(shape, Math.exp(Math.clamp(event.getDeltaY() / 500, -3, 3)));
            event.consume();
        });
    }
}

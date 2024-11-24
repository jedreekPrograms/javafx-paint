package pl.paint;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Shape;

public final class ShapeInteractions {
    private ShapeInteractions() { }

    public static void install(Shape shape) {
        shape.setFocusTraversable(true);
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

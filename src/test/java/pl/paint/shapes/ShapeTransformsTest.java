package pl.paint.shapes;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ShapeTransformsTest {
    @Test void movementChangesTranslationWithoutLosingGeometry() {
        var shape = new EditableRectangle(10, 20, 40, 60);
        shape.setRotate(30);
        ShapeTransforms.move(shape, 25, -12);
        assertEquals(25, shape.getTranslateX());
        assertEquals(-12, shape.getTranslateY());
        assertEquals(10, shape.getX());
        assertEquals(30, shape.getRotate());
    }

    @Test void rectangleScalingKeepsItsCenter() {
        var shape = new EditableRectangle(10, 20, 40, 60);
        ShapeTransforms.scale(shape, 2);
        assertEquals(80, shape.getWidth());
        assertEquals(120, shape.getHeight());
        assertEquals(30, shape.getX() + shape.getWidth() / 2);
        assertEquals(50, shape.getY() + shape.getHeight() / 2);
    }

    @Test void ellipseScalingKeepsItsCenterAndAspectRatio() {
        var shape = new EditableEllipse(100, 80, 20, 10);
        ShapeTransforms.scale(shape, 1.5);
        assertEquals(30, shape.getRadiusX());
        assertEquals(15, shape.getRadiusY());
        assertEquals(100, shape.getCenterX());
        assertEquals(80, shape.getCenterY());
    }

    @Test void polygonScalingUpdatesEveryCoordinateAroundItsCenter() {
        var shape = new EditablePolygon();
        shape.getPoints().addAll(0.0, 0.0, 20.0, 0.0, 10.0, 20.0);
        ShapeTransforms.scale(shape, 2);
        assertEquals(java.util.List.of(-10.0, -10.0, 30.0, -10.0, 10.0, 30.0), shape.getPoints());
    }

    @Test void shrinkingStopsAtMinimumSize() {
        var shape = new EditableRectangle(0, 0, 20, 10);
        ShapeTransforms.scale(shape, 0.00001);
        assertEquals(2, shape.getHeight(), 0.000001);
        assertEquals(4, shape.getWidth(), 0.000001);
    }

    @Test void growthStopsAtMaximumSize() {
        var shape = new EditableEllipse(0, 0, 20, 10);
        ShapeTransforms.scale(shape, Double.MAX_VALUE);
        assertEquals(ShapeTransforms.MAX_SIZE, shape.getRadiusX() * 2, 0.01);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -1, Double.NaN, Double.POSITIVE_INFINITY})
    void invalidScaleDoesNotChangeGeometry(double factor) {
        var shape = new EditableRectangle(0, 0, 20, 10);
        ShapeTransforms.scale(shape, factor);
        assertEquals(20, shape.getWidth());
        assertEquals(10, shape.getHeight());
    }

    @Test void invalidMovementDoesNotIntroduceNaN() {
        var shape = new EditableRectangle(0, 0, 20, 10);
        ShapeTransforms.move(shape, Double.NaN, 5);
        assertEquals(0, shape.getTranslateX());
        assertEquals(0, shape.getTranslateY());
    }

    @Test void fullRotationReturnsToStartingOrientation() {
        var shape = new EditableRectangle(0, 0, 20, 10);
        for (int i = 0; i < 24; i++) ShapeTransforms.rotate(shape, 15);
        assertEquals(0, shape.getRotate());
        assertEquals(Color.RED, shape.getFill());
    }
}

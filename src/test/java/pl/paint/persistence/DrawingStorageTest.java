package pl.paint.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pl.paint.shapes.*;
import static org.junit.jupiter.api.Assertions.*;

class DrawingStorageTest {
    @TempDir Path directory;
    final DrawingStorage storage = new DrawingStorage();

    @Test void roundTripPreservesAllThreeShapesAndVisualTransforms() throws IOException {
        var rectangle = new EditableRectangle(10, 20, 30, 40);
        var ellipse = new EditableEllipse(80, 70, 15, 25);
        var polygon = new EditablePolygon();
        polygon.getPoints().addAll(0.0, 0.0, 20.0, 0.0, 10.0, 20.0);
        List<Shape> originals = List.of(rectangle, ellipse, polygon);
        for (Shape shape : originals) {
            ShapeTransforms.move(shape, 125, -35);
            ShapeTransforms.rotate(shape, 45);
            shape.setScaleX(1.2);
            shape.setScaleY(0.8);
            shape.setFill(Color.CORNFLOWERBLUE);
        }
        Path file = directory.resolve("rysunek-ąę.json");
        storage.save(file, originals);
        var loaded = storage.load(file);
        assertEquals(3, loaded.size());
        for (int i = 0; i < 3; i++) {
            Shape before = originals.get(i), after = loaded.get(i);
            assertEquals(before.getClass(), after.getClass());
            assertEquals(before.getFill(), after.getFill());
            assertEquals(before.getRotate(), after.getRotate());
            assertEquals(before.getTranslateX(), after.getTranslateX());
            assertEquals(before.getTranslateY(), after.getTranslateY());
            assertEquals(before.getScaleX(), after.getScaleX());
            assertEquals(before.getScaleY(), after.getScaleY());
            assertEquals(before.getBoundsInParent().getMinX(), after.getBoundsInParent().getMinX(), 0.001);
            assertEquals(before.getBoundsInParent().getMaxY(), after.getBoundsInParent().getMaxY(), 0.001);
        }
        assertEquals(30, ((EditableRectangle) loaded.get(0)).getWidth());
        assertEquals(25, ((EditableEllipse) loaded.get(1)).getRadiusY());
        assertEquals(polygon.getPoints(), ((EditablePolygon) loaded.get(2)).getPoints());
    }

    @Test void opensOriginalFormatWithoutNewTransformFields() throws IOException {
        Path file = directory.resolve("legacy.json");
        Files.writeString(file, """
            [
              {"type":"RECTANGLE","x":10,"y":20,"width":30,"height":40,"rotateAngle":15,"colorHex":"0xff0000ff"},
              {"type":"ELLIPSE","x":60,"y":70,"radiusX":20,"radiusY":10,"rotateAngle":0},
              {"type":"POLYGON","points":[0,0,20,0,10,20],"rotateAngle":30,"colorHex":"0x0000ffff"}
            ]
            """);
        var loaded = storage.load(file);
        assertEquals(3, loaded.size());
        assertEquals(15, loaded.get(0).getRotate());
        assertEquals(0, loaded.get(0).getTranslateX());
        assertEquals(1, loaded.get(0).getScaleX());
        assertEquals(Color.BLUE, loaded.get(2).getFill());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "null", "{}", "[null]", "[", "[] trailing",
        "[{\"type\":\"TRIANGLE\"}]", "[{\"width\":20,\"height\":10}]",
        "[{\"type\":\"RECTANGLE\",\"width\":-1,\"height\":10}]",
        "[{\"type\":\"ELLIPSE\",\"radiusX\":0,\"radiusY\":10}]",
        "[{\"type\":\"POLYGON\",\"points\":[0,0,20,0,10]}]",
        "[{\"type\":\"POLYGON\",\"points\":null}]",
        "[{\"type\":\"POLYGON\",\"points\":[0,0,20,0,null,10]}]",
        "[{\"type\":\"RECTANGLE\",\"width\":20,\"height\":10,\"scaleX\":0}]",
        "[{\"type\":\"RECTANGLE\",\"width\":20,\"height\":10,\"x\":1e309}]",
        "[{\"type\":\"RECTANGLE\",\"width\":20,\"height\":10,\"colorHex\":\"invalid-color\"}]"
    })
    void rejectsMalformedOrInvalidDocuments(String json) throws IOException {
        Path file = directory.resolve("bad.json");
        Files.writeString(file, json);
        assertThrows(IOException.class, () -> storage.load(file));
    }

    @Test void emptyDrawingIsValid() throws IOException {
        Path file = directory.resolve("empty.json");
        storage.save(file, List.of());
        assertTrue(storage.load(file).isEmpty());
    }

    @Test void invalidShapeDoesNotOverwriteExistingFile() throws IOException {
        Path file = directory.resolve("keep.json");
        Files.writeString(file, "previous contents");
        assertThrows(IllegalArgumentException.class,
                () -> storage.save(file, List.of(new EditableRectangle(0, 0, -1, 20))));
        assertEquals("previous contents", Files.readString(file));
    }

    @Test void successfulSaveReplacesFileAndCleansTemporaryFile() throws IOException {
        Path file = directory.resolve("replace.json");
        Files.writeString(file, "old contents");
        storage.save(file, List.of(new EditableRectangle(0, 0, 30, 20)));
        assertEquals(1, storage.load(file).size());
        try (var paths = Files.list(directory)) { assertEquals(1, paths.count()); }
    }

    @Test void exportedPolygonIsAnIndependentSnapshot() {
        var polygon = new EditablePolygon();
        polygon.getPoints().addAll(0.0, 0.0, 20.0, 0.0, 10.0, 20.0);
        ShapeData data = ShapeData.fromShape(polygon);
        polygon.getPoints().set(0, 999.0);
        assertEquals(0, ((EditablePolygon) data.toShape()).getPoints().get(0));
    }

    @Test void missingFileIsReported() {
        assertThrows(IOException.class, () -> storage.load(directory.resolve("missing.json")));
    }
}

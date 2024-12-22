package pl.paint.persistence;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javafx.scene.shape.Shape;

public final class DrawingStorage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<Shape> load(Path file) throws IOException {
        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            ShapeData[] data = gson.fromJson(reader, ShapeData[].class);
            if (data == null) throw new IOException("Expected a JSON array of shapes");
            return Arrays.stream(data).map(value -> {
                if (value == null) throw new IllegalArgumentException("Null shape entry");
                return value.toShape();
            }).toList();
        } catch (JsonParseException | IllegalArgumentException exception) {
            throw new IOException("Invalid drawing: " + exception.getMessage(), exception);
        }
    }

    public void save(Path file, List<Shape> shapes) throws IOException {
        var data = shapes.stream().map(ShapeData::fromShape).toList();
        Path destination = file.toAbsolutePath();
        Path temporary = Files.createTempFile(destination.getParent(), ".paint-", ".json");
        try {
            try (var writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
            try {
                Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (JsonParseException exception) {
            throw new IOException("Could not write drawing: " + exception.getMessage(), exception);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }
}

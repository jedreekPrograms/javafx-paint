package pl.paint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javafx.scene.shape.Shape;

public final class DrawingStorage {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(Path file, List<Shape> shapes) throws IOException {
        var data = shapes.stream().map(ShapeData::fromShape).toList();
        try (var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }
}

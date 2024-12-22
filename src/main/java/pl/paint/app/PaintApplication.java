package pl.paint.app;

import pl.paint.ui.PaintView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public final class PaintApplication extends Application {
    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new PaintView(stage), 720, 540));
        stage.setMinWidth(480);
        stage.setMinHeight(360);
        stage.setTitle("GeometryEditorFX");
        var icon = PaintApplication.class.getResourceAsStream("/images/paintlogo.jpeg");
        if (icon != null) stage.getIcons().add(new Image(icon));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

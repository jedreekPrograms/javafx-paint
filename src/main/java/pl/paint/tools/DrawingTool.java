package pl.paint.tools;



import javafx.scene.input.MouseEvent;

public interface DrawingTool {
    default void pressed(MouseEvent event) { }
    default void dragged(MouseEvent event) { }
    default void released(MouseEvent event) { }
    default void clicked(MouseEvent event) { }
    void cancel();
    boolean isFinished();
}

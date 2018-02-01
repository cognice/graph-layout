package us.cognice.graph.layout;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

/**
 * Created by Kirill Simonov on 16.06.2017.
 */
public class ZoomPane extends Pane {

    private static final double MAX_SCALE = 10.0d;
    private static final double MIN_SCALE = .1d;

    private DoubleProperty scale = new SimpleDoubleProperty(1.0);

    public ZoomPane() {
        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);
    }

    public void zoom(ScrollEvent event) {
        setManaged(false);
        double delta = 1.2;
        double scale = getScale();
        double oldScale = scale;
        if (event.getDeltaY() < 0)
            scale /= delta;
        else
            scale *= delta;
        scale = clamp(scale, MIN_SCALE, MAX_SCALE);
        double f = (scale / oldScale) - 1;
        double dx  = event.getSceneX() - getBoundsInParent().getMinX();
        double dy  = event.getSceneY() - getBoundsInParent().getMinY();
        setScale(scale);
        relocate(getLayoutX() - dx * f, getLayoutY() - dy * f);
        event.consume();
    }

    public double getScale() {
        return scale.get();
    }

    public void setScale(double scale) {
        this.scale.set(scale);
    }

    private static double clamp(double value, double min, double max) {
        if (Double.compare(value, min) < 0)
            return min;
        if (Double.compare(value, max) > 0)
            return max;
        return value;
    }
}

package us.cognice.graph.layout.forced;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import us.cognice.graph.layout.Cell;
import us.cognice.graph.layout.Vector;

public class ForcedLayoutNode extends Cell {

    private Vector displacement = new Vector(0, 0);
    private Vector position;

    public ForcedLayoutNode(String id, String name) {
        super(id, name);
    }

    public ForcedLayoutNode(String id, String name, int radius, Color color) {
        super(id, name, radius, color);
    }

    public ForcedLayoutNode(String id, String name, int radius, String url) {
        super(id, name, radius, url);
    }

    public ForcedLayoutNode(String id, String name, int radius, Image image) {
        super(id, name, radius, image);
    }

    public Vector getDisplacement() {
        return displacement;
    }

    public void resetDisplacement() {
        displacement.setX(0);
        displacement.setY(0);
    }

    public Vector getPosition() {
        if (position == null) {
            position = new Vector(getLayoutX(), getLayoutY());
        }
        return position;
    }

    public void relocate() {
        super.relocate(position.getX(), position.getY());
    }

    public DoubleBinding positionXProperty() {
        return layoutXProperty().add(getBoundsInParent().getMinX() + getCenterOffset().getX());
    }

    public DoubleBinding positionYProperty() {
        return layoutYProperty().add(getBoundsInParent().getMinY() + getCenterOffset().getY());
    }
}

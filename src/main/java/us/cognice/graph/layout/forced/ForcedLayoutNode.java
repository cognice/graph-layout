package us.cognice.graph.layout.forced;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import us.cognice.graph.layout.Cell;
import us.cognice.graph.layout.Coordinates;

public class ForcedLayoutNode extends Cell {

    private Coordinates displacement = new Coordinates(0, 0);
    private Coordinates position;

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

    public Coordinates getDisplacement() {
        return displacement;
    }

    public void resetDisplacement() {
        displacement.setX(0);
        displacement.setY(0);
    }

    public Coordinates getPosition() {
        if (position == null) {
            position = new Coordinates(getLayoutX(), getLayoutY());
        }
        return position;
    }

    public void relocate() {
        super.relocate(position.getX(), position.getY());
    }
}

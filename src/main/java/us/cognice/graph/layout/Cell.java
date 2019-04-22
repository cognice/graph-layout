package us.cognice.graph.layout;

import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */

public class Cell extends Circle {

    private String name;

    public Cell(String id, String name) {
        this(id, name, 3, Color.DODGERBLUE);
    }

    public Cell(String id, String name, int radius, Color color) {
        super(radius);
        this.name = name;
        setId(id);
        setStroke(color);
        setFill(color);
    }

    public Cell(String id, String name, int radius, String url) {
        super(radius);
        this.name = name;
        setId(id);
        setFill(new ImagePattern(new Image(url)));
    }

    public Cell(String id, String name, int radius, Image image) {
        super(radius);
        this.name = name;
        setId(id);
        setFill(new ImagePattern(image));
    }

    public String getName() {
        return name;
    }

    public Vector getCenterOffset() {
        return new Vector(getRadius(), getRadius());
    }

    public Vector getPosition() {
        return new Vector(getCenterX(), getCenterY());
    }

    public double distance(Cell cell) {
        return Math.sqrt(Math.pow(getPosition().getX() - cell.getPosition().getX(), 2) + Math.pow(getPosition().getY() - cell.getPosition().getY(), 2));
    }

    public Vector delta(Cell cell) {
        return new Vector(getPosition().getX() - cell.getPosition().getX(), getPosition().getY() - cell.getPosition().getY());
    }

    public ObservableValue<Number> positionXProperty() {
        return centerXProperty();
    }

    public ObservableValue<Number> positionYProperty() {
        return centerYProperty();
    }
}

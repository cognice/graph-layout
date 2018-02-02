package us.cognice.graph.layout;

import javafx.scene.shape.Line;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public class Edge<T extends Cell> extends Line {

    protected T source;
    protected T target;

    public Edge(T source, T target) {
        this.source = source;
        this.target = target;
        setStrokeWidth(0.2);
        setOpacity(0.5);
        startXProperty().bind(source.layoutXProperty().add(source.getBoundsInParent().getMinX() + source.getCenterOffset().getX()));
        startYProperty().bind(source.layoutYProperty().add(source.getBoundsInParent().getMinY() + source.getCenterOffset().getY()));
        endXProperty().bind(target.layoutXProperty().add(target.getBoundsInParent().getMinX() + target.getCenterOffset().getX()));
        endYProperty().bind(target.layoutYProperty().add(target.getBoundsInParent().getMinX() + target.getCenterOffset().getY()));
    }

    public T getSource() {
        return source;
    }

    public T getTarget() {
        return target;
    }

}

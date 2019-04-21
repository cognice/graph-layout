package us.cognice.graph.layout;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */

public class MouseGestures {

    private final DragContext dragContext = new DragContext();
    private Graph<? extends Cell> graph;
    private Layout layout;

    private DragStep loop = new DragStep();

    private class DragContext {
        List<Edge> neighbors = new ArrayList<>();
        double x;
        double y;
    }

    private class DragStep extends AnimationTimer {
        private Cell target;
        private double displacement;
        @Override
        public void handle(long now) {
            layout.dragStep(target, displacement);
        }
        void setTarget(Cell target) {
            this.target = target;
        }
        void setDisplacement(double displacement) {
            this.displacement = displacement;
        }
    }

    public MouseGestures(Graph<? extends Cell> graph, Layout layout) {
        this.graph = graph;
        this.layout = layout;
    }

    public <T extends Cell> void apply(final T cell) {
        cell.setOnMousePressed(onMousePressed);
        cell.setOnMouseDragged(onMouseDragged);
        cell.setOnMouseReleased(onMouseReleased);
        Tooltip.install(cell, new Tooltip(cell.getName()));
    }

    private EventHandler<MouseEvent> onMousePressed = event -> {
        Cell cell = (Cell) event.getSource();
        loop.setTarget(cell);
        loop.setDisplacement(0);
        loop.start();
        double scale = graph.getScale();
        dragContext.x = cell.getBoundsInParent().getMinX() * scale - event.getScreenX();
        dragContext.y = cell.getBoundsInParent().getMinY() * scale - event.getScreenY();
        graph.getEdges().stream().filter(e -> e.target.equals(cell) || e.source.equals(cell)).forEach(e -> {
            e.setStroke(Color.ORANGERED);
            e.setStrokeWidth(0.7);
            dragContext.neighbors.add(e);
        });
    };

    private EventHandler<MouseEvent> onMouseDragged = event -> {
        Cell cell = (Cell) event.getSource();
        double offsetX = event.getScreenX() + dragContext.x;
        double offsetY = event.getScreenY() + dragContext.y;
        double scale = graph.getScale();
        offsetX /= scale;
        offsetY /= scale;
        double displacement = Math.sqrt(Math.pow(offsetX - cell.getPosition().getX(), 2) + Math.pow(offsetY - cell.getPosition().getY(), 2));
        cell.relocate(offsetX, offsetY);
        cell.getPosition().setX(offsetX);
        cell.getPosition().setY(offsetY);
        loop.setDisplacement(displacement);
        event.consume();
    };

    private EventHandler<MouseEvent> onMouseReleased = event -> {
        loop.stop();
        dragContext.neighbors.forEach(e -> {
            e.setStroke(Color.BLACK);
            e.setStrokeWidth(0.2);
        });
        layout.restart();
        dragContext.neighbors.clear();
    };
}

package us.cognice.graph.layout;

import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;

import java.util.Random;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public class ForcedLayout implements Layout {

    private final BorderPane container;
    private final Graph<? extends Cell> graph;
    private final int width, height;
    private final double attractionCoefficient, k;
    private final DragContext dragContext = new DragContext();

    private class LayoutStep extends AnimationTimer {
        private long timeout = 7000;
        private long start = 0;
        private double temperature;
        private double energy = Double.MAX_VALUE;
        private int progress = 0;
        LayoutStep(double temperature) {
            this.temperature = temperature;
        }
        @Override
        public void start() {
            super.start();
            start = System.nanoTime();
        }
        @Override
        public void handle(long now) {
            step(temperature);
            double previousEnergy = energy;
            energy = 0;
            for (Cell c: graph.getCells().values()) {
                energy += c.getDisplacement().length();
                c.getPosition().add(c.getDisplacement().unit().scale(temperature));
                c.relocate();
            }
            if (energy < previousEnergy) {
                progress++;
                if (progress > 5) {
                    progress = 0;
                    temperature /= 0.9;
                }
            } else {
                progress = 0;
                temperature *= 0.9;
            }
            if (temperature <= 0.01 || now - start > timeout * 1000000) this.stop();
        }
    }

    private class DragContext {
        double x;
        double y;
    }

    public ForcedLayout(BorderPane container, Graph<? extends Cell> graph, int width, int height) {
        this(container, graph, width, height, graph.getCells().size());
    }

    public ForcedLayout(BorderPane container, Graph<? extends Cell> graph, int width, int height, double attractionCoefficient) {
        this.container = container;
        this.graph = graph;
        this.graph.canvas.setManaged(false);
        this.width = width;
        this.height = height;
        this.attractionCoefficient = attractionCoefficient;
        this.k = Math.sqrt(width * height / graph.getCells().size());
    }

    @Override
    public void calculate() {
        if (!graph.isLocated()) {
            Random rnd = new Random();
            for (Cell c : graph.getCells().values()) {
                Coordinates offset = c.getCenterOffset();
                double x = rnd.nextDouble() * (width - 2 * offset.getX());
                double y = rnd.nextDouble() * (height - 2 * offset.getY());
                c.getPosition().setX(x);
                c.getPosition().setY(y);
            }
            graph.setLocated(true);
        }
        container.setCenter(graph.canvas);
        applyDragAndScroll();
        LayoutStep loop = new LayoutStep(width / 10);
        loop.start();
    }

    @Override
    public void recalculate() {
        LayoutStep loop = new LayoutStep(5);
        loop.start();
    }


    @Override
    public void step(double temperature) {
        for (Cell v: graph.getCells().values()) {
            v.resetDisplacement();
            for (Cell u: graph.getCells().values()) {
                if (!u.equals(v)) {
                    v.getDisplacement().add(v.delta(u).unit().scale(fr(u, v)));
                }
            }
        }
        for (Edge e: graph.getEdges()) {
            Cell v = e.source, u = e.target;
            Coordinates delta = v.delta(u).unit().scale(fa(u, v));
            v.getDisplacement().subtract(delta);
            u.getDisplacement().add(delta);
        }
    }

    @Override
    public <T extends Cell> void dragStep(T cell, double displacement) {
        for (Cell v: graph.getCells().values()) {
            if (v != cell) {
                v.resetDisplacement();
                for (Cell u: graph.getCells().values()) {
                    if (!u.equals(v) && u != cell) {
                        v.getDisplacement().add(v.delta(u).unit().scale(fr(u, v)));
                    }
                }
            }
        }
        for (Edge e: graph.getEdges()) {
            Cell v = e.source, u = e.target;
            Coordinates delta = v.delta(u).unit().scale(fa(u, v));
            if (v != cell) v.getDisplacement().subtract(delta);
            if (u != cell) u.getDisplacement().add(delta);
        }
        for (Cell c: graph.getCells().values()) {
            if (c != cell) {
                c.getPosition().add(c.getDisplacement().unit().scale(displacement));
                c.relocate();
            }
        }
    }

    private void applyDragAndScroll() {
        container.setOnMousePressed(event -> {
            dragContext.x = event.getScreenX();
            dragContext.y = event.getScreenY();
        });
        container.setOnMouseDragged(event -> {
            this.container.setCursor(Cursor.CLOSED_HAND);
            double offsetX = dragContext.x - event.getScreenX();
            double offsetY = dragContext.y - event.getScreenY();
            dragContext.x = event.getScreenX();
            dragContext.y = event.getScreenY();
            graph.canvas.relocate(graph.canvas.getLayoutX() - offsetX , graph.canvas.getLayoutY() - offsetY);
            event.consume();
        });
        container.setOnScroll(event -> graph.canvas.zoom(event));
        container.setOnMouseReleased(event -> this.container.setCursor(Cursor.DEFAULT));
    }

    private double fa(Cell u, Cell v) {
        double distance = u.distance(v);
        return attractionCoefficient * distance * distance / k;
    }

    private double fr(Cell u, Cell v) {
        double distance = u.distance(v);
        return k * k / (distance + 0.000001);
    }

}

package us.cognice.graph.layout.forced;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.BorderPane;
import us.cognice.graph.layout.Cell;
import us.cognice.graph.layout.Vector;
import us.cognice.graph.layout.Edge;
import us.cognice.graph.layout.Graph;

import java.util.Random;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public class ForcedLayout extends DraggableScrollableLayout {

    private final int width, height;
    private final double attractionCoefficient, k;

    private LayoutStep loop;
    private boolean located;

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
            step();
            double previousEnergy = energy;
            energy = 0;
            for (ForcedLayoutNode c: graph.getCells().values()) {
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

    public ForcedLayout(BorderPane container, Graph<? extends ForcedLayoutNode> graph, int width, int height) {
        this(container, graph, width, height, graph.getCells().size());
    }

    public ForcedLayout(BorderPane container, Graph<? extends ForcedLayoutNode> graph, int width, int height, double attractionCoefficient) {
        super(container, graph);
        this.graph.getCanvas().setManaged(false);
        this.width = width;
        this.height = height;
        this.attractionCoefficient = attractionCoefficient;
        this.k = Math.sqrt(width * height / graph.getCells().size());
    }

    @Override
    public void start() {
        if (!located) {
            Random rnd = new Random();
            for (Cell c : graph.getCells().values()) {
                Vector offset = c.getCenterOffset();
                double x = rnd.nextDouble() * (width - 2 * offset.getX());
                double y = rnd.nextDouble() * (height - 2 * offset.getY());
                c.getPosition().setX(x);
                c.getPosition().setY(y);
            }
            located = true;
        }
        container.setCenter(graph.getCanvas());
        applyDragAndScroll();
        loop = new LayoutStep(width / 10);
        loop.start();
    }

    @Override
    public void restart() {
        loop = new LayoutStep(5);
        loop.start();
    }

    @Override
    public void stop() {
        if (loop != null) {
            loop.stop();
        }
    }

    @Override
    public void step() {
        for (ForcedLayoutNode v: graph.getCells().values()) {
            v.resetDisplacement();
            for (Cell u: graph.getCells().values()) {
                if (!u.equals(v)) {
                    v.getDisplacement().add(v.delta(u).unit().scale(fr(u, v)));
                }
            }
        }
        for (Edge<? extends ForcedLayoutNode> e: graph.getEdges()) {
            ForcedLayoutNode v = e.getSource(), u = e.getTarget();
            Vector delta = v.delta(u).unit().scale(fa(u, v));
            v.getDisplacement().subtract(delta);
            u.getDisplacement().add(delta);
        }
    }

    public <T extends Cell> void dragStep(T cell, double displacement) {
        for (ForcedLayoutNode v: graph.getCells().values()) {
            if (v != cell) {
                v.resetDisplacement();
                for (Cell u: graph.getCells().values()) {
                    if (!u.equals(v) && u != cell) {
                        v.getDisplacement().add(v.delta(u).unit().scale(fr(u, v)));
                    }
                }
            }
        }
        for (Edge<? extends ForcedLayoutNode> e: graph.getEdges()) {
            ForcedLayoutNode v = e.getSource(), u = e.getTarget();
            Vector delta = v.delta(u).unit().scale(fa(u, v));
            if (v != cell) v.getDisplacement().subtract(delta);
            if (u != cell) u.getDisplacement().add(delta);
        }
        for (ForcedLayoutNode c: graph.getCells().values()) {
            if (c != cell) {
                c.getPosition().add(c.getDisplacement().unit().scale(displacement));
                c.relocate();
            }
        }
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

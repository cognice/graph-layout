package us.cognice.graph.layout;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public class Graph<T extends Cell> {

    protected ZoomPane canvas;
    private Map<String, T> cells;
    private List<Edge<T>> edges;

    public Graph() {
        cells = new HashMap<>();
        edges = new ArrayList<>();
        canvas = new ZoomPane();
    }

    public void addCell(T cell) {
        cells.put(cell.getId(), cell);
        canvas.getChildren().add(cell);
    }

    public void addEdge(String from, String to) {
        T source = cells.get(from);
        T target = cells.get(to);
        if (source != null && target != null) {
            addEdge(source, target);
        }
    }

    public void addEdge(T from, T to) {
        Edge<T> edge = new Edge<>(from, to);
        edges.add(edge);
        canvas.getChildren().add(edge);
        edge.toBack();
    }

    public boolean removeCell(String id) {
        Cell removed = cells.remove(id);
        if (removed != null) {
            Predicate<Node> p = n -> {
                if (n instanceof Edge) {
                    Edge e = (Edge) n;
                    return e.source == removed || e.target == removed;
                }
                return false;
            };
            edges.removeIf(p);
            canvas.getChildren().remove(removed);
            canvas.getChildren().removeIf(p);
            return true;
        }
        return false;
    }

    public boolean removeEdge(String from, String to) {
        Predicate<Node> p = n -> {
            if (n instanceof Edge) {
                Edge e = (Edge) n;
                return e.source.getId().equals(from) && e.target.getId().equals(to)
                        || e.source.getId().equals(to) && e.target.getId().equals(from);
            }
            return false;
        };
        edges.removeIf(p);
        return canvas.getChildren().removeIf(p);
    }

    public boolean areCellsConnected(T a, T b) {
        for (Edge<T> e : edges) {
            if (e.source.equals(a) && e.target.equals(b)
                    || e.source.equals(b) && e.target.equals(a)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, T> getCells() {
        return cells;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    public double getScale() {
        return canvas.getScale();
    }

    public ZoomPane getCanvas() {
        return canvas;
    }
}

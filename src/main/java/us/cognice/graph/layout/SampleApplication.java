package us.cognice.graph.layout;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */

public class SampleApplication extends Application {

    private final int WIDTH = 1280, HEIGHT = 800;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        URL styleURL = getClass().getClassLoader().getResource("application.css");
        if (styleURL != null) {
            scene.getStylesheets().add(styleURL.toExternalForm());
        }
        primaryStage.setScene(scene);
        Graph<Cell> graph = buildGraph();
        Layout layout = new ForcedLayout(root, graph, WIDTH, HEIGHT);
        MouseGestures mouseGestures = new MouseGestures(graph, layout);
        graph.getCells().values().forEach(mouseGestures::apply);
        layout.calculate();
        primaryStage.show();
    }

    private Graph<Cell> buildGraph() {
        Graph<Cell> graph = new Graph<>();
        Random r = new Random();
        List<Cell> randomEdges = new ArrayList<>();
        Cell from = new Cell("0", "0");
        graph.addCell(from);
        for (int i = 1; i < 200; i++) {
            Cell to = new Cell(String.valueOf(i), String.valueOf(i));
            graph.addCell(to);
            graph.addEdge(from.getId(), to.getId());
            if (r.nextInt(100) < 50) {
                randomEdges.add(to);
            }
            from = to;
        }
        if (randomEdges.size() > 1) {
            from = randomEdges.get(r.nextInt(randomEdges.size()));
            for (int i = 1; i < 20; i++) {
                Cell to = randomEdges.get(r.nextInt(randomEdges.size()));
                graph.addEdge(from.getId(), to.getId());
                from = to;
            }
        }
        return graph;
    }
}

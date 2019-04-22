package us.cognice.graph.layout;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public interface Layout {
    void start();
    void restart();
    void stop();
    void step();
}

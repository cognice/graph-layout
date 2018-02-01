package us.cognice.graph.layout;

/**
 * Created by Kirill Simonov on 14.06.2017.
 */
public interface Layout {
    void calculate();
    void recalculate();
    void step(double temperature);
    <T extends Cell> void dragStep(T cell, double displacement);
}

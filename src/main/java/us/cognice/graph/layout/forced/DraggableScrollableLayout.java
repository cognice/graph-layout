package us.cognice.graph.layout.forced;

import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import us.cognice.graph.layout.Graph;
import us.cognice.graph.layout.Layout;

public abstract class DraggableScrollableLayout implements Layout {

    private final DragContext dragContext = new DragContext();

    protected final BorderPane container;
    protected final Graph<? extends ForcedLayoutNode> graph;

    public DraggableScrollableLayout(BorderPane container, Graph<? extends ForcedLayoutNode> graph) {
        this.container = container;
        this.graph = graph;
    }

    protected void applyDragAndScroll() {
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
            graph.getCanvas().relocate(graph.getCanvas().getLayoutX() - offsetX , graph.getCanvas().getLayoutY() - offsetY);
            event.consume();
        });
        container.setOnScroll(event -> graph.getCanvas().zoom(event));
        container.setOnMouseReleased(event -> this.container.setCursor(Cursor.DEFAULT));
    }

    private class DragContext {
        double x;
        double y;
    }
}

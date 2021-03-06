package views.main.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import models.graph.Graph;
import views.main.graph_helper.DefaultGraphHelper;
import views.main.graph_helper.GraphHelper;

import java.util.List;

public class Component_BFS extends AlgorithmComponent {

    public Component_BFS(Canvas c) {
        super(c);
    }

    @Override
    public String getComponentName() {
        return "BFS";
    }

    @Override
    public GraphHelper getGraphHelper() {
        return new DefaultGraphHelper();
    }

    @Override
    public void setOnAction() {

    }

    @Override
    public boolean validateStructure() {
        return false;
    }

    @Override
    public boolean setupAlgorithm() {
        return false;
    }

    @Override
    public void runAlgorithm() {

    }
}

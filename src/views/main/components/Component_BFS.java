package views.main.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import models.graph.Graph;

import java.util.List;

public class Component_BFS extends AlgorithmComponent {

    public Component_BFS(Canvas c) {
        super(c);
        setOnAction();
    }

    @Override
    public MenuItem getMenuItem() {
        return menuItem;
    }

    @Override
    public void setOnAction() {
        if(menuItem == null) {
            menuItem = new MenuItem("BFS");
        }
        menuItem.setOnAction(e -> {
            System.out.println("selected BFS");
            //keep track of selected menu item to prevent searching list
            setSelectedMenuItem(this);
            focusGraph();
        });
    }

    @Override
    public void build() {
        System.out.println("selected component: " + AlgorithmComponent.getSelectedMenuItem().getMenuItem().getText());
        graphHelper.initGraph((int)canvas.getWidth(), (int)canvas.getHeight());
        graph = graphHelper.getGraph();
        focusGraph();
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

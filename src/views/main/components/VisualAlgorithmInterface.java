package views.main.components;

import javafx.scene.control.MenuItem;
import models.GraphAlgorithms;
import models.GraphAlgorithms.GraphType;
import views.main.graph_helper.GraphHelper;

import java.util.List;

public interface VisualAlgorithmInterface {


    /*
        Used when user selects MenuItem from the MenuButton
        User may leave override empty
     */
    public void setOnAction();
    public boolean validateStructure();
    public boolean setupAlgorithm();
    public void runAlgorithm();
    public String getComponentName();
    public GraphHelper getGraphHelper();
}

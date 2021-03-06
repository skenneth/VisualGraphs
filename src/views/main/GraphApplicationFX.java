package views.main;
import com.sun.javafx.scene.traversal.Algorithm;
import controllers.AlgorithmsController;
import controllers.CanvasController;
import controllers.GraphController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.*;
import javafx.scene.paint.Color;
import models.graph.Graph;
import models.GraphAlgorithms.GraphType;
import models.graph.MyMath;
import views.main.components.*;

import java.io.File;

public class GraphApplicationFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    //GraphController gc = new GraphController();
    //AlgorithmsController ac = new AlgorithmsController();
    //CanvasController canvasController;
    String filename = "";
    int width = 750;
    int height = 600;
    private Stage stage;

    String algorithmPlaceholder = "Algorithm";
    GraphType type = GraphType.UNDIRECTED;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setWidth(width);
        stage.setHeight(height);
        VBox root = new VBox();
        HBox upper = new HBox();
        //CREATE CANVAS
        Canvas canvas = new Canvas(width, height-upper.getHeight());

        //CREATE MENU BUTTON
        MenuButton menuButton = new MenuButton(algorithmPlaceholder);

        //--------- ADD ALGORITHM COMPONENTS HERE ---------
        //Each component has a reference to the canvas
        Component_DFS dfs = new Component_DFS(canvas);
        Component_BFS bfs = new Component_BFS(canvas);
        Component_BST bst = new Component_BST(canvas);
        menuButton.getItems().addAll(dfs.getMenuItem(), bfs.getMenuItem(), bst.getMenuItem());
        //--------- END CODE ---------


        //--------- ADDITIONAL COMPONENTS TO BE ADDED ---------
        MenuButton direction = new MenuButton("Direction");
        MenuItem undirected = new MenuItem("Undirected");
        MenuItem directed = new MenuItem("Directed");
        direction.getItems().addAll(undirected, directed);

        undirected.setOnAction(e -> {
            type = GraphType.UNDIRECTED;
            updateGraph();
        });
        directed.setOnAction(e -> {
            type = GraphType.DIRECTED;
            updateGraph();
        });

        Button build = new Button("build");
        build.setOnAction(e -> {
            AlgorithmComponent component = AlgorithmComponent.getSelectedMenuItem();
            if(component != null) {
                component.setGraphType(type);
                component.build();
            }
            else
                System.out.println("no component selected");
        });
        //--------- END CODE ---------
        upper.getChildren().addAll(menuButton, build, direction);

        upper.setMinWidth(stage.getWidth());

        upper.setStyle("-fx-background-color: #c7c6c6");

        root.getChildren().addAll(upper, canvas);

        /*
        FXMLLoader canvas_fxml = new FXMLLoader(getClass().getResource("../../resources/fxml/canvas-graph.fxml"));
        Parent canvas_node = canvas_fxml.load();
        canvas_node.setStyle("-fx-background-color: #f0e6e6");
        canvasController = canvas_fxml.getController();
        canvasController.setDimension(width, height);
        gc.setCanvasController(canvasController);
        ac.setCanvasController(canvasController);
        canvasController.init();
        canvasController.setGraphController(gc);
        root.getChildren().addAll(upper, canvas_node);
        */

        Scene scene = new Scene(root);
        //setScene(scene);
        //---------------------------------

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    public void updateGraph() {
        AlgorithmComponent component = AlgorithmComponent.getSelectedMenuItem();
        if(component != null) {
            component.setGraphType(type);
            component.focusGraph();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /*
    @Override
    public void start(Stage stage) throws Exception {
        MyMath.angleMode(MyMath.ANGLE.DEG);
        this.stage = stage;
        stage.setWidth(width);
        stage.setHeight(height);
        VBox root = new VBox();
        HBox upper = new HBox();

        Button randomize = new Button("Randomize Graph");
        nodes = new TextField("nodes");
        edges = new TextField("edges");
        Button fileSelector = new Button("Upload File");

        menuButtonDirection = new MenuButton("Directed");
        MenuItem undirected = new MenuItem("Undirected");
        MenuItem directed = new MenuItem("Directed");
        menuButtonDirection.getItems().addAll(undirected, directed);

        menuButtonAlgorithm = new MenuButton("Algorithm");
        MenuItem dfs = new MenuItem("DFS");
        MenuItem bfs = new MenuItem("BFS");
        MenuItem bsio = new MenuItem("BSIO"); //binary search - in order
        menuButtonAlgorithm.getItems().addAll(dfs, bfs, bsio);

        Button runAlgorithm = new Button("Run Algorithm");
        Button rotate = new Button("Rotate 360");
        Button test = new Button("Self sort");

        upper.getChildren().addAll(randomize, nodes, edges, fileSelector);
        upper.getChildren().addAll(menuButtonDirection, menuButtonAlgorithm);
        upper.getChildren().addAll(runAlgorithm, rotate);

        upper.getChildren().add(test);
        test.setOnAction(e -> {
            gc.testSelfSort();
        });

        upper.setMinWidth(stage.getWidth());
        upper.setStyle("-fx-background-color: #c7c6c6");

        FXMLLoader canvas_fxml = new FXMLLoader(getClass().getResource("../../resources/fxml/canvas-graph.fxml"));
        Parent canvas_node = canvas_fxml.load();
        canvas_node.setStyle("-fx-background-color: #f0e6e6");
        canvasController = canvas_fxml.getController();
        canvasController.setDimension(width, height);
        gc.setCanvasController(canvasController);
        ac.setCanvasController(canvasController);
        canvasController.init();
        canvasController.setGraphController(gc);
        root.getChildren().addAll(upper, canvas_node);

        Scene scene = new Scene(root);
        //CONTROL NODE SETUP
        setSize(nodes, edges);
        setTextFields(nodes, edges);
        setMenu(undirected, false, "direction");
        setMenu(directed, true, "direction");

        setMenu(bfs, false, "algorithm");
        setMenu(dfs, true, "algorithm");
        setMenu(bsio, true, "algorithm");

        setFileSelector(fileSelector);
        setBuildType(randomize, true);
        startAlgorithm(runAlgorithm);
        setRotate(rotate);
        setScene(scene);
        //---------------------------------

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
     */


    /*
    @Override
    public void stop() throws Exception {
        FileParser.saveFile(gc.getGraph());
        super.stop();
    }

    public void setRotate(Button rotate) {
        rotate.setOnAction(e -> {
            Thread thread = new Thread(() -> {
                if(gc == null)
                    return;
                gc.calculateAveragePivot();
                for(int i = 0; i< 360; i++) {
                    gc.rotateGraphAveragePivot(1);
                    //draw updated graph
                    try {
                        Thread.sleep(20);
                    } catch (Exception ex) { }
                    gc.updateEdges();
                    //canvasController.repaint();
                }
            });
            thread.start();
        });
    }

    GraphAlgorithms.GraphType type = GraphAlgorithms.GraphType.DIRECTED;
    public void startAlgorithm(Button operation) {
        operation.setOnAction(e -> {
            Graph g = gc.getGraph();
            if(g == null)
                return;

            ac.setGraph(g);
            ac.setColors();
            if(menuButtonAlgorithm.getText().equals("algorithm")) { //default setup if it isn't already selected
                ac.setUpGraph(GraphAlgorithms.OperationType.SEARCH,
                        GraphAlgorithms.SearchType.DFS,
                        type);
            }
            ac.startOperation();
        });
    }

    TextField nodes, edges;
    public int getCount(String type) {
        int count;
        switch(type) {
            case "nodes":
                try {
                    count = Integer.parseInt(nodes.getText());
                } catch (Exception e) { count = 7; }
                break;
            case "edges":
                try {
                    count = Integer.parseInt(edges.getText());
                }catch (Exception e) { count = 15; }
                break;
            default:
                count = 3;
        }
        return count;
    }

    public void setTextFields(TextField...fields) {
        for(TextField field : fields) {
            switch(field.getText().split(":")[0]) {
                case "nodes":
                    field.setPromptText("nodes: 2-20");
                    break;
                case "edges":
                    field.setPromptText("edges: 8-30");
                    break;
            }
            field.clear();
        }
    }

    public void setBuildType(Button build, boolean random) {
        if(random) {
            //CONSTRUCTS GRAPH RANDOMLY
            build.setOnAction(e -> {
                //gc.setRadius();
                gc.generateRandomGraph(getCount("nodes"), getCount("edges"));
                run(stage, true);
            });
        }
        clearFields();
    }
    public void setFileSelector(Button fileSelector) {
        //FINDS FILE TO BUILD GRAPH
        fileSelector.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            try {
                fc.setInitialDirectory(new File("C:/Users/azva_/IdeaProjects/VisualGraphs/src/resources/text"));
                File selectedFile = fc.showOpenDialog(stage);
                filename = selectedFile.getAbsolutePath();
                Graph graph = FileParser.parseFile(filename);
                gc.setGraph(graph);
                System.out.println("reading file: " + filename);
                if(filename.contains(".cus"))
                    run(stage, false);
                else
                    run(stage, true);
                gc.getGraph().sort();
            }catch(Exception ex) {
                ex.printStackTrace();
                System.out.println("Error reading file");
            }
        });
        clearFields();
    }
    MenuButton
        menuButtonDirection,
        menuButtonAlgorithm;
    public void setMenu(MenuItem item, boolean directed, String menuType) {
        switch(menuType) {
            case "direction": {
                item.setOnAction(e -> {
                    String label = item.getText();
                    menuButtonDirection.setText(label);
                    if (gc.getGraph() == null) return;
                    gc.getGraph().setDirected(directed);
                    type = directed ? GraphAlgorithms.GraphType.DIRECTED : GraphAlgorithms.GraphType.UNDIRECTED;
                    //canvasController.collectGraphShapes();
                    System.out.println("collecting shapes in graph");
                    //canvasController.repaint();
                });
                break;
            }
            case "algorithm": {
                item.setOnAction(e -> {
                    String label = item.getText();
                    menuButtonAlgorithm.setText(label);
                    GraphAlgorithms.SearchType searchType = GraphAlgorithms.SearchType.DFS;
                    switch(label) {
                        case "DFS": searchType = GraphAlgorithms.SearchType.DFS; break;
                        case "BFS": searchType = GraphAlgorithms.SearchType.BFS; break;
                        case "BSIO": searchType = GraphAlgorithms.SearchType.BSIO; break;
                    }
                    ac.setUpGraph(GraphAlgorithms.OperationType.SEARCH,
                            searchType,
                            type);
                });
                break;
            }
        }
    }
    public void setSize(Control...controls) {
        for(Control control : controls)
            control.setPrefWidth(80);
    }

    public void run(Stage stage, boolean calculatePlacement) {
        if(gc == null)
            return;
        //CALCULATES GRAPH PLACEMENT
        if(!(gc.getGraph()).isAlreadyPlaced()) {
            gc.calculatePlacement();
            gc.getGraph().sort();
        }
        gc.init();
        //GET READY TO DRAW
        //canvasController.collectGraphShapes();
        //canvasController.paintComp();
    }
    public void clearFields() {
        nodes.clear();
        edges.clear();
    }

    double zoom = 1.0;
    double delta = 0.03;

    public void setScene(Scene scene) {
        setScrolling(scene);
        //KEY LISTENERS
        scene.setOnKeyPressed(e -> {
            //canvasController.onKeyPressed(e.getCode());
        });
        scene.setOnKeyReleased(e -> {
            //canvasController.onKeyReleased(e.getCode());
        });
    }
    public void setScrolling(Scene scene) {
        //SCROLLING
        scene.setOnScroll(e -> {
            if(gc == null)
                return;
            boolean change = false;
            double rate = e.getDeltaY();
            if(rate > 0) { //scrolling up
                if(zoom >= 3.0)
                    zoom = 2.9;
                else
                    change = true;
                rate = delta;
            }
            else {
                if(zoom <= 0.6)
                    zoom = 0.7;
                else
                    change = true;
                rate = -delta;
            }
            zoom += rate;
            if(change) {
                gc.resizeGraph(rate);
            }
            //canvasController.repaint();
        });
    }
     */
}

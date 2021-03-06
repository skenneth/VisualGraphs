package controllers;
import javafx.scene.canvas.GraphicsContext;
import models.*;
import models.graph.Edge;
import models.graph.Graph;
import models.graph.MyMath;
import models.graph.Vertex;
import models.shapes.Arrow;
import models.shapes.Circle;
import models.shapes.Line;
import models.shapes.Shape;

import java.util.*;
import java.util.stream.Collectors;

import static models.graph.MyMath.*;

public class GraphController {
    private CanvasController canvasController;
    private Graph graph = null;
    private double spacing = 1.5;
    private static String charSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public void setCanvasController(CanvasController cc) { canvasController = cc; }
    public char[] getCharSet(int nodes) {
        char[] set = new char[nodes];
        for(int i = 0; i < nodes; i++) {
            set[i] = charSet.charAt(i);
        }
        return set;
    }
    public void generateRandomGraph(int nodeLimit, int edgeLimit) {
        graph = new Graph();
        char[] charSet = getCharSet(nodeLimit);
        char v1, v2;
        int size = charSet.length;
        for(int i = 0; i < edgeLimit; i++) {
            v1 = charSet[random.nextInt(size)];
            do {
                v2 = charSet[random.nextInt(size)];
            }while(v1 == v2);
            graph.addVertices(v1 + "", v2 + "");
        }
    }
    public void calculatePlacement() {
        if(graph == null || canvasController == null)
            return;
        int //total diameter or height/width
            shapeWidth = 30,
            shapeHeight = 30;
        width = canvasController.getWidth();
        height = canvasController.getHeight();
        double vert = 0, hor = 0; //x = horizontal, y = vertical
        int widthLim = width-shapeWidth;
        int heightLim = height-shapeHeight;
        int size = graph.getVertices().size();
        int rowCount = width/shapeWidth; //testing purposes for aligning objects in a row

        int count;
        for(int i = 0; i < size; i++) {
            count = 0;
            Shape circle = new Circle(0, 0, shapeWidth, shapeHeight);
            graph.getVertices().get(i).setValue(circle);
            circle.setValue(graph.getVertices().get(i).getLabel());
            boolean test1, test2, test3;
            do {
                if(count == 20) {
                    widthLim *= 1.5;
                    heightLim *= 1.5;
                    count = 0;
                }
                hor = random.nextInt(widthLim) + shapeWidth/2;
                vert = random.nextInt(heightLim) + shapeHeight/2;
                circle.setX((int)hor);
                circle.setY((int)vert);
                //hor  = ((i % rowCount) * diam) + radius;
                //vert = ((i / rowCount) * diam) + radius;
                //TODO: INSTEAD OF CALCULATING OPTIMAL PLACEMENT, RANDOMLY PLACE (NON-OVERLAPPING)
                test1 = false;//intersects(circle); //once placed, check if line interects
                test2 = false;//intersects(graph.getVertices().get(i)); //after placed, check if edges intersect
                test3 = false;//overlaps(circle);
            } while(test1 || test2 || test3);
        }
        //sort Graph (adjacency) lists
        //TODO: THEN SHIFT THE CIRCLE SUCH THAT THEY ARE CLOSE TO A MINIMUM AVERAGE DISTANCE
    }
    public void init() {
        //selfSort();
        injectEdgeShape();
        updateEdges();
    }

    private void injectEdgeShape() {
        for(Edge e : graph.getEdges())
            if(e.isDirected())
                e.setValue(new Arrow(0, 0, 0, 0));
            else
                e.setValue(new Line(0, 0, 0, 0));
    }

    //TODO: SORT INTO GROUPS WITH MINIMUM INTERSECTIONS
    public void selfSort() {
        int
            x, y,
            shapeWidth,
            shapeHeight;

        List<Vertex> degrees = getVertices()
                .stream()
                .sorted(Comparator.comparing(Vertex::getDegree))
                .collect(Collectors.toList());
        //parse each Circle/Vertex in the graph
        for(Vertex v : degrees) {
            Circle current = (Circle) v.getValue();
            ArrayList<Vertex> list = (ArrayList<Vertex>)v.getAdjacencyList();
            //skip nodes w/ no edge
            if(list.size() == 0)
                continue;
                //sort nodes with only 1 edge
            else if(list.size() == 1) {
                //TODO: REFACTOR TO CONSIDER ANY SHAPE BEING SELF-SORTED
                Circle end = (Circle) (list.get(0).getValue());
                x = end.getX();
                y = end.getY();
                shapeWidth = (int) end.getWidth();
                shapeHeight = (int) end.getHeight();
                double[] uvect = MyMath.getUnitVector(x, y, shapeWidth/2.0, shapeHeight/2.0);
                do {
                    x += (int)(uvect[0] * shapeWidth);
                    y += (int)(uvect[1] * shapeHeight);
                    current.setX(x);
                    current.setY(y);
                }while(overlaps(current));
            }
            //sort/replace from lower to higher degrees
            else {
                Vertex previous = null;

            }
        }
    }

    //--------------------------------
    private int width;
    private int height;

    private long seed = 1;
    private Random random = new Random(seed);

    //helper functions
    public void updateEdges() {
        for(Edge e : graph.getEdges()) {
            //if(e.getFrom().getValue() != null && e.getTo().getValue() != null)
            updateEdge(e);
        }
    }
    public void updateEdges(Vertex vertex) {
        if(vertex == null) return;
        for(Edge e : (ArrayList<Edge>)vertex.getAdjacencyEList()) {
            //if(e.getFrom().getValue() != null && e.getTo().getValue() != null)
            updateEdge(e);
        }
    }
    //main function
    public void updateEdge(Edge e) {
        Shape start = e.getFrom().getValue();
        Shape end = e.getTo().getValue();
        if(start == null || end == null)
            return;
        double
            hor = end.getX() - start.getX(),
            vert = end.getY() - start.getY(),
            distance = Math.sqrt(hor * hor + vert * vert),
            u_hor = hor / distance,
            u_vert = vert / distance,
            horShift = (int) (u_hor * start.getWidth()/2.0),
            vertShift = (int) (u_vert * start.getHeight()/2.0);
        e.setXStart(start.getX() + horShift);
        e.setYStart(start.getY() + vertShift);

        horShift = (int) (u_hor * end.getWidth()/2.0);
        vertShift = (int) (u_vert * end.getHeight()/2.0);
        e.setXEnd(end.getX() - horShift);
        e.setYEnd(end.getY() - vertShift);
    }
    public void updateNewEdge(Edge newEdge, int x, int y) {
        Shape start = newEdge.getFrom().getValue();
        if(start == null) return;
        double
            hor = x - start.getX(),
            vert = y - start.getY(),
            distance = Math.sqrt(hor*hor + vert*vert),
            u_hor = hor/distance,
            u_vert = vert/distance,
            horShift = (int) (u_hor * start.getWidth()/2.0),
            vertShift = (int) (u_vert * start.getHeight()/2.0);
        newEdge.setXStart(start.getX() + horShift);
        newEdge.setYStart(start.getY() + vertShift);

        newEdge.setXEnd(x);
        newEdge.setYEnd(y);
    }

    public boolean overlaps(Shape shape){
        for(int i = 0; i < graph.getVertices().size(); i++) {
            Shape temp = graph.getVertices().get(i).getValue();
            if(temp == null || temp == shape) continue;
            //if(MyMath.overlappingCircles(circle, temp))
            if(shape.distanceFromBounds(temp) < 0)
                return true;
        }
        return false;
    }

    public Item[] findItems(int x, int y) {
        List<Item> list = new ArrayList<>();
        list.addAll(findEdges(x, y));
        list.addAll(findNodes(x, y));
        Collections.sort(list);
        //convert List<Item> to Item[]
        Item[] array = new Item[list.size()];
        list.toArray(array);
        return array;
    }

    public List<Item> findEdges(int x, int y) {
        if(graph == null) return null;
        List<Item> items = new ArrayList<>();
        //x, y represents mouse click
        int pxThreshold = 4;
        int epsilon = 2;
        for(Edge e : graph.getEdges()) {
            double
                x1 = e.getXStart(),
                y1 = e.getYStart(),
                x2 = e.getXEnd(),
                y2 = e.getYEnd(),
                xPoints[] = {x1, x, x2},
                yPoints[] = {y1, y, y2};
            double distance = Math.round(MyMath.distancePointFromLine(xPoints, yPoints));
            //RANGE EXCLUSION
            if(!MyMath.isBetween(x1, x, x2, epsilon)) continue;
            else if(!MyMath.isBetween(y1, y, y2, epsilon)) continue;
            //DISTANCE
            if(distance <= pxThreshold) {
                items.add(new Item(e.getValue(), distance));
                Collections.sort(items);
                if(items.size() == 3) {
                    items.remove(2);
                }
            }
        }
        return items;
    }
    public List<Item> findNodes(int x, int y) {
        List<Item> items = new ArrayList<>();
        for(Vertex v : graph.getVertices()) {
            if(v.getValue() == null) continue;
            Circle current = (Circle)v.getValue();
            double distance = current.pointDistanceFromBounds(x, y);
            if(distance < 3) {
                items.add(new Item(current, distance));
                Collections.sort(items);
                if(items.size() == 3)
                    items.remove(2);
            }
        }
        return items;
    }

    //View <-> Controller data interaction (for single Circle instance)
    public Object getNode(int index) { return graph.getVertices().get(index).getValue(); }
    //View <-> Controller data interaction (for PaintComponent)
    public List<Vertex> getVertices() {
        if(graph == null)
            return null;
        return graph.getVertices();
    }
    public List<Edge> getEdges() {
        if(graph == null)
            return null;
        return graph.getEdges();
    }

    public Graph getGraph() { return graph; }
    public void debug() {
        System.out.println("Printing vertices");
        for(Vertex v : getVertices())
            System.out.println(v);
        System.out.println();

        System.out.println("Printing undirected edges");
        for(Edge e: getEdges())
            System.out.println(e);

        System.out.println("\nPrinting directed edges");
        for(Edge e: getEdges()) {
            if (e.isDirected())
                System.out.println(e);
            if(e.getFrom() == null)
                System.out.printf("from (%s) is null\n", e.getFrom());
            if(e.getTo() == null)
                System.out.printf("to (%s) is null\n", e.getTo());
        }
        System.out.println();
    }
    public void debugAdjacency(){
        System.out.println("Printing adjacency lists");
        for(Vertex v: graph.getVertices()) {
            System.out.println(v + " : " + v.getDegree());
            for(Vertex v2 : (ArrayList<Vertex>)v.getAdjacencyList()) {
                System.out.println("\t" + v2);
            }
        }

        System.out.println("\nPrinting Edge list");
        for(Edge e : graph.getEdges()) {
            if(e.isDirected())
                System.out.println(e);
        }
        int j = 0;
    }

    public void setGraph(Graph g) {
        graph = g;
    }
    public void resizeGraph(double mult) {
        if(Math.abs(mult) < 0.01)
            return;
        mult += 1;
        double
            shapeWidth,
            shapeHeight;
        if(graph == null) return;
        for(Vertex v : graph.getVertices()) {
            if(v.getValue() == null) continue;
            Shape current = v.getValue();
            shapeWidth = (current.getWidth() * mult);
            shapeHeight = (current.getHeight() * mult);
            current.setWidth((int)shapeWidth);
            current.setHeight((int)shapeHeight);
        }
        updateEdges();
    }

    public void rotateGraph(double pivotX, double pivotY, double alpha) {
        Shape shape;
        for (Vertex v : getVertices()) {
            shape = v.getValue();
            if (shape == null) continue;
            double
                    x2, y2, step[];
            x2 = shape.getX();
            y2 = shape.getY();
            step = rotateLineAbout(pivotX, pivotY, x2, y2, alpha);
            shape.setX((int)step[0]);
            shape.setY((int)step[1]);
        }
    }
    public void rotateGraphAveragePivot(double alpha) {
        List<Vertex> list = getVertices();
        //try and implement rotate plane
        double
                xPoints[] = new double[list.size()],
                yPoints[] = new double[list.size()];

        Circle circle;
        for (int i = 0; i < list.size(); i++) {
            circle = (Circle) list.get(i).getValue();
            if (circle == null) continue;
            xPoints[i] = circle.getX();
            yPoints[i] = circle.getY();
        }

        MyMath.rotatePlaneAbout(pivotX, pivotY, xPoints, yPoints, alpha);
        for (int j = 0; j < list.size(); j++) {
            circle = (Circle) list.get(j).getValue();
            circle.setX((int)xPoints[j]);
            circle.setY((int)yPoints[j]);
        }

    }
    private double pivotX, pivotY;
    public void calculateAveragePivot() {
        List<Vertex> list = getVertices();
        double
                xPoints[] = new double[list.size()],
                yPoints[] = new double[list.size()];

        Circle circle;
        double xAvg = 0,
                yAvg = 0;
        for (int i = 0; i < list.size(); i++) {
            circle = (Circle) list.get(i).getValue();
            if (circle == null) continue;
            xPoints[i] = circle.getX();
            yPoints[i] = circle.getY();
            xAvg += xPoints[i];
            yAvg += yPoints[i];
        }

        pivotX = xAvg / xPoints.length;
        pivotY = yAvg / yPoints.length;

    }

    public boolean testIntersection() {
        for(Edge e1 : graph.getEdges()) {
            for(Edge e2: graph.getEdges()) {
                Edge couple = graph.getEdgeCouple(e1.getLabel());
                if(e1 == e2 || couple == e2) continue;
                double
                    x1 = e1.getXStart(),
                    y1 = e1.getYStart(),
                    x2 = e1.getXEnd(),
                    y2 = e1.getYEnd(),
                    x3 = e2.getXStart(),
                    y3 = e2.getYStart(),
                    x4 = e2.getXEnd(),
                    y4 = e2.getYEnd(),
                    xPoints[] = { x1, x2, x3, x4 },
                    yPoints[] = { y1, y2, y3, y4 };
                if(MyMath.linesIntersect(xPoints, yPoints)) {
                    return true;
                }
            }
        }
        return false;
    }

    public double[] findIntersectionPoint(Edge e1, Edge e2) {
        //choose which vertex to  flip over line
        Edge min;
        int line1 = Math.min(e1.getFrom().getDegree(), e1.getTo().getDegree());
        int line2 = Math.min(e2.getFrom().getDegree(), e2.getTo().getDegree());

        Line pivot, flip;
        if(line1 < line2) {
            min = e1;
            flip = (Line) e1.getValue().copy();
            pivot = (Line) e2.getValue().copy();
        }
        else {
            min = e2;
            pivot = (Line) e1.getValue().copy();
            flip = (Line) e2.getValue().copy();
        }
        double
        //    xAvg = (e1Line.getX() + e1Line.getWidth() + e2Line.getX() + e1Line.getWidth())/4,
        //    yAvg = (e1Line.getY() + e1Line.getHeight() + e2Line.getY() + e1Line.getHeight())/4,
            angle = MyMath.normalizeAngle(pivot.getX(), pivot.getY(), flip.getWidth(), flip.getHeight()),
            results[] = new double[2];
        pivot.rotate(-angle, pivot.getX(), pivot.getY());
        flip.rotate(-angle, pivot.getX(), pivot.getY());

        double
            y1 = flip.getY(),
            y2 = flip.getHeight(),
            x,
            x1 = flip.getX(),
            x2 = flip.getWidth(),
            height = Math.abs(y2-y1),
            percentage;
        if(y1 - y2 < 0.1) {
            percentage = (pivotY-y1) / height;
            x = (x2-x1)*percentage + x1;
        }
        else if(y1 - y2 > 0.1){
            percentage = (pivotY-y2) / height;
            x = (x1-x2)*percentage + x1;
        }
        else {
            percentage = pivotY-y1;
            x = Math.abs(x2-x1) + x1;
        }
        results[0] = x;
        results[1] = pivot.getY(); //normalized pivotY

        Line temp = new Line((int)pivotX, (int)pivotY, (int)results[0], (int)results[1]);
        temp.rotate(angle, pivotX, pivotY);
        return new double[2];
    }

    public Vertex findVertex(double[] intersection, Edge e1, Edge e2) {
        double distance = Double.MAX_VALUE;
        Vertex[] vertices = { e1.getFrom(), e1.getTo(), e2.getFrom(), e2.getTo() };
        int index = 0;
        for(int i = 0; i < vertices.length; i++) {
            Shape temp = vertices[i].getValue();
            double dist = MyMath.calculateDistance(temp.getX(), temp.getY(), intersection[0], intersection[1]);
            if(dist < distance) {
                distance = dist;
                index = i;
            }
        }
        return vertices[index];
    }

    public void flipVertex(double[] intersection, Vertex v) {
        Shape circle = v.getValue();
        double
            x = circle.getX(),
            y = circle.getY(),
            dx = intersection[0] - circle.getX(),
            dy = intersection[1] - circle.getY();
        circle.setX((int)(x + 2*dx));
        circle.setY((int)(y + 2*dy));
    }

    public void testSelfSort() {
        boolean change;
        Vertex recent = null;
        GraphicsContext g = canvasController.canvas.getGraphicsContext2D();
        do {
            change = false;
            boolean flag = false;
            for(Edge e1 : graph.getEdges()) {
                if(flag) break;
                for(Edge e2: graph.getEdges()) {
                    Edge couple = graph.getEdgeCouple(e1.getLabel());
                    if(e1 == e2 || couple == e2) continue;
                    change = true;
                    flag = true;
                    double
                        x1 = e1.getXStart(),
                        y1 = e1.getYStart(),
                        x2 = e1.getXEnd(),
                        y2 = e1.getYEnd(),
                        x3 = e2.getXStart(),
                        y3 = e2.getYStart(),
                        x4 = e2.getXEnd(),
                        y4 = e2.getYEnd(),
                        xPoints[] = { x1, x2, x3, x4 },
                        yPoints[] = { y1, y2, y3, y4 };
                    if(MyMath.linesIntersect(xPoints, yPoints)) {
                        double[] intersection = findIntersectionPoint(e1, e2);
                        g.fillOval(intersection[0], intersection[1], 2, 2);
                        //TODO: find vertex to flip first, and then flip if it wasn't recently flipped or something?
                        //TODO: stop infinite loop
                        Vertex v = findVertex(intersection, e1, e2);
                        if(v != recent) {
                            flipVertex(intersection, v);
                            recent = v;
                        }
                    }
                }
            }
        }while(change);
    }
}

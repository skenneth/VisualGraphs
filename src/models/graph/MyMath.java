package models.graph;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import models.shapes.*;

public class MyMath {
    public static final double RadToDeg = (360/(2*Math.PI));
    public static final double DegToRad = (2*Math.PI)/360;

    public static boolean isBetween(double a, double b, double c, double epsilon) {
        if((a-epsilon <= b && b <= c+epsilon) || (c-epsilon <= b && b <= a+epsilon))
            return true;
        return false;
    }
    public static double distancePointFromLine(double[] xPoints, double[] yPoints) {
        double
            x1 = xPoints[0],
            x = xPoints[1],
            x2 = xPoints[2],
            y1 = yPoints[0],
            y = yPoints[1],
            y2 = yPoints[2],
            theta = Math.atan2((y2-y1), (x2-x1));//Math.atan2((y2-y1), (x2-x1)) * MyMath.RadToDeg;
        //normalize line slope and equation
        MyMath.rotatePlane(xPoints, yPoints, -theta);
        double d = Math.abs(yPoints[1] - yPoints[0]);
        return d;
    }

    public static double[] averageVector(double x, double y, double[] doubleXs, double[] doubleYs) {
        double[] vector = new double[2];

        return vector;
    }

    public static boolean overlappingCircles(Circle c1, Circle c2){
        boolean overlaps = false;
        double
            x1 = c1.getX(),
            y1 = c1.getY(),
            x2 = c2.getX(),
            y2 = c2.getY();
        //TODO: CONSIDER OVALS when calculating distances
        if(calculateDistance(x1, y1, x2, y2) < c1.getWidth()/2+c2.getHeight()/2)
            overlaps = true;
        return overlaps;
    }

    public static double calculateDistance(double x0, double y0, double x1, double y1) {
        double
            x2 = Math.pow(x1-x0, 2),
            y2 = Math.pow(y1-y0, 2),
            distance = Math.sqrt((x2 + y2));

        return distance;
    }

    public static double[] getUnitVector(double x1, double y1, double x2, double y2) {
        double[] vector = new double[2];
        double distance = calculateDistance(x1, y1, x2, y2);
        vector[0] = (x2-x1)/distance;
        vector[1] = (y2-y1)/distance;
        return vector;
    }
    public static int testCase = 0;

    public static boolean onAxis(double x, double y, double axis) {
        double angle = Math.atan2(y, x);
        double e = 0.0001;
        return (Math.abs(angle-axis) < e) || (Math.abs(angle-(axis+180)) < e);
    }

    public static boolean linesIntersect(double[] xPoints, double[] yPoints) {
        double
                x1 = xPoints[0], //line 1 start
                y1 = yPoints[0],
                x2 = xPoints[1], //line 1 end
                y2 = yPoints[1],
                x3 = xPoints[2], //line 2 start
                y3 = yPoints[2],
                x4 = xPoints[3], //line 2 end
                y4 = yPoints[3],
                temp1XPoints[] = {x1, x2, x3, x4},
                temp1YPoints[] = {y1, y2, y3, y4},
                line1x = x2-x1,
                line1y = y2-y1,
                line2x = x4-x3,
                line2y = y4-y3;
        //normalize first line
        double angle = Math.atan2(line1y, line1x);
        angle *= MyMath.RadToDeg;
        rotatePlane(temp1XPoints, temp1YPoints, -angle);
                y1 = temp1YPoints[0];
                y3 = temp1YPoints[2];
                y4 = temp1YPoints[3];
        if((y3 > y1 && y4 > y1) || (y3 < y1 && y4 < y1)) {
            return false;
        }

        x1 = xPoints[0]; //line 1 start
        y1 = yPoints[0];
        x2 = xPoints[1]; //line 1 end
        y2 = yPoints[1];
        x3 = xPoints[2]; //line 2 start
        y3 = yPoints[2];
        x4 = xPoints[3]; //line 2 end
        y4 = yPoints[3];
        double
                temp2NewXPoints[] = {x1, x2, x3, x4},
                temp2NewYPoints[] = {y1, y2, y3, y4};
        //normalize second line
        angle = Math.atan2(line2y, line2x);
        angle *= MyMath.RadToDeg;
        rotatePlane(temp2NewXPoints, temp2NewYPoints, -angle);
                y1 = temp2NewYPoints[0];
                y2 = temp2NewYPoints[1];
                y3 = temp2NewYPoints[2];
        if((y1 > y3 && y2 > y3) ||(y1 < y3 && y2 < y3)) {
            return false;
        }
        else
            return true;
    }
    public static boolean linesIntersectsCircle(Circle circle, int x1, int y1, int x2, int y2) {
        double
            x0 = circle.getX(),
            y0 = circle.getY(),
            height = circle.getWidth()/2,
            width = circle.getHeight()/2,
            numerator, denominator, distance;

        numerator = (y2-y1)*x0 - (x2-x1)*y0 + x2*y1 - y2*x1;
        numerator = Math.abs(numerator);
        denominator = Math.pow(y2-y1, 2) + Math.pow(x2-x1, 2);
        denominator = Math.sqrt(denominator);
        distance = Math.abs(numerator/denominator);
        return distance < width;
    }

    public static void rotatePlane(double[] xPoints, double[] yPoints, double angle) {
        double
            temp[],
            coords[][] = new double[2][1],
            pivotX=0, pivotY=0;

        int i;
        for(i = 0; i < xPoints.length; i++) {
            pivotX += xPoints[i];
            pivotY += yPoints[i];
        }
        pivotX /= i;
        pivotY /= i;

        for(i = 0; i < xPoints.length; i++) {
            temp = rotatePointAbout(pivotX, pivotY, xPoints[i], yPoints[i], angle);
            xPoints[i] = temp[0];
            yPoints[i] = temp[1];
        }
    }
    public static void rotatePlaneAbout(double pivotX, double pivotY, double[] xPoints, double[] yPoints, double angle) {
        double temp[];
        for(int i = 0; i < xPoints.length; i++) {
            temp = rotateLineAbout(pivotX, pivotY, xPoints[i], yPoints[i], angle);
            xPoints[i] = temp[0];
            yPoints[i] = temp[1];
        }
    }
    public static double[] rotatePointAbout(double x1, double y1, double x2, double y2, double alpha) {
        double
            coords[] = new double[2],
            x = x2-x1,
            y = y2-y1,
            degrees = Math.atan2(y,x),
            distance = Math.sqrt(x*x + y*y),
            theta = degrees;
        alpha = DegToRad*(alpha);
        theta += alpha;
        x = distance * Math.cos(theta);
        y = distance * Math.sin(theta);
        coords[0] = (x1 + x);
        coords[1] = (y1 + y);
        return coords;
    }
    public static double[] rotateLineAbout(double x1, double y1, double x2, double y2, double alpha) {
        double
            coords[] = new double[2],
            x = x2-x1,
            y = y2-y1,
            degrees = Math.atan2(y,x),
            distance = Math.sqrt(x*x + y*y),
            theta = degrees;
            alpha = DegToRad*(alpha);
            theta += alpha;
            x = distance * Math.cos(theta);
            y = distance * Math.sin(theta);
            coords[0] = (x1 + x);
            coords[1] = (y1 + y);
            return coords;
    }
}

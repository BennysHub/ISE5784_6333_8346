package renderer.super_sampling;

import primitives.Point;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;
import static primitives.Util.alignZero;
import static primitives.Util.random;

public final class Blackboard {

    public static List<Point> getPointsOnCircle
            (Vector normal, Point center, double radius, int numOfPoints) {

        if (alignZero(radius) <= 0)
            return List.of(center);

        Vector right;
        Vector up;
        try {
            right = new Vector(0, 1, 0).crossProduct(normal).normalize();
        } catch (IllegalArgumentException ignore) {
            right = new Vector(0, 0, 1).crossProduct(normal).normalize();
        }
        up = right.crossProduct(normal).normalize();//already normalized


        int cellsInRow = (int) sqrt(numOfPoints);
        double cellVertexSize = radius * 2 / cellsInRow;
        LinkedList<Point> pointsOnArea = new LinkedList<>();

//        int dotsPerAxis = (int) sqrt(numOfVectors);
//        double halfGridDistance = size / dotsPerAxis;
//        for (double i = -size; i < size; i += halfGridDistance * 2) {
//            for (double j = -size; j < size; j += halfGridDistance * 2) {

        for (int i = 0; i < cellsInRow; i++) {
            for (int j = 0; j < cellsInRow; j++) {
                double x = -(i - (cellsInRow - 1) / 2d) * cellVertexSize;
                double y = (j - (cellsInRow - 1) / 2d) * cellVertexSize;

                x += random(-cellVertexSize/2, cellVertexSize/2);
                y += random(-cellVertexSize/2, cellVertexSize/2);

                double distanceSquared = x * x + y * y;
                Point point = center;
                if (distanceSquared <= radius * radius) {
                    if (x != 0) point = point.add(right.scale(x));
                    if (y != 0) point = point.add(up.scale(y));
                    pointsOnArea.add(point);
                }
            }
        }
        return pointsOnArea;
    }
}

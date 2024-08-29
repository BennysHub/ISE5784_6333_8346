package renderer.super_sampling;

import primitives.Point;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;
import static primitives.Util.*;

/**
 * The {@code Blackboard} class provides utility methods for generating random points within geometric shapes.
 */
public final class Blackboard {
    /**
     * Generates a list of points distributed randomly within a circular area on a plane defined by a normal vector.
     * The points are generated within a square grid of cells, with each point randomly jittered within its cell.
     *
     * @param normal      the normal vector defining the plane on which the circle lies.
     * @param center      the center point of the circle.
     * @param radius      the radius of the circle.
     * @param numOfPoints the number of points to generate.
     * @return a list of points distributed within the circle. If the radius is non-positive, a list containing only the center is returned.
     * @throws IllegalArgumentException if the normal vector is invalid.
     */
    public static List<Point> getPointsOnCircle(Vector normal, Point center, double radius, int numOfPoints) {

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

        LinkedList<Point> pointsOnArea = new LinkedList<>();
//        int dotsPerAxis = (int) sqrt(numOfPoints);
//        double halfGridDistance = radius / dotsPerAxis;
//        for (double i = -radius; i < radius; i += halfGridDistance * 2) {
//            for (double j = -radius; j < radius; j += halfGridDistance * 2) {
//                double x = i;
//                double y = j;
//                System.out.println(x + " " + y);
//                x += random(-halfGridDistance, halfGridDistance);
//                y += random(-halfGridDistance, halfGridDistance);

        int cellsInRow = (int) sqrt(numOfPoints);
        double cellVertexSize = radius * 2 / cellsInRow;
        for (int i = 0; i < cellsInRow; i++) {
            for (int j = 0; j < cellsInRow; j++) {
                double x = -(i - (cellsInRow - 1) / 2d) * cellVertexSize;
                double y = (j - (cellsInRow - 1) / 2d) * cellVertexSize;

                x += random(-cellVertexSize / 2, cellVertexSize / 2);
                y += random(-cellVertexSize / 2, cellVertexSize / 2);

                double distanceSquared = x * x + y * y;
                Point point = center;
                if (distanceSquared <= radius * radius) {
                    if (!isZero(x)) point = point.add(right.scale(x));
                    if (!isZero(y)) point = point.add(up.scale(y));
                    pointsOnArea.add(point);
                }
            }
        }
        return pointsOnArea;
    }
}

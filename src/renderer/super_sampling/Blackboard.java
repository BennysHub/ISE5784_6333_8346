package renderer.super_sampling;

import primitives.Double2;
import primitives.Matrix;
import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;
import static primitives.Util.isZero;
import static primitives.Util.random;

/**
 * The {@code Blackboard} class provides utility methods for generating random points within geometric shapes.
 */
public final class Blackboard {


    public static Double2[] GRID_1_169 = generateSquareGrid(1, 169);
    public static Double2[] CIRCLE_1_169 = warpToCircle2(GRID_1_169, 1, 1);


    public static Double2[] generateSquareGrid(double gridSize, int numOfPoints) {
        return generateSquareGrid(gridSize, Double2.ZERO, numOfPoints);
    }

    public static Double2[] generateSquareGrid(double gridSize, Double2 center, int numOfPoints) {
        int pointsPerSide = (int) sqrt(numOfPoints);
        Double2[] grid = new Double2[pointsPerSide * pointsPerSide];
        double distanceRation = gridSize / pointsPerSide;
        for (int i = 0; i < pointsPerSide; i++) {
            for (int j = 0; j < pointsPerSide; j++) {
                double x = -(i - (pointsPerSide - 1) / 2d) * distanceRation;
                double y = (j - (pointsPerSide - 1) / 2d) * distanceRation;

                grid[i * pointsPerSide + j] = new Double2(x + center.x(), y + center.y());
            }
        }
        return grid;
    }


    public static Double2[] applyJitter(Double2[] points, double jitter) {
        for (int i = 0; i < points.length; i++) {
            double jitterX = random(-jitter, jitter);
            double jitterY = random(-jitter, jitter);
            points[i] = new Double2(points[i].x() + jitterX, points[i].y() + jitterY);
        }
        return points;
    }

    public static Point[] convertTo3D(Double2[] points, Point center, Vector right, Vector up) {
        right = right.normalize();
        up = up.normalize();
        Point[] points3D = new Point[points.length];
        for (int i = 0; i < points.length; i++) {

            double u = points[i].x();
            double v = points[i].y();

            double x = center.getX() + u * right.getX() + v * up.getX();
            double y = center.getY() + u * right.getY() + v * up.getY();
            double z = center.getZ() + u * right.getZ() + v * up.getZ();

            points3D[i] = new Point(x, y, z);
        }
        return points3D;
    }


    public static Point[] addSphereDepth(Point[] points, Point sphereCenter, double sphereRadius, Vector normal) {
        /*
        take a sample of point within a plane where the plane center is the sphere center and take normal vector of the plane
        the function moves the points from the disk in the direction of the normal until they reach the sphere
        Note make sure that the sphere radius is >= plane (max) radius, meaning it could fit inside the sphere.
        */
        normal = normal.normalize();
        Point[] spherePoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            double xSquared = points[i].distanceSquared(sphereCenter);

            double ySquared = Math.pow(sphereRadius, 2) - xSquared;
            double y = Math.sqrt(ySquared);
            if (!isZero(y))
                spherePoints[i] = points[i].add(normal.scale(y));
            else
                spherePoints[i] = points[i];
        }
        return spherePoints;
    }

    public static Double2[] scale(Double2[] points, double num) {
        int length = points.length;
        Double2[] scaledPoints = new Double2[length];
        for (int i = 0; i < length; i++) {
            scaledPoints[i] = points[i].scale(num);
        }
        return scaledPoints;
    }


    public static Point[] applyJitterToSphere(Point[] points, Point sphereCenter, double radius, double jitter) {
        return null;
    }


    //takes a square sample of point the function scale and warp the point to fit inside a disk with given radius
    public static Double2[] warpToCircle1(Double2[] squarePoints, Double2 center, double sideLength, double diskRadius) {
        Double2[] warpedPoints = new Double2[squarePoints.length];

        for (int i = 0; i < squarePoints.length; i++) {
            //normalize points to be withing [-1, 1]
            double normX = (squarePoints[i].x() - center.x()) / sideLength * 2.0;
            double normY = (squarePoints[i].y() - center.y()) / sideLength * 2.0;
            //convert to Cartesian coordinates
            double r = Math.sqrt(normX * normX + normY * normY);
            double theta = Math.atan2(normY, normX);
            //adjust point radius to fit in disk radius

            //option 1 less edge points
            //double rPrime = (r / Math.sqrt(2)) * diskRadius;

            //option2 2 more edge points
            double rPrime = Math.min(r, 1) * diskRadius;
            double xPrime = rPrime * Math.cos(theta);
            double yPrime = rPrime * Math.sin(theta);
            warpedPoints[i] = new Double2(xPrime + center.x(), yPrime + center.y());
        }
        return warpedPoints;
    }

    //assume the square center is at the origin
    public static Double2[] warpToCircle1(Double2[] squarePoints, double sideLength, double diskRadius) {
        return warpToCircle1(squarePoints, Double2.ZERO, sideLength, diskRadius);
    }

    //assume the square center is at the origin
    public static Double2[] warpToCircle2(Double2[] squarePoints, Double2 center, double sideLength, double radius) {
        Double2[] circlePoints = new Double2[squarePoints.length];

        for (int i = 0; i < squarePoints.length; i++) {
            // Normalize coordinates to [-1, 1]
            double normX = (squarePoints[i].x() - center.x()) / sideLength * 2.0;
            double normY = (squarePoints[i].y() - center.y()) / sideLength * 2.0;

            // Map to circle using inverse transformation
            double circleX = normX * Math.sqrt(1 - (normY * normY / 2));
            double circleY = normY * Math.sqrt(1 - (normX * normX / 2));

            // Scale back to original size
            circleX *= radius;
            circleY *= radius;

            circlePoints[i] = new Double2(circleX + center.x(), circleY + center.y());
        }
        return circlePoints;
    }

    public static Double2[] warpToCircle2(Double2[] squarePoints, double sideLength, double radius) {
        return warpToCircle2(squarePoints, Double2.ZERO, sideLength, radius);
    }

    public static Point[] rotatePointsOnSphere(Point[] points, Vector originalNormal, Vector newNormal, Point center) {
        Point[] transformedPoints = new Point[points.length];

        // Calculate the rotation matrix
        Vector axis = originalNormal.parallel(newNormal) ? originalNormal.perpendicular() : originalNormal.crossProduct(newNormal);

        double angle = Math.acos(originalNormal.dotProduct(newNormal) / (originalNormal.length() * newNormal.length()));
        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);

        // Apply the rotation to each point
        for (int i = 0; i<points.length; i++) {
            Vector translatedPoint = points[i].subtract(center);
            Vector rotatedPoint = rotationMatrix.multiply(translatedPoint);
            Point finalPoint = center.add(rotatedPoint);
            transformedPoints[i]  = finalPoint;
        }
        return transformedPoints;
    }

    static public Point[] getDiskPoints(Point center, double radius, Vector normal) {
        Vector right = normal.perpendicular();
        Vector up = normal.crossProduct(right);
        return convertTo3D(scale(CIRCLE_1_169, radius), center, right, up);
    }

    static public Point[] getSpherePoints(Point center, double radius, Vector normal) {
        Vector right = normal.perpendicular();
        Vector up = normal.crossProduct(right);
        var a = convertTo3D(scale(CIRCLE_1_169, radius), center, right, up);
        return addSphereDepth(a, center, radius, normal);
    }
}

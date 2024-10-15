package renderer.super_sampling;

import primitives.*;
import renderer.RenderSettings;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.sqrt;
import static primitives.Util.*;

/**
 * The {@code Blackboard} class provides utility methods for generating random points within geometric shapes.
 */
public final class Blackboard {


    //    public static List<Point2D> circleSample = null;
//    public static List<Point2D> squareSample = null;
    public static Point2D[] grid169 = generateGrid(1, 169);

    public static List<Point> getPointsOnSphere(Vector normal, Point center, double radius, int numOfPoints) {
        if (alignZero(radius) <= 0 || numOfPoints == 1)
            return List.of(center);

        Vector right = normal.perpendicular();
        Vector up = right.crossProduct(normal);

        List<Point> pointsOnArea = new ArrayList<>(numOfPoints);


        int cellsInRow = (int) sqrt(numOfPoints);
        double cellVertexSize = radius * 2 / cellsInRow;
        for (int i = 0; i < cellsInRow; i++) {
            for (int j = 0; j < cellsInRow; j++) {
                double x = -(i - (cellsInRow - 1) / 2d) * cellVertexSize;
                double y = (j - (cellsInRow - 1) / 2d) * cellVertexSize;

                x += random(-cellVertexSize / 2, cellVertexSize / 2);
                y += random(-cellVertexSize / 2, cellVertexSize / 2);

//                Point warpedPoint = warpSquareToCircle(x, y, radius, right, up, center);
//                pointsOnArea.add(warpedPoint);

                double distanceSquared = x * x + y * y;
                Point point = center;
                if (distanceSquared <= radius * radius) {
                    if (!isZero(x)) point = point.add(right.scale(x));
                    if (!isZero(y)) point = point.add(up.scale(y));


                    double distanceFromCenter = point.distance(center);
                    double radiusMinusDistanceFromCenter = alignZero(radius - distanceFromCenter);

                    if (radiusMinusDistanceFromCenter > 0)
                        point = point.add(normal.scale(radiusMinusDistanceFromCenter));
                    pointsOnArea.add(point);
                }
            }
        }
        return pointsOnArea;
    }

    public static Point2D[] generateGrid(int gridSize, int numOfPoints) {
        int pointsPerRow = (int) sqrt(numOfPoints);
        Point2D[] grid = new Point2D[pointsPerRow * pointsPerRow];
        double distanceRation = (double) gridSize / pointsPerRow;
        for (int i = 0; i < pointsPerRow; i++) {
            for (int j = 0; j < pointsPerRow; j++) {
                double x = -(i - (pointsPerRow - 1) / 2d) * distanceRation;
                double y = (j - (pointsPerRow - 1) / 2d) * distanceRation;
                grid[i * pointsPerRow + j] = new Point2D(x, y);
            }
        }
        return grid;
    }


    public static Point2D[] applyJitter(Point2D[] points, double jitter) {
        for (int i = 0; i < points.length; i++) {
            double jitterX = random(-jitter, jitter);
            double jitterY = random(-jitter, jitter);
            points[i] = new Point2D(points[i].getX() + jitterX, points[i].getY() + jitterY);
        }
        return points;
    }


    public static Point2D[] warpToDisk(Point2D[] grid, double radius) {
        int size = (int) Math.sqrt(grid.length);
        Point2D[] diskPoints = new Point2D[grid.length];
        for (int i = 0; i < grid.length; i++) {
            double theta = 2 * Math.PI * grid[i].getX() / size;
            double r = radius * Math.sqrt(grid[i].getY() / size);
            double x = r * Math.cos(theta);
            double y = r * Math.sin(theta);
            diskPoints[i] = new Point2D(x, y);
        }
        return diskPoints;
    }

    public static Point[] convertTo3D(Point2D[] diskPoints, Point center, Vector right, Vector up) {

        Point[] points3D = new Point[diskPoints.length];
        for (int i = 0; i < diskPoints.length; i++) {
            Point point = center;
            if (!isZero(diskPoints[i].getX())) point = point.add(right.scale(diskPoints[i].getX()));
            if (!isZero(diskPoints[i].getY())) point = point.add(up.scale(diskPoints[i].getY()));
            points3D[i] = point;
        }
        return points3D;
    }

    public static Point[] addSphereDepth(Point[] points, Point sphereCenter, double sphereRadius, Vector normal) {
        Point[] spherePoints = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            double distanceFromCenter = points[i].distance(sphereCenter);
            double radiusMinusDistanceFromCenter = alignZero(sphereRadius - distanceFromCenter);

            if (radiusMinusDistanceFromCenter > 0)
                spherePoints[i] = points[i].add(normal.scale(radiusMinusDistanceFromCenter));
            else
                spherePoints[i] = points[i];
        }
        return spherePoints;
    }


    public static List<Ray> constructRays(Point[] points, Point p) {
        List<Ray> rays = new ArrayList<>(points.length);
        for (Point point : points)
            rays.add(new Ray(point, p.subtract(point)));
        return rays;
    }


    public static List<Ray> constructRays(List<Point> points, Point p) {
        List<Ray> rays = new ArrayList<>(points.size());
        for (Point point : points)
            rays.add(new Ray(point, p.subtract(point)));
        return rays;
    }

    public static Point[] applyJitterToSphere(Point[] points, Point sphereCenter, double radius ,double jitter){
        return null;
    }



//    public static Point[] convertTo3D(Point2D[] diskPoints, Point center, Vector right, Vector up) {
//        Point normal = right.crossProduct(up);
//
//        double[][] transformationData = {
//                {right.getX(), up.getX(), normal.getX()},
//                {right.getY(), up.getY(), normal.getY()},
//                {right.getZ(), up.getZ(), normal.getZ()}
//        };
//        Matrix transformationMatrix = new Matrix(transformationData);
//
//        Point[] points3D = new Point[diskPoints.length];
//        for (int i = 0; i < diskPoints.length; i++) {
//            Vector pointData = new Vector(diskPoints[i].getX(), diskPoints[i].getY(), 1);
//            Vector transformedPoint = transformationMatrix.multiply(pointData);
//            points3D[i] = center.add(transformedPoint);
//        }
//        return points3D;
//    }




//    public static List<Point2D> warpToDisk(List<Point2D> points, double diskRadius) {
//        List<Point2D> warpedPoints = new ArrayList<>();
//
//        for (Point2D point : points) {
//            double x = point.getX();
//            double y = point.getY();
//            double r = Math.sqrt(x * x + y * y);
//            double theta = Math.atan2(y, x);
//            double rPrime = Math.sqrt(r);
//            double xPrime = rPrime * Math.cos(theta) * diskRadius;
//            double yPrime = rPrime * Math.sin(theta) * diskRadius;
//            warpedPoints.add(new Point2D(xPrime, yPrime));
//        }
//
//        return warpedPoints;
//    }
//
//    // can be improved by adding to point a bit at a time to center instead of adding to center each time
//    public static List<Point> to3D(List<Point2D> twoDPoints, Vector right, Vector up, Point center) {
//        List<Point> threeDPoints = new ArrayList<>(RenderSettings.getShadowRaysSampleCount());
//        Point point3D = center;
//        for (Point2D point2D : twoDPoints) {
//            if (!isZero(point2D.getX()))
//                point3D = point3D.add(right.scale(point2D.getX()));
//            if (!isZero(point2D.getY()))
//                point3D = point3D.add(up.scale(point2D.getY()));
//
//            threeDPoints.add(point3D);
//        }
//        return threeDPoints;
//    }
//
//    public static List<Point> convert2DTo3D(List<Point2D> points2D, Point center, Vector right, Vector up) {
//        List<Point> points3D = new ArrayList<>();
//
//        for (Point2D point2D : points2D) {
//            double u = point2D.getX();
//            double v = point2D.getY();
//
//            double x = center.getX() + u * right.getX() + v * up.getX();
//            double y = center.getY() + u * right.getY() + v * up.getY();
//            double z = center.getZ() + u * right.getZ() + v * up.getZ();
//
//            points3D.add(new Point(x, y, z));
//        }
//        return points3D;
//    }
//
//    public static List<Point> addSphereDepth(List<Point> points, Point sphereCenter, double sphereRadius, Vector normal) {
//        List<Point> spherePoints = new ArrayList<>();
//        for (Point point : points) {
//            double distanceFromCenter = point.distance(sphereCenter);
//            double radiusMinusDistanceFromCenter = alignZero(sphereRadius - distanceFromCenter);
//
//            if (radiusMinusDistanceFromCenter > 0)
//                spherePoints.add(point.add(normal.scale(radiusMinusDistanceFromCenter)));
//            else
//                spherePoints.add(point);
//        }
//        return spherePoints;
//    }
//
//
//    public static List<Point> getSpherePoints(Point sphereCenter, double sphereRadius, int numOfPoints, Vector right, Vector up) {
//        List<Point2D> distPoints = generateJitteredGrid(numOfPoints, 0);
//        distPoints = warpToDisk(distPoints, sphereRadius);
//        List<Point> spherePoints = convert2DTo3D(distPoints, sphereCenter, right, up);
//        return addSphereDepth(spherePoints, sphereCenter, sphereRadius, right.crossProduct(up));
//    }
//
//    public static List<Point2D> circleScale(double scaleFactor) {
//        List<Point2D> scaledCircleSample = new ArrayList<>(RenderSettings.getShadowRaysSampleCount());
//        for (Point2D point2D : circleSample)
//            scaledCircleSample.add(point2D.scale(scaleFactor));
//        return scaledCircleSample;
//    }
//
//
//    public static List<Point> rotatePointsOnSphere(List<Point> points, Vector originalNormal, Vector newNormal, Point center) {
//        List<Point> transformedPoints = new ArrayList<>();
//
//        // Calculate the rotation matrix
//        Vector axis = originalNormal.crossProduct(newNormal);
//        double angle = Math.acos(originalNormal.dotProduct(newNormal) / (originalNormal.length() * newNormal.length()));
//        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);
//
//        // Apply the rotation to each point
//        for (Point point : points) {
//            Vector translatedPoint = point.subtract(center);
//            Vector rotatedPoint = rotationMatrix.multiply(translatedPoint);
//            Point finalPoint = center.add(rotatedPoint);
//            transformedPoints.add(finalPoint);
//        }
//
//        return transformedPoints;
//    }
//
//
//    public static List<Point> scalePoints(List<Point> points, double oldRadius, double newRadius) {
//        double scaleFactor = newRadius / oldRadius;
//        List<Point> newSpherePoints = new ArrayList<>(points.size());
//        for (Point point : points)
//            newSpherePoints.add(new Point(point.getX() * scaleFactor, point.getY() * scaleFactor, point.getZ() * scaleFactor));
//        return newSpherePoints;
//    }
//
//    public static List<Point> addPoint(List<Point> points, Point newPoint) {
//        List<Point> newPoints = new ArrayList<>(points.size());
//        for (Point point : points)
//            newPoints.add(new Point(point.getX() + newPoint.getX(), point.getY() + newPoint.getY(), point.getZ() + newPoint.getZ()));
//        return newPoints;
//    }
//
//    public static List<Point> getSphereSampleWithZNormal(Point center, double radius) {
//        List<Point> newPoints = new ArrayList<>(sphereSample.size());
//        for (Point point : sphereSample)
//            newPoints.add(new Point(point.getX() * radius + center.getX(), point.getY() * radius + center.getY(), point.getZ() * radius + center.getZ()));
//        return newPoints;
//    }
}

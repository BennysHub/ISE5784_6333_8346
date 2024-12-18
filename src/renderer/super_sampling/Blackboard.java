package renderer.super_sampling;

import primitives.Double2;
import primitives.Matrix;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.sqrt;
import static primitives.Util.isZero;
import static primitives.Util.random;

/**
 * The {@code Blackboard} class provides utility methods for generating and transforming sample points
 * within various geometric shapes. These samples are used in rendering techniques such as depth of field,
 * soft shadows, and antialiasing.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Generate evenly spaced grid points.</li>
 *   <li>Warp grid points to fit within circles or spheres.</li>
 *   <li>Apply transformations such as jittering and rotation.</li>
 *   <li>Precompute and retrieve sampling points at different quality levels.</li>
 * </ul>
 *
 * <p>Supported Quality Levels:</p>
 * <ul>
 *   <li>{@link QualityLevel#LOW}</li>
 *   <li>{@link QualityLevel#MEDIUM}</li>
 *   <li>{@link QualityLevel#HIGH}</li>
 *   <li>{@link QualityLevel#ULTRA}</li>
 * </ul>
 *
 * @author Benny Avrahami
 */
public final class Blackboard {

    // Constants for sample counts at different quality levels
    private static final int SAMPLE_COUNT_LOW = 36;
    private static final int SAMPLE_COUNT_MEDIUM = 64;
    private static final int SAMPLE_COUNT_HIGH = 100;
    private static final int SAMPLE_COUNT_ULTRA = 169;

    // Precomputed sample points for different shapes and quality levels
    private static final Map<QualityLevel, Double2[]> GRID_MAP = new HashMap<>(4);
    private static final Map<QualityLevel, Double2[]> CIRCLE_MAP = new HashMap<>(4);
    private static final Map<QualityLevel, Point[]> SPHERE_MAP = new HashMap<>(4);

    static {
        // Precompute square grids
        GRID_MAP.put(QualityLevel.LOW, generateSquareGrid(1, SAMPLE_COUNT_LOW));
        GRID_MAP.put(QualityLevel.MEDIUM, generateSquareGrid(1, SAMPLE_COUNT_MEDIUM));
        GRID_MAP.put(QualityLevel.HIGH, generateSquareGrid(1, SAMPLE_COUNT_HIGH));
        GRID_MAP.put(QualityLevel.ULTRA, generateSquareGrid(1, SAMPLE_COUNT_ULTRA));

        // Warp grids to circles
        CIRCLE_MAP.put(QualityLevel.LOW, warpToCircle2(GRID_MAP.get(QualityLevel.LOW), 1, 1));
        CIRCLE_MAP.put(QualityLevel.MEDIUM, warpToCircle2(GRID_MAP.get(QualityLevel.MEDIUM), 1, 1));
        CIRCLE_MAP.put(QualityLevel.HIGH, warpToCircle2(GRID_MAP.get(QualityLevel.HIGH), 1, 1));
        CIRCLE_MAP.put(QualityLevel.ULTRA, warpToCircle2(GRID_MAP.get(QualityLevel.ULTRA), 1, 1));

        // Generate Fibonacci spheres
        SPHERE_MAP.put(QualityLevel.LOW, generateFibonacciSphere(Point.ZERO, 1, SAMPLE_COUNT_LOW));
        SPHERE_MAP.put(QualityLevel.MEDIUM, generateFibonacciSphere(Point.ZERO, 1, SAMPLE_COUNT_MEDIUM));
        SPHERE_MAP.put(QualityLevel.HIGH, generateFibonacciSphere(Point.ZERO, 1, SAMPLE_COUNT_HIGH));
        SPHERE_MAP.put(QualityLevel.ULTRA, generateFibonacciSphere(Point.ZERO, 1, SAMPLE_COUNT_ULTRA));
    }

    private Blackboard() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a square grid of points.
     *
     * @param gridSize    The size of the grid.
     * @param numOfPoints The total number of points in the grid.
     * @return An array of {@link Double2} representing the grid points.
     */
    public static Double2[] generateSquareGrid(double gridSize, int numOfPoints) {
        return generateSquareGrid(gridSize, Double2.ZERO, numOfPoints);
    }

    /**
     * Generates a square grid of points centered at a given point.
     *
     * @param gridSize    The size of the grid.
     * @param center      The center of the grid.
     * @param numOfPoints The total number of points in the grid.
     * @return An array of {@link Double2} representing the grid points.
     */
    public static Double2[] generateSquareGrid(double gridSize, Double2 center, int numOfPoints) {
        int pointsPerSide = (int) sqrt(numOfPoints);
        Double2[] grid = new Double2[pointsPerSide * pointsPerSide];
        double spacing = gridSize / pointsPerSide;

        for (int i = 0; i < pointsPerSide; i++) {
            for (int j = 0; j < pointsPerSide; j++) {
                double x = -(i - (pointsPerSide - 1) / 2.0) * spacing;
                double y = (j - (pointsPerSide - 1) / 2.0) * spacing;

                grid[i * pointsPerSide + j] = new Double2(x + center.x(), y + center.y());
            }
        }
        return grid;
    }

    /**
     * Applies jitter to a set of 2D points.
     *
     * @param points The array of {@link Double2} points.
     * @param jitter The maximum jitter amount for each axis.
     * @return A new array of jittered points.
     */
    public static Double2[] applyJitter(Double2[] points, double jitter) {
        Double2[] jitteredPoints = new Double2[points.length];
        for (int i = 0; i < points.length; i++) {
            double jitterX = random(-jitter, jitter);
            double jitterY = random(-jitter, jitter);
            jitteredPoints[i] = new Double2(points[i].x() + jitterX, points[i].y() + jitterY);
        }
        return jitteredPoints;
    }

    /**
     * Converts 2D points to 3D points on a plane defined by a normal vector and center.
     *
     * @param points  The 2D points.
     * @param center  The center of the plane.
     * @param normal  The normal vector of the plane.
     * @return An array of 3D points.
     */
    public static Point[] convertTo3D(Double2[] points, Point center, Vector normal) {
        Vector right = normal.perpendicular();
        Vector up = normal.crossProduct(right).normalize();

        Point[] points3D = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            double u = points[i].x();
            double v = points[i].y();
            points3D[i] = center.add(right.scale(u)).add(up.scale(v));
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
            if (!isZero(y)) spherePoints[i] = points[i].add(normal.scale(y));
            else spherePoints[i] = points[i];
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

    public static Point[] movePoints(Point[] points  ,Vector move){
        int numOfPoints = points.length;
        Point[] transformedPoints = new Point[numOfPoints];

        for (int i = 0; i<numOfPoints; i++)
            transformedPoints[i] = points[i].add(move);

        return transformedPoints;
    }


    public static Point[] applyFastJitter(Point[] points, double radius, double jitter) {
        return null;
    }

    public static Double2[] applyFastJitter(Double2[] points, double radius, double jitter) {
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

            //option #1 less edge points
            //double rPrime = (r / Math.sqrt(2)) * diskRadius;

            //option2 #2 more-edge points
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
        Vector axis = originalNormal.isParallel(newNormal) ? originalNormal.perpendicular() : originalNormal.crossProduct(newNormal);

        double angle = Math.acos(originalNormal.dotProduct(newNormal) / (originalNormal.length() * newNormal.length()));
        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);

        // Apply the rotation to each point
        for (int i = 0; i < points.length; i++) {
            Vector translatedPoint = points[i].subtract(center);
            Vector rotatedPoint = rotationMatrix.multiply(translatedPoint);
            Point finalPoint = center.add(rotatedPoint);
            transformedPoints[i] = finalPoint;
        }
        return transformedPoints;
    }

    /**
     * Retrieves precomputed circle points scaled to a specific radius.
     *
     * @param radius        The desired radius of the circle.
     * @param qualityLevel  The sampling quality level.
     * @return An array of {@link Double2} points representing the circle.
     */
    public static Double2[] getCirclePoints(double radius, QualityLevel qualityLevel) {
        return scale(CIRCLE_MAP.get(qualityLevel), radius);
    }

    static public Point[] getDiskPoints(Point center, double radius, Vector normal, QualityLevel qualityLevel) {
        return convertTo3D(scale(CIRCLE_MAP.get(qualityLevel), radius), center, normal);
    }

    static public Point[] getHalfSpherePoints(Point center, double radius, Vector normal, QualityLevel qualityLevel) {
        var a = convertTo3D(scale(CIRCLE_MAP.get(qualityLevel), radius), center, normal);
        return addSphereDepth(a, center, radius, normal);
    }

    static public Point[] getSpherePoints(Point center, double radius, QualityLevel qualityLevel) {

        Point[] unitSpherePoints = SPHERE_MAP.get(qualityLevel);
        Point[] spherePoints = new Point[unitSpherePoints.length];

        for (int i = 0; i < unitSpherePoints.length; i++) {
            Point unitSpherePoint = unitSpherePoints[i];
            spherePoints[i] = new Point(unitSpherePoint.getX() * radius + center.getX(), unitSpherePoint.getY() * radius + center.getY(), unitSpherePoint.getZ() * radius + center.getZ());
        }
        return spherePoints;
    }


    /**
     * Generates points evenly distributed on the surface of a sphere using the Fibonacci algorithm.
     *
     * @param center  The center of the sphere.
     * @param radius  The radius of the sphere.
     * @param samples The number of points to generate.
     * @return An array of {@link Point} objects representing the sphere points.
     */
    public static Point[] generateFibonacciSphere(Point center, double radius, int samples) {
        Point[] points = new Point[samples];
        double phi = Math.PI * (3.0 - sqrt(5.0)); // Golden angle in radians

        for (int i = 0; i < samples; i++) {
            double y = 1 - (double) i / (samples - 1) * 2; // Map i to range [-1, 1]
            double radiusAtY = sqrt(1 - y * y); // Radius of the circle at height y
            double theta = phi * i; // Angle for the point
            double x = Math.cos(theta) * radiusAtY;
            double z = Math.sin(theta) * radiusAtY;

            points[i] = new Point(
                    x * radius + center.getX(),
                    y * radius + center.getY(),
                    z * radius + center.getZ()
            );
        }
        return points;
    }


}

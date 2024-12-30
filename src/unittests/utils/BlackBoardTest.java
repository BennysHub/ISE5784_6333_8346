package utils;

import geometries.Sphere;
import lighting.PointLight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.Camera;
import renderer.QualityLevel;
import scene.Scene;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Util.alignZero;
import static utils.Util.isZero;

public class BlackBoardTest {


    private Point center;
    private Point center2;
    private Vector normal;


    @BeforeEach
    public void setUp() {
        center = Point.ZERO;
        center2 = new Point(5, 4, 7);
        normal = Vector.UNIT_Z;
    }

    @Test
    public void generateGrid() {
        Double2[] actualGridPoints = Blackboard.generateSquareGrid(2, 4);
        Double2[] expectedGridPoints = {new Double2(0.5, -0.5), new Double2(0.5, 0.5), new Double2(-0.5, -0.5), new Double2(-0.5, 0.5)};
        //.System.out.println(Arrays.toString(actualGridPoints));
        assertArrayEquals(actualGridPoints, expectedGridPoints, "Generate Grid is incorrect");
    }

    @Test
    public void testConvertTo3D() {
        // Create a sample disk
        Double2[] square2DPoints = new Double2[4];
        square2DPoints[0] = new Double2(0, 0);
        square2DPoints[1] = new Double2(1, 0);
        square2DPoints[2] = new Double2(0, 1);
        square2DPoints[3] = new Double2(1, 1);

        // Convert the disk to 3D
        Point[] actualConversionPoints = Blackboard.convertTo3D(square2DPoints, center, normal);

        Point[] expectedConversionPoints = {Point.ZERO, new Point(1, 0, 0), new Point(0, 1, 0), new Point(1, 1, 0)};

        assertArrayEquals(expectedConversionPoints, actualConversionPoints, "Conversion to 3D is incorrect");
    }

    @Test
    public void testScale() {
        Double2[] expectedScale = Blackboard.generateSquareGrid(10, 100);
        Double2[] actualScale = Blackboard.scale(Blackboard.generateSquareGrid(1, 100), 10);

        assertArrayEquals(expectedScale, actualScale, "Scaling is incorrect");
    }

    @Test
    public void testAddSphereDepth() {
        //Point center =  new Point(1, 5, 2);
        Double2[] gridPoint = Blackboard.generateSquareGrid(8, 169);
        Point[] girdPoints3D = Blackboard.convertTo3D(gridPoint, center, normal);
        Point[] spherePoints = Blackboard.addSphereDepth(girdPoints3D, center, 6, normal);
        for (Point point : spherePoints)
            assertEquals(0, alignZero(point.distance(center) - 6), "Sphere depth is incorrect");
    }


    @Test
    public void testWarpToCircle1() {
        Double2[] squareGridPoints = Blackboard.generateSquareGrid(17, 169);
        Double2[] warpedCirclePoint1 = Blackboard.warpToCircle1(squareGridPoints, 17, 5);

        int size = squareGridPoints.length;
        int size1 = warpedCirclePoint1.length;
        assertEquals(size1, size, "Missing Disk Points");

        for (Double2 double2 : warpedCirclePoint1) {
            double x = double2.x();
            double y = double2.y();
            double distance = Math.sqrt(x * x + y * y);
            System.out.println(x + "," + y + "  distance: " + distance);

            assertTrue(alignZero(5 - distance) >= 0, "Point warping to disk is incorrect.");
        }
    }

    @Test
    public void testWarpToCircle2() {
        Double2[] squareGridPoints = Blackboard.generateSquareGrid(17, 169);
        Double2[] warpedCirclePoint2 = Blackboard.warpToCircle2(squareGridPoints, 17, 5);

        int size = squareGridPoints.length;
        int size2 = warpedCirclePoint2.length;
        assertEquals(size2, size, "Missing Disk Points");

        for (Double2 double2 : warpedCirclePoint2) {
            double x = double2.x();
            double y = double2.y();
            double distance = Math.sqrt(x * x + y * y);
            System.out.println(x + "," + y + "  distance: " + distance);

            assertTrue(alignZero(5 - distance) >= 0, "Point warping to disk is incorrect.");
        }
    }

    private void sortPoints(Point[] points) {
        Arrays.sort(points, (p1, p2) -> {
            if (isZero(p1.getX() - p2.getX())) {
                if (isZero(p1.getY() - p2.getY())) {
                    return Double.compare(p1.getZ(), p2.getZ());
                }
                return Double.compare(p1.getY(), p2.getY());
            }
            return Double.compare(p1.getX(), p2.getX());
        });
    }

    @Test
    public void testRotatePointsOnSphere() {

        Point[] points = Blackboard.getHalfSpherePoints(center2, 3, normal, QualityLevel.HIGH);

        // Test case 1: Rotate from up to down

        testRotation(points, normal, normal.scale(-1), center2);

        // Test case 2: Rotate from up to right
        Vector right = new Vector(0, 8, 0);
        testRotation(points, normal, right, center2);

        // Test case 3: Rotate from up to an arbitrary vector (1, 1, 1)
        Vector arbitraryVector = new Vector(1, 1, 1);
        testRotation(points, normal, arbitraryVector, center2);
    }


    private void testRotation(Point[] points, Vector from, Vector to, Point center) {
        Point[] expectedPoints = Blackboard.getHalfSpherePoints(center, 3, to, QualityLevel.HIGH);
        Point[] actualPoints = Blackboard.rotatePointsOnSphere(points, from, to, center);

        // Sort the arrays
        sortPoints(expectedPoints);
        sortPoints(actualPoints);
        assertArrayEquals(expectedPoints, actualPoints, "Sphere points rotation is incorrect.");
    }


    @Test
    public void visualFibonacciSphere() {
        Scene scene = new Scene("fibonacciSphere");

        double div = 100;

        Material materialRed = new Material().setKs(new Double3(2)).setKd(new Double3(175/div, 255/div, 30/div)).setShininess(30);
        Material materialBlue = new Material().setKs(new Double3(2)).setKd(new Double3(2, 2, 2)).setShininess(30).setEmission(new Color(50, 50, 100));

       // scene.geometries.add(new Sphere(20, Point.ZERO).setMaterial(materialRed));

      //  Point[] sphereSampled = Blackboard.generateFibonacciSphere(Point.ZERO, 20, 10000);
        Point[] sphereSampled = Blackboard.generateFibonacciDisk(Point.ZERO, 20, Vector.UNIT_X, 10000);

        for (Point spherePoint : sphereSampled)
            scene.geometries.add(new Sphere(0.1, spherePoint).setMaterial(materialBlue));

        //scene.ambientLight = new AmbientLight(new Color(java.awt.Color.WHITE), 0.2);

        PointLight pointLight = new PointLight(new Color(50, 50, 50), new Point(1560, 0, 0));

        scene.lights.add(pointLight);

        Camera.Builder camaraBuilder = Camera.builder()
                .setPosition(new Point(100, 0, 0))
                .setOrientation(Point.ZERO, Vector.UNIT_Z)
                .setViewPlaneSize(25, 25)
                .setResolution(2560, 2560)
                .setViewPlaneDistance(50)
                .setScene(scene)
                .enableBVH(true)
                .setImageName("fibonacciSphere")
                .enableParallelStreams(true);

        camaraBuilder.build().renderImage().writeToImage();

    }


}

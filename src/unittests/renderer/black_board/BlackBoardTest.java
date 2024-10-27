package renderer.black_board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primitives.Double2;
import primitives.Point;
import primitives.Vector;
import renderer.super_sampling.Blackboard;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static primitives.Util.alignZero;

public class BlackBoardTest {


    private Point center;
    private Vector right;
    private Vector up;
    private Vector normal;


    @BeforeEach
    public void setUp() {
        center = new Point(0, 0, 0);
        right = new Vector(1, 0, 0);
        up = new Vector(0, 1, 0);
        normal = new Vector(0, 0, 1);
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
        Point[] actualConversionPoints = Blackboard.convertTo3D(square2DPoints, center, right, up);

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
        Point[] girdPoints3D = Blackboard.convertTo3D(gridPoint, center, right, up);
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
        Arrays.sort(points, Comparator.comparingDouble(Point::getX)
                .thenComparingDouble(Point::getY)
                .thenComparingDouble(Point::getZ));
    }

    @Test
    public void testRotatePointsOnSphere() {
        Vector up = new Vector(0, 0, 1);
        Vector down = new Vector(0, 0, -1);
        Point center = new Point(3, -1, 30);
        Point[] points = Blackboard.getSpherePoints(center, 3, up);

        // Test case 1: Rotate from up to down

        testRotation(points, up, down, center);

        // Test case 2: Rotate from up to right
        Vector right = new Vector(0, 1, 0);
        testRotation(points, up, right, center);

        // Test case 3: Rotate from up to right with a different center
        //Point center = new Point(3, -1, 30);
        testRotation(points, up, right, center);

        // Test case 4: Rotate from up to an arbitrary vector (1, 1, 1)
        Vector arbitraryVector = new Vector(1, 1, 1);
        testRotation(points, up, arbitraryVector, center);
    }

    private void testRotation(Point[] points, Vector from, Vector to, Point center) {
        Point[] expectedPoints = Blackboard.getSpherePoints(center, 3, to);
        Point[] actualPoints = Blackboard.rotatePointsOnSphere(points, from, to, center);

        // Sort the arrays
        sortPoints(expectedPoints);
        sortPoints(actualPoints);
        assertArrayEquals(expectedPoints, actualPoints, "Sphere points rotation is incorrect.");
    }
}

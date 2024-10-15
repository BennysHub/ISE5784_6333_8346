package renderer.black_board;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import primitives.Matrix;
import primitives.Point;
import primitives.Point2D;
import primitives.Vector;
import renderer.super_sampling.Blackboard;

import static org.junit.jupiter.api.Assertions.*;

public class BlackBoardTest {


    private Point center;
    private Vector right;
    private Vector up;

    @BeforeEach
    public void setUp() {
        center = new Point(0, 0, 0);
        right = new Vector(1, 0, 0);
        up = new Vector(0, 1, 0);
    }

//    @Test
//    public void testGenerateJitteredGrid() {
//        int size = 10;
//        double jitter = 0.5;
//
//        Point2D[] grid = Blackboard.generateJitteredGrid(size, jitter);
//
//        // Check if the grid size is correct
//        assertEquals(size * size, grid.length);
//
//        // Check if points are within the expected range and centered at (0, 0)
//        double centerX = 0;
//        double centerY = 0;
//        for (Point2D point : grid) {
//            assertTrue(Math.abs(point.getX() - centerX) <= jitter);
//            assertTrue(Math.abs(point.getY() - centerY) <= jitter);
//        }
//
//        // Check if the grid is not completely uniform
//        boolean isUniform = true;
//        for (int i = 1; i < grid.length; i++) {
//            if (!grid[i].equals(grid[i - 1])) {
//                isUniform = false;
//                break;
//            }
//        }
//        assertFalse(isUniform);
//    }


//    @Test
//    public void testWarpToDisk() {
//        int size = 10;
//        double jitter = 0.1;
//        double radius = 1.0;
//        Point2D[] grid = Blackboard.generateJitteredGrid(size, jitter);
//        Point2D[] diskPoints = Blackboard.warpToDisk(grid, radius);
//
//        assertEquals(grid.length, diskPoints.length, "Disk points size should match grid size");
//        for (Point2D point : diskPoints) {
//            double distance = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
//            assertTrue(distance <= radius, "Point is outside the disk radius");
//        }
//    }

//    @Test
//    public void testConvertTo3D() {
//        int size = 10;
//        double jitter = 0.1;
//        double radius = 1.0;
//        Point2D[] grid = Blackboard.generateJitteredGrid(size, jitter);
//        Point2D[] diskPoints = Blackboard.warpToDisk(grid, radius);
//        Point[] points3D = Blackboard.convertTo3D(diskPoints, center, right, up);
//
//        assertEquals(diskPoints.length, points3D.length, "3D points size should match disk points size");
//        for (Point point : points3D) {
//            assertNotNull(point, "3D point should not be null");
//        }
//    }

    @Test
    public void testConvertTo3D() {
        // Create a sample disk
        Point2D[] diskPoints = new Point2D[4];
        diskPoints[0] = new Point2D(0, 0);
        diskPoints[1] = new Point2D(1, 0);
        diskPoints[2] = new Point2D(0, 1);
        diskPoints[3] = new Point2D(1, 1);


        // Convert the disk to 3D
        Point[] points3D = Blackboard.convertTo3D(diskPoints, center, right, up);

        // Check if the converted points are on the z=0 plane
        for (Point point : points3D) {
            assertEquals(0, point.getZ(), 0.0001);
        }
    }
}

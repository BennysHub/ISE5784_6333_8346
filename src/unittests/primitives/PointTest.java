package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the primitives.Point class.
 * Each method is tested separately with Equivalence Partitioning and Boundary Value Analysis.
 *
 * @author Benny Avrahami
 */
class PointTest {
    private static final Point p1 = new Point(1, 2, 3);
    private static final Point p2 = new Point(-5, 2, 49);
    private static final Point p3 = new Point(1, 2, 3);
    private static final double DELTA = 0.000001; // Delta value for accuracy when comparing 'double' types

    /**
     * Test method for {@link primitives.Point#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Adding a vector to a point results in a new point with the sum of coordinates
        var v1 = new Vector(6, 6, 6);
        assertEquals(new Point(7, 8, 9), p1.add(v1),
                "Adding a vector to a point did not result in the correct point.");

        // =============== Boundary Values Tests ==================

        // TC10: Adding opposite vector and point should result in point (0,0,0)
        var v2 = new Vector(-1, -2, -3); // opposite vector to point v1
        assertEquals(Point.ZERO, p1.add(v2),
                "Adding an opposite vector to a point did not result in the origin point.");
    }

    /**
     * Test method for {@link primitives.Point#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Subtracting two points returns a vector from the second point to the first point
        assertEquals(new Vector(6, 0, -46), p1.subtract(p2),
                "Subtracting one point from another did not return the correct vector.");

        // =============== Boundary Values Tests ==================

        // TC10: Subtracting the same point should throw an exception
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
                "Subtracting a point from itself should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Point#distance(primitives.Point)}.
     */
    @Test
    void testDistance() {


        // ============ Equivalence Partitions Tests ==============

        // TC01: The distance between two points need to work correctly
        assertEquals(Math.sqrt(14), p1.distance(Point.ZERO),
                "The distance calculation between two points is incorrect.");

        // TC02: The  distance between two different points is positive
        assertTrue(p1.distance(p2) > 0,
                "The squared distance between two different points should be positive.");

        // =============== Boundary Values Tests ==================

        // TC10: The distance between the same point should be zero
        assertEquals(0, p1.distance(p3), DELTA,
                "The squared distance between the same point should be zero.");

        // TC20: The distance between two points should be the square root of the squared distance
        assertEquals(Math.sqrt(p1.distanceSquared(p2)), p1.distance(p2),
                "The distance between two points should be the square root of the squared distance.");

        // Additional BVA Test: The distance between two points at a very small scale
        Point pSmall1 = new Point(0.000001, 0.000001, 0.000001);
        Point pSmall2 = new Point(0.000002, 0.000002, 0.000002);
        assertTrue(pSmall1.distance(pSmall2) < 0.000002,
                "The distance between two points at a very small scale is incorrect.");

        // Additional BVA Test: The distance between two points at a very large scale
        Point pLarge1 = new Point(1000000, 1000000, 1000000);
        Point pLarge2 = new Point(2000000, 2000000, 2000000);
        assertTrue(pLarge1.distance(pLarge2) > 1000000,
                "The distance between two points at a very large scale is incorrect.");

    }

    /**
     * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}
     */
    @Test
    void testDistanceSquared() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: The distance square between two points need to work correctly
        assertEquals(14d, p1.distanceSquared(Point.ZERO),
                "The distance calculation between two points is incorrect.");

        // TC02: The squared distance between two different points is positive
        assertTrue(p1.distanceSquared(p2) > 0,
                "The squared distance between two different points should be positive.");

        // =============== Boundary Values Tests ==================

        // TC10: The squared distance between the same point should be zero
        assertEquals(0d, p1.distanceSquared(p3), DELTA,
                "The squared distance between the same point should be zero.");

        // TC20: The distance between two points should be the square root of the squared distance
        assertEquals(Math.sqrt(p1.distanceSquared(p2)), p1.distance(p2),
                "The distance between two points should be the square root of the squared distance.");

        // Additional BVA Test: The squared distance between two points at a very small scale
        Point pSmall1 = new Point(0.000001, 0.000001, 0.000001);
        Point pSmall2 = new Point(0.000002, 0.000002, 0.000002);
        assertTrue(pSmall1.distanceSquared(pSmall2) < 0.000002 * 0.000002,
                "The squared distance between two points at a very small scale is incorrect.");

    }
}

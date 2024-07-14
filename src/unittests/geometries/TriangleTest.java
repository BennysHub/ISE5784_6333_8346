package geometries;

import org.junit.jupiter.api.Test;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Triangle class.
 *
 * @author Benny Avrahami
 */
class TriangleTest {

    /**
     * Test method for {@link geometries.Triangle#Triangle(primitives.Point, primitives.Point, primitives.Point)}.
     */
    @Test
    void testTriangleConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct instance creation of Triangle class
        assertDoesNotThrow(() -> new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                "Triangle constructor failed to create a valid instance with three points.");

        // =============== Boundary Values Tests ==================

        // TC01: Constructing a triangle with two identical points
        assertThrows(IllegalArgumentException.class,
                () -> new Triangle(new Point(1, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0)),
                "Constructed a triangle with two identical points");

        // TC02: Constructing a triangle with three collinear points
        assertThrows(IllegalArgumentException.class,
                () -> new Triangle(new Point(0, 0, 0), new Point(1, 1, 1), new Point(2, 2, 2)),
                "Constructed a triangle with three collinear points");
    }

    /**
     * Test method for {@link geometries.Triangle#getNormal(primitives.Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Correct normal calculation
        Triangle triangle = new Triangle(new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1));
        Point p = new Point(0, 0, 1); // A point on the triangle

        // Expected normal can be on both side of triangle
        Vector expectedNormal1 = new Vector(1, 1, 1).normalize();
        Vector expectedNormal2 = new Vector(-1, -1, -1).normalize();

        Vector actualNormal = triangle.getNormal(p);
        assertTrue(expectedNormal1.equals(actualNormal) || expectedNormal2.equals(actualNormal),
                "getNormal() - The normal of the Triangle is not being calculated correctly.");
    }


    /**
     * Test method for {@link geometries.Triangle#findIntersections(Ray)}.
     * Verifies that the intersection with rays are correct for a given Triangle.
     */
    @Test
    void findIntersections() {
        Triangle triangle = new Triangle(
                new Point(1, 1, 0),
                new Point(2, 1, 0),
                new Point(1, 2, 0)
        );
        Vector v = new Vector(0, 0, -1);
        Vector v2 = new Vector(4, 2, 9);
        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray intersects inside the triangle
        var result1 = triangle.findIntersections(new Ray(new Point(1.35, 1.35, 1), v));
        assertNotNull(result1, "No intersection inside the triangle");
        assertEquals(new Point(1.35, 1.35, 0), result1.getFirst(), "Wrong intersection point inside the triangle");

        // TC02: Ray intersects next to one of the edges
        var result2 = triangle.findIntersections(new Ray(new Point(1.5, 0.5, 1), v));
        assertNull(result2, "Ray intersects next to one of the edges, should be no intersection");

        // TC03: Ray intersects next to one of the points
        var result3 = triangle.findIntersections(new Ray(new Point(2.5, 0.75, 0), v));
        assertNull(result3, "Ray intersects next to one of the points, should be no intersection");

        // =============== Boundary Values Tests ==================
        // TC10: Ray intersects triangle edge
        var result4 = triangle.findIntersections(new Ray(new Point(1.5, 1.5, 1), v));
        assertNull(result4, "Ray intersects triangle edge, should be no intersection");

        // TC20: Ray intersects triangle vertex
        var result5 = triangle.findIntersections(new Ray(new Point(Double3.ONE), v));
        assertNull(result5, "Ray intersects triangle point, should be no intersection");

        // TC30: Ray intersects the plane on a line that continues the side of the triangle
        var result6 = triangle.findIntersections(new Ray(new Point(3, 1, 1), v));
        assertNull(result6, "Ray intersects the plane on a line that continues the side of the triangle, should be no intersection");

        // TC40: Ray parallel to triangle
        var result7 = triangle.findIntersections(new Ray(new Point(3, 1, 1), new Vector(4, 2, 0)));
        assertNull(result7, "Ray parallel to triangle, should be no intersection");

        // TC50: Ray parallel inside triangle
        var result8 = triangle.findIntersections(new Ray(new Point(1, 1, 0), new Vector(4, 2, 0)));
        assertNull(result8, "Ray parallel inside triangle, should be no intersection");

        // TC60: Ray start at triangle
        var result9 = triangle.findIntersections(new Ray(new Point(1, 1, 0), v2));
        assertNull(result9, "Ray start inside triangle, should be no intersection");

        // TC70: Ray start at triangle edge
        var result10 = triangle.findIntersections(new Ray(new Point(1.5, 1.5, 0), v2));
        assertNull(result10, "Ray start inside triangle, should be no intersection");

        // TC80: Ray start at triangle vertex
        var result11 = triangle.findIntersections(new Ray(new Point(1, 1, 0), v2));
        assertNull(result11, "Ray start inside triangle, should be no intersection");

    }
}
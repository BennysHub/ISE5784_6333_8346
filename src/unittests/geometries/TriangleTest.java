package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for geometries.Triangle class.
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
        Vector expectedNormal = new Vector(1, 1, 1).normalize(); // Expected normal

        Vector actualNormal = triangle.getNormal(p);
        assertEquals(expectedNormal, actualNormal,
                "getNormal() - The normal of the Triangle is not being calculated correctly.");
    }


    @Test
    void findIntsersections() {
    }
}
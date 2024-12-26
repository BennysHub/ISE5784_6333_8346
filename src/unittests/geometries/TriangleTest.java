package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

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

        // Expected normal
        Vector expectedNormal1 = new Vector(1, 1, 1).normalize();
        Vector expectedNormal2 = new Vector(-1, -1, -1).normalize();

        Vector actualNormal = triangle.getNormal(p);
        assertTrue(expectedNormal1.equals(actualNormal) || expectedNormal2.equals(actualNormal),
                "getNormal() - The normal of the Triangle is not being calculated correctly.");
    }

    /**
     * Test method for {@link geometries.Triangle#findGeoIntersections(Ray)}.
     * Verifies that the intersection with rays is correct for a given Triangle.
     */
    @Test
    void findIntersections() {
        Triangle triangle = new Triangle(
                new Point(1, 1, 0),
                new Point(2, 1, 0),
                new Point(1, 2, 0)
        );

        Vector v = new Vector(0, 0, -1);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects inside the triangle
        var result1 = triangle.findGeoIntersections(new Ray(new Point(1.35, 1.35, 1), v));
        assertNotNull(result1, "No intersection inside the triangle");
        assertEquals(new Point(1.35, 1.35, 0), result1.get(0).point(),
                "Wrong intersection point inside the triangle");

        // TC02: Ray intersects next to one of the edges
        var result2 = triangle.findGeoIntersections(new Ray(new Point(1.5, 0.5, 1), v));
        assertNull(result2, "Ray intersects next to one of the edges, should be no intersection");

        // TC03: Ray intersects next to one of the points
        var result3 = triangle.findGeoIntersections(new Ray(new Point(2.5, 0.75, 0), v));
        assertNull(result3, "Ray intersects next to one of the points, should be no intersection");

        // =============== Boundary Values Tests ==================

        // TC10: Ray intersects triangle edge
        var result4 = triangle.findGeoIntersections(new Ray(new Point(1.5, 1.5, 1), v));
        assertNull(result4, "Ray intersects triangle edge, should be no intersection");

        // TC20: Ray intersects triangle vertex
        var result5 = triangle.findGeoIntersections(new Ray(new Point(1, 1, 0), v));
        assertNull(result5, "Ray intersects triangle vertex, should be no intersection");

        // TC30: Ray intersects the plane but outside the triangle
        var result6 = triangle.findGeoIntersections(new Ray(new Point(3, 1, 1), v));
        assertNull(result6, "Ray intersects the plane but outside the triangle, should be no intersection");

        // TC40: Ray parallel to triangle
        var result7 = triangle.findGeoIntersections(new Ray(new Point(3, 1, 1), new Vector(4, 2, 0)));
        assertNull(result7, "Ray parallel to triangle, should be no intersection");

        // TC50: Ray parallel inside triangle
        var result8 = triangle.findGeoIntersections(new Ray(new Point(1, 1, 0), new Vector(4, 2, 0)));
        assertNull(result8, "Ray parallel inside triangle, should be no intersection");

        // TC60: Ray starting at the triangle and pointing away
        var result9 = triangle.findGeoIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 0, 1)));
        assertNull(result9, "Ray starts at the triangle and points away, should be no intersection");
    }


    @Test
    void testTransformations() {
        Triangle triangle = new Triangle(
                new Point(0, 0, 0),
                new Point(1, 0, 0),
                new Point(0, 1, 0)
        );

        // ============ Translation ==============
        Triangle translatedTriangle = (Triangle) triangle.translate(new Vector(1, 1, 1));
        assertEquals(new Point(1, 1, 1), translatedTriangle.getVertices(0), "Incorrect translation for vertex 0.");
        assertEquals(new Point(2, 1, 1), translatedTriangle.getVertices(1), "Incorrect translation for vertex 1.");
        assertEquals(new Point(1, 2, 1), translatedTriangle.getVertices(2), "Incorrect translation for vertex 2.");

        // ============ Scaling ==============
        Triangle scaledTriangle = (Triangle) triangle.scale(new Vector(2, 2, 2));
        assertEquals(new Point(0, 0, 0), scaledTriangle.getVertices(0), "Incorrect scaling for vertex 0.");
        assertEquals(new Point(2, 0, 0), scaledTriangle.getVertices(1), "Incorrect scaling for vertex 1.");
        assertEquals(new Point(0, 2, 0), scaledTriangle.getVertices(2), "Incorrect scaling for vertex 2.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 0, 1), Math.toRadians(90));
        Triangle rotatedTriangle = (Triangle) triangle.rotate(rotation);
        assertEquals(new Point(0, 0, 0), rotatedTriangle.getVertices(0), "Incorrect rotation for vertex 0.");
        assertEquals(new Point(0, 1, 0), rotatedTriangle.getVertices(1), "Incorrect rotation for vertex 1.");
        assertEquals(new Point(-1, 0, 0), rotatedTriangle.getVertices(2), "Incorrect rotation for vertex 2.");
    }

}

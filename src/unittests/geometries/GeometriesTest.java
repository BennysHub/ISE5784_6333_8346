package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Geometries.
 * Unit tests for the Geometries composite class.
 * Author: Benny Avrahami and Tzvi Yisrael
 */
class GeometriesTest {

    /**
     * Test method for {@link Geometries#Geometries()}.
     * Verifies that the constructor initializes a valid Geometries instance.
     */
    @Test
    void testConstructor() {
        Sphere s = new Sphere(1, new Point(1.5, 0, 0));
        Triangle t = new Triangle(new Point(3, 0, 2),
                new Point(3, 2, -1),
                new Point(3, -2, -1));
        Plane p = new Plane(new Point(4, 0, 2),
                new Point(4, 2, -1),
                new Point(4, -2, -1));

        // Test valid constructor
        Geometries g = new Geometries(s, t, p);
        assertNotNull(g, "Geometries constructor failed to create a valid instance.");
    }

    /**
     * Test method for {@link Geometries#findGeoIntersections(Ray)}.
     * Verifies the behavior of the `findIntersections` method in different scenarios.
     */
    @Test
    void testFindIntersections() {
        Sphere s = new Sphere(1, new Point(1.5, 0, 0));
        Triangle t = new Triangle(new Point(3, 0, 2),
                new Point(3, 2, -1),
                new Point(3, -2, -1));
        Plane p = new Plane(new Point(4, 0, 2),
                new Point(4, 2, -1),
                new Point(4, -2, -1));

        // =============== Boundary Values Tests ==================

        // TC01: Empty collection
        Geometries empty = new Geometries();
        assertNull(empty.findGeoIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0))),
                "TC01: Expected no intersections for an empty collection.");

        Geometries g = new Geometries(s, t, p);

        // TC02: No intersection
        assertNull(g.findGeoIntersections(new Ray(new Point(-1, 0, 0), new Vector(-1, 0, 0))),
                "TC02: Expected no intersections when the ray does not intersect any geometry.");

        // TC03: One geometry intersecting
        var result1 = g.findGeoIntersections(
                new Ray(new Point(3.5, 0, 0), new Vector(1, 0, 0))
        );
        assertNotNull(result1, "TC03: Expected an intersection with one geometry.");
        assertEquals(1, result1.size(), "TC03: Wrong number of intersections.");
        assertEquals(new Point(4, 0, 0), result1.get(0).point(), "TC03: Wrong intersection point.");

        // TC04: All geometries intersect
        var result2 = g.findGeoIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0)));
        assertNotNull(result2, "TC04: Expected intersections with all geometries.");
        assertEquals(4, result2.size(), "TC04: Wrong number of intersections.");
        assertTrue(result2.stream().map(GeoPoint::point).toList().containsAll(
                        List.of(new Point(0.5, 0, 0),
                                new Point(2.5, 0, 0),
                                new Point(3, 0, 0),
                                new Point(4, 0, 0))),
                "TC04: Wrong intersection points.");

        // ============ Equivalence Partitions Tests ==============

        // TC05: Some geometries intersect
        var result3 = g.findGeoIntersections(new Ray(new Point(2.75, 0, 0), new Vector(1, 0, 0)));
        assertNotNull(result3, "TC05: Expected intersections with some geometries.");
        assertEquals(2, result3.size(), "TC05: Wrong number of intersections.");
        assertTrue(result3.stream().map(GeoPoint::point).toList().containsAll(
                        List.of(new Point(3, 0, 0), new Point(4, 0, 0))),
                "TC05: Wrong intersection points.");
    }
}

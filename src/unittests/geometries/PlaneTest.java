package geometries;

import org.junit.jupiter.api.Test;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Plane} class.
 * This class verifies the correct behavior of the Plane geometry,
 * including its construction, normal calculation, and ray-plane intersection logic.
 *
 * @author Benny Avrahami
 */
class PlaneTest {

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     * Verifies that the constructor correctly initializes a plane with valid input
     * and throws exceptions for invalid configurations.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Successfully constructing a plane in the xy-plane
        assertDoesNotThrow(() -> new Plane(
                        new Point(0, 0, 0),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0)),
                "Plane should be constructed with three distinct, non-collinear points.");

        // =============== Boundary Values Tests ==================

        // TC10: Attempting to construct a plane with two identical points
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(
                        new Point(0, 0, 0),
                        new Point(0, 0, 0),
                        new Point(0, 1, 0)),
                "Construction should fail with two identical points as they cannot define a plane.");

        // TC20: Attempting to construct a plane with three collinear points
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(
                        new Point(0, 0, 0),
                        new Point(Double3.ONE),
                        new Point(2, 2, 2)),
                "Construction should fail with three collinear points as they cannot define a plane.");
    }

    /**
     * Test method for {@link Plane#getNormal()} and {@link Plane#getNormal(Point)}.
     * Verifies that the normal vector is calculated correctly and is consistent
     * regardless of the method used to retrieve it.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Verifying the normal of a plane defined in the xy-plane
        Plane plane = new Plane(
                new Point(0, 0, 0),
                new Point(1, 0, 0),
                new Point(0, 1, 0));
        Vector normal = plane.getNormal();
        Vector expectedNormal1 = new Vector(0, 0, 1);
        Vector expectedNormal2 = new Vector(0, 0, -1);

        assertTrue(normal.equals(expectedNormal1) || normal.equals(expectedNormal2),
                "The normal of the plane is incorrect or not perpendicular to the xy-plane.");

        // TC02: Ensuring consistency of normal vector regardless of the retrieval method
        assertEquals(normal, plane.getNormal(new Point(0, 0, 0)),
                "The getNormal(Point) method should return a consistent normal vector for the plane.");
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     * Verifies the intersection logic of a ray with a plane in various scenarios.
     */
    @Test
    void findIntersections() {
        Plane plane = new Plane(
                new Point(0, 0, 0),
                new Point(1, 0, 0),
                new Point(0, 1, 0));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the plane
        var result1 = plane.findGeoIntersections(new Ray(
                new Point(1, 0, 1),
                new Vector(1, 0, -1)));
        assertNotNull(result1, "TC01: Ray should intersect the plane.");
        assertEquals(new Point(2, 0, 0), result1.get(0).point(),
                "TC01: Intersection point is incorrect.");

        // TC02: Ray does not intersect the plane
        var result2 = plane.findGeoIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 0, 1)));
        assertNull(result2, "TC02: Ray should not intersect the plane.");

        // =============== Boundary Values Tests ==================

        // Ray is parallel to the plane
        // TC10: Ray lies in the plane
        var result3 = plane.findGeoIntersections(new Ray(
                new Point(1, 0, 0),
                new Vector(1, 1, 0)));
        assertNull(result3, "TC10: Ray lies in the plane, no intersection.");

        // TC20: Ray is parallel and not included in the plane
        var result4 = plane.findGeoIntersections(new Ray(
                new Point(0, 0, 1),
                new Vector(1, 1, 0)));
        assertNull(result4, "TC20: Ray is parallel and not in the plane, no intersection.");

        // Ray is orthogonal to the plane
        // TC30: Ray starts before the plane
        var result5 = plane.findGeoIntersections(new Ray(
                new Point(1, 0, 1),
                new Vector(0, 0, -1)));
        assertNotNull(result5, "TC30: Ray should intersect the plane.");
        assertEquals(new Point(1, 0, 0), result5.get(0).point(),
                "TC30: Intersection point is incorrect.");

        // TC40: Ray starts in the plane
        var result6 = plane.findGeoIntersections(new Ray(
                new Point(1, 0, 0),
                new Vector(0, 0, -1)));
        assertNull(result6, "TC40: Ray starts in the plane, no intersection.");

        // TC50: Ray starts after the plane
        var result7 = plane.findGeoIntersections(new Ray(
                new Point(0, 0, -1),
                new Vector(0, 0, -1)));
        assertNull(result7, "TC50: Ray starts after the plane, no intersection.");

        // Ray is neither orthogonal nor parallel
        // TC60: Ray starts on the plane
        var result8 = plane.findGeoIntersections(new Ray(
                new Point(2, 0, 0),
                new Vector(1, 1, -1)));
        assertNull(result8, "TC60: Ray starts in the plane, no intersection.");

        // TC70: Ray starts at a reference point in the plane
        var result9 = plane.findGeoIntersections(new Ray(
                new Point(0, 0, 0),
                new Vector(1, 1, -1)));
        assertNull(result9, "TC70: Ray starts at a reference point of the plane, no intersection.");
    }
}

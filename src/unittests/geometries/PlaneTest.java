package geometries;

import org.junit.jupiter.api.Test;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Plane geometry.
 */
class PlaneTest {

    /**
     * Test method for {@link geometries.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
     * Verifies that a plane is correctly constructed with three distinct, non-collinear points.
     */
    @Test
    void testConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Successfully constructing a plane in the xy axis.
        assertDoesNotThrow(() -> new Plane(new Point(0, 0, 0),
                        new Point(1, 0, 0),
                        new Point(0, 1, 0)),
                "Plane should be constructed with three distinct points in the xy axis.");

        // =============== Boundary Values Tests ==================

        // TC10: Attempting to construct a plane with two identical points.
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(0, 0, 0),
                        new Point(0, 0, 0),
                        new Point(0, 1, 0)),
                "Construction should fail with two identical points as they cannot define a plane.");

        // TC20: Attempting to construct a plane with three collinear points.
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(0, 0, 0),
                        new Point(Double3.ONE),
                        new Point(2, 2, 2)),
                "Construction should fail with three collinear points as they cannot define a plane.");
    }

    /**
     * Test method for {@link geometries.Plane#getNormal()}.
     * Verifies that the normal vector returned is correct for a given plane.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Verifying the normal of the plane.
        var p1 = new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));
        assertTrue(p1.getNormal().equals(new Vector(0, 0, 1)) || p1.getNormal().equals(new Vector(0, 0, -1)),
                "The normal of the plane should be perpendicular to the xy axis.");

        // TC02: Ensuring consistency of normal vector regardless of the method used.
        assertEquals(p1.getNormal(new Point(0, 0, 0)), p1.getNormal(),
                "The getNormal() method should return a consistent normal vector for the plane.");
    }

    /**
     * Test method for {@link geometries.Plane#findIntersections(Ray)}.
     * Verifies that the intersection with rays are correct for a given plane.
     */
    @Test
    void findIntersections() {
        var p1 = new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(0, 1, 0));

        // ============ Equivalence Partitions Tests ==============
        // TC01: Ray intersects the plane
        final var result1 = p1.findIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 0, -1)));
        assertNotNull(result1, "No intersection");
        assertEquals(new Point(2, 0, 0), result1.getFirst(), "Wrong intersection point");

        // TC02: Ray does not intersect the plane
        final var result2 = p1.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 0, 1)));
        assertNull(result2, "Ray should not intersect the plane");

        // =============== Boundary Values Tests ==================
        // Ray is parallel to the plane:
        // TC01: The ray included in the plane
        final var result3 = p1.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 1, 0)));
        assertNull(result3, "Ray is included in the plane, should be no intersection");

        // TC02: The ray not included in the plane
        final var result4 = p1.findIntersections(new Ray(new Point(0, 0, 1), new Vector(1, 1, 0)));
        assertNull(result4, "Ray is parallel and not included in the plane, should be no intersection");

        // Ray is orthogonal to the plane
        // TC03: P0 before the plane
        final var result5 = p1.findIntersections(new Ray(new Point(1, 0, 1), new Vector(0, 0, -1)));
        assertNotNull(result5, "No intersection when P0 is before the plane");
        assertEquals(new Point(1, 0, 0), result5.getFirst(), "Wrong intersection point");

        // TC04: P0 in the plane
        final var result6 = p1.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 0, -1)));
        assertNull(result6, "Ray starts in the plane, should be no intersection");

        // TC05: P0 after the plane
        final var result7 = p1.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, -1)));
        assertNull(result7, "Ray starts after the plane, should be no intersection");

        // Ray is neither orthogonal nor parallel to the plane
        // TC06: begins at the plane
        final var result8 = p1.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 1, -1)));
        assertNull(result8, "Ray starts in the plane, should be no intersection");

        // TC07: begins in the same point which appears as reference point in the plane
        final var result9 = p1.findIntersections(new Ray(new Point(0, 1, 0), new Vector(1, 1, -1)));
        assertNull(result9, "Ray starts at a reference point in the plane, should be no intersection");
    }
}

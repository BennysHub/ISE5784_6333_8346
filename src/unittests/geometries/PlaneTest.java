package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
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
                        new Point(1, 1, 1),
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

    @Test
    void findIntersections() {
    }
}

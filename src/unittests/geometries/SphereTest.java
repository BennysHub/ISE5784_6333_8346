package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Sphere geometry.
 */
class SphereTest {

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     * Verifies that the normal at a given point on the sphere is calculated correctly.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the sphere with center at the origin and radius 3
        Sphere sphere = new Sphere(3, Point.ZERO);
        assertEquals(new Vector(1,0,0), sphere.getNormal(new Point(3,0,0)),
                "The normal to the sphere at point (3,0,0) should be (1,0,0)");
    }
}

package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the geometries.Tube class.
 *
 * @author Benny Avrahami
 */
class TubeTest {

    /**
     * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
     * This method tests the normal calculation at a point on the tube's surface.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the tube's surface.
        Tube tube = new Tube(1, new Ray(Point.ZERO, new Vector(1, 0, 0)));
        assertEquals(new Vector(0, 0, 1), tube.getNormal(new Point(1, 0, 1)),
                "The normal at point (1,0,1) should be perpendicular to the tube's surface.");

        // =============== Boundary Values Tests ==================

        // TC10: Normal at a point on the tube's starting cap.
        assertEquals(new Vector(0, 0, 1), tube.getNormal(new Point(0, 0, 1)),
                "The normal at point (0,0,1) should be perpendicular to the tube's starting cap.");
    }

    @Test
    void findIntersections() {
    }
}

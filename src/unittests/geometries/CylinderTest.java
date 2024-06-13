package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Cylinder.
 * Author: Benny Avrahami
 */
class CylinderTest {

    /**
     * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
     */
    @Test
    void getNormalTest() {
        // Test case for the Cylinder class.
        var cylinder = new Cylinder(1, new Ray(Point.ZERO, new Vector(1, 0, 0)), 2);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal on the curved surface of the cylinder.
        assertEquals(new Vector(0,0,1), cylinder.getNormal(new Point(1,0,1)),
                "Normal calculation is incorrect for a point on the curved surface.");

        // TC02: Normal on the front base of the cylinder.
        assertEquals(new Vector(-1,0,0), cylinder.getNormal(new Point(0,0.5,0.5)),
                "Normal calculation is incorrect for a point on the front base.");

        // TC03: Normal on the back base of the cylinder.
        assertEquals(new Vector(1,0,0), cylinder.getNormal(new Point(2,0.5,0.5)),
                "Normal calculation is incorrect for a point on the back base.");

        // =============== Boundary Values Tests ==================

        // TC04: Normal at the center of the front base of the cylinder.
        assertEquals(new Vector(-1,0,0), cylinder.getNormal(Point.ZERO),
                "Normal calculation is incorrect for the center of the front base.");

        // TC05: Normal at the center of the back base of the cylinder.
        assertEquals(new Vector(1,0,0), cylinder.getNormal(new Point(2, 0, 0)),
                "Normal calculation is incorrect for the center of the back base.");

        // TC06: Normal on the edge of the front base of the cylinder.
        assertEquals(new Vector(-1,0,0), cylinder.getNormal(new Point(0, 0 ,1)),
                "Normal calculation is incorrect for an edge point on the front base.");

        // TC07: Normal on the edge of the back base of the cylinder.
        assertEquals(new Vector(1,0,0), cylinder.getNormal(new Point(2, 0, 1)),
                "Normal calculation is incorrect for an edge point on the back base.");
    }
}

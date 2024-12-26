package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link OOB} class.
 *
 * @author Benny Avrahami
 */
class OOBTest {

    /**
     * Test method for {@link OOB#intersects(Ray)}.
     */
    @Test
    void testIntersectsRay() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 1, 1), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the OOB from outside
        Ray ray = new Ray(new Point(-2, 0, 0), Vector.UNIT_X);
        assertTrue(oob.intersects(ray), "Ray should intersect the OOB.");

        // TC02: Ray misses the OOB
        ray = new Ray(new Point(-2, 2, 0), Vector.UNIT_X);
        assertFalse(oob.intersects(ray), "Ray should miss the OOB.");

        // =============== Boundary Values Tests ==================

        // TC03: Ray starts inside the OOB
        ray = new Ray(new Point(0, 0, 0), Vector.UNIT_X);
        assertTrue(oob.intersects(ray), "Ray starting inside the OOB should intersect.");

        // TC04: Ray grazes the edge of the OOB
        ray = new Ray(new Point(-2, 1, 0), Vector.UNIT_X);
        assertTrue(oob.intersects(ray), "Ray grazing the edge of the OOB should intersect.");

        // TC05: Ray is parallel to one of the OOB's faces
        ray = new Ray(new Point(0, 2, 0), Vector.UNIT_Z);
        assertFalse(oob.intersects(ray), "Parallel ray should not intersect the OOB.");
    }

    /**
     * Test method for {@link OOB#translate(Vector)}.
     */
    @Test
    void testTranslate() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 1, 1), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});
        Vector translation = new Vector(2, 3, 4);
        OOB translatedOOB = oob.translate(translation);

        // Ensure the original OOB is unchanged
        assertEquals(new Point(0, 0, 0), oob.getCenterPoint(), "Original OOB center should remain unchanged.");

        // Check the translated OOB
        assertEquals(new Point(2, 3, 4), translatedOOB.getCenterPoint(), "Translated OOB center is incorrect.");
    }

    /**
     * Test method for {@link OOB#rotate(Vector, double)}.
     */
    @Test
    void testRotate() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 1, 1), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});
        Quaternion rotation = Quaternion.fromAxisAngle(Vector.UNIT_Y, Math.toRadians(90));
        OOB rotatedOOB = oob.rotate(rotation);

        // Ensure the original OOB is unchanged
        assertArrayEquals(new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z}, oob.getAxes(), "Original OOB axes should remain unchanged.");

        // Check the rotated axes
        assertEquals(new Vector(0, 0, -1), rotatedOOB.getAxes()[0], "Rotated OOB X-axis is incorrect.");
        assertEquals(Vector.UNIT_Y, rotatedOOB.getAxes()[1], "Rotated OOB Y-axis is incorrect.");
        assertEquals(new Vector(1, 0, 0), rotatedOOB.getAxes()[2], "Rotated OOB Z-axis is incorrect.");
    }

    /**
     * Test method for {@link OOB#scale(Vector)}.
     */
    @Test
    void testScale() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 1, 1), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});
        Vector scaleFactors = new Vector(2, 3, 4);
        OOB scaledOOB = oob.scale(scaleFactors);

        // Ensure the original OOB is unchanged
        assertEquals(new Vector(1, 1, 1), oob.getHalfDimensions(), "Original OOB dimensions should remain unchanged.");

        // Check the scaled OOB
        assertEquals(new Vector(2, 3, 4), scaledOOB.getHalfDimensions(), "Scaled OOB dimensions are incorrect.");
    }

    /**
     * Test method for {@link OOB#surfaceArea()}.
     */
    @Test
    void testSurfaceArea() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 2, 3), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});
        double expectedSurfaceArea = 2 * (1 * 2 + 2 * 3 + 3 * 1); // Surface area of the box
        assertEquals(expectedSurfaceArea, oob.surfaceArea(), "Surface area calculation is incorrect.");
    }

    /**
     * Test method for {@link OOB#expand(Point)}.
     */
    @Test
    void testExpand() {
        OOB oob = new OOB(new Point(0, 0, 0), new Vector(1, 1, 1), new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z});
        Point newPoint = new Point(2, 2, 2);
        oob.expand(newPoint);

        // Verify the new dimensions
        assertEquals(new Vector(2, 2, 2), oob.getHalfDimensions(), "OOB dimensions after expansion are incorrect.");
    }
}

package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link AABB} class.
 *
 * @author
 * Benny Avrahami
 */
class AABBTest {

    /**
     * Test method for AABB construction and expansion.
     */
    @Test
    void testConstructionAndExpansion() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Construct AABB and expand with points
        AABB aabb = new AABB();
        aabb.expand(new Point(1, 1, 1));
        aabb.expand(new Point(-1, -1, -1));
        assertEquals(new Point(-1, -1, -1), aabb.getMin(), "Incorrect minimum point after expansion.");
        assertEquals(new Point(1, 1, 1), aabb.getMax(), "Incorrect maximum point after expansion.");

        // =============== Boundary Values Tests ==================

        // TC10: Expand AABB with the same point repeatedly
        aabb.expand(new Point(1, 1, 1));
        assertEquals(new Point(-1, -1, -1), aabb.getMin(), "Minimum point should remain unchanged.");
        assertEquals(new Point(1, 1, 1), aabb.getMax(), "Maximum point should remain unchanged.");
    }

    /**
     * Test method for {@link AABB#intersects(AABB)}.
     */
    @Test
    void testIntersectsAABB() {
        AABB aabb1 = new AABB(new Point(-1, -1, -1), new Point(1, 1, 1));
        AABB aabb2 = new AABB(new Point(0, 0, 0), new Point(2, 2, 2));
        AABB aabb3 = new AABB(new Point(2, 2, 2), new Point(3, 3, 3));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Intersecting AABBs
        assertTrue(aabb1.intersects(aabb2), "AABBs should intersect.");

        // TC02: Non-intersecting AABBs
        assertFalse(aabb1.intersects(aabb3), "AABBs should not intersect.");

        // =============== Boundary Values Tests ==================

        // TC10: AABBs touching at a single edge
        AABB aabb4 = new AABB(new Point(1, 1, 1), new Point(2, 2, 2));
        assertTrue(aabb1.intersects(aabb4), "AABBs should intersect at their edges.");
    }

    /**
     * Test method for {@link AABB#intersects(Ray)}.
     */
    @Test
    void testIntersectsRay() {
        AABB aabb = new AABB(new Point(-1, -1, -1), new Point(1, 1, 1));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the AABB
        Ray ray1 = new Ray(new Point(0, 0, 2), new Vector(0, 0, -1));
        assertTrue(aabb.intersects(ray1), "Ray should intersect the AABB.");

        // TC02: Ray does not intersect the AABB
        Ray ray2 = new Ray(new Point(2, 2, 2), new Vector(1, 1, 1));
        assertFalse(aabb.intersects(ray2), "Ray should not intersect the AABB.");

        // =============== Boundary Values Tests ==================

        // TC10: Ray starts inside the AABB
        Ray ray3 = new Ray(new Point(0, 0, 0), new Vector(1, 0, 0));
        assertTrue(aabb.intersects(ray3), "Ray starting inside the AABB should intersect.");

        // TC11: Ray grazes the edge of the AABB
        Ray ray4 = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));
        assertTrue(aabb.intersects(ray4), "Ray grazing the edge should intersect.");
    }

    /**
     * Test method for {@link AABB#getCenter()}.
     */
    @Test
    void testGetCenter() {
        AABB aabb = new AABB(new Point(-1, -1, -1), new Point(1, 1, 1));

        // TC01: Center of the AABB
        Point center = aabb.getCenter();
        assertEquals(Point.ZERO, center, "Incorrect center of the AABB.");
    }

    /**
     * Test method for geometric transformations: translate, rotate, and scale.
     */
    @Test
    void testTransformations() {
        AABB aabb = new AABB(new Point(-1, -1, -1), new Point(1, 1, 1));

        // ============ Translation ==============
        AABB translatedAABB = aabb.translate(new Vector(1, 1, 1));
        assertEquals(new Point(0, 0, 0), translatedAABB.getMin(), "Incorrect translation of minimum point.");
        assertEquals(new Point(2, 2, 2), translatedAABB.getMax(), "Incorrect translation of maximum point.");

        // ============ Scaling ==============
        AABB scaledAABB = aabb.scale(new Vector(2, 2, 2));
        assertEquals(new Point(-2, -2, -2), scaledAABB.getMin(), "Incorrect scaling of minimum point.");
        assertEquals(new Point(2, 2, 2), scaledAABB.getMax(), "Incorrect scaling of maximum point.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 1, 0), Math.toRadians(90));
        AABB rotatedAABB = aabb.rotate(rotation);
        assertNotNull(rotatedAABB, "Rotation should produce a valid AABB.");
    }
}

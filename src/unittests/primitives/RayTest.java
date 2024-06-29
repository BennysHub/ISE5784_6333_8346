package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RayTest {

    /**
     * Test method for {@link primitives.Ray#getPoint(double t)}.
     */
    @Test
    void getPointTest() {

        Ray ray = new Ray(new Point(1, 0, 0), new Vector(1, 0, 0));
        // ============ Equivalence Partitions Tests ==============

        // TC01: Positive distance from head
        assertEquals(new Point(3, 0, 0), ray.getPoint(2),
                "Expected point 2 units away from the head in the ray direction");

        // TC02: Negative distance from head
        assertEquals(new Point(-1, 0, 0), ray.getPoint(-2),
                "Expected point 2 units away from the head in the opposite direction");

        // =============== Boundary Values Tests ==================

        // TC03: Zero distance from head (returns the head point)
        assertEquals(ray.getHead(), ray.getPoint(0),
                "Expected head point (distance = 0)");

    }
}
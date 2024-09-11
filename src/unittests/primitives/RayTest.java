package primitives;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for Ray.
 * Author: Benny
 */
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
        assertEquals(ray.getOrigin(), ray.getPoint(0),
                "Expected head point (distance = 0)");

    }

    /**
     * Test method for {@link primitives.Ray#findClosestPoint(List)}.
     */
    @Test
    void findClosestPoint() {
        Ray ray = new Ray(new Point(1, 0, 0), new Vector(0, 0, 1));
        Point p1 = new Point(1, 0, 3);
        Point p2 = new Point(1, 0, 5);
        Point p3 = new Point(1, 0, 7);

        List<Point> list;
        // ============ Equivalence Partitions Tests ==============
        //TC01 the middle point is the closest
        list = List.of(p2, p1, p3);
        assertEquals(p1, ray.findClosestPoint(list), "wrong point");
        // =============== Boundary Values Tests ==================
        //TC02 empty list
        list = List.of();
        assertNull(ray.findClosestPoint(list), "not null");
        //TC03 the first point is the closest
        list = List.of(p1, p2, p3);
        assertEquals(p1, ray.findClosestPoint(list), "wrong point");
        //TC04 the last point is the closest
        list = List.of(p3, p2, p1);
        assertEquals(p1, ray.findClosestPoint(list), "wrong point");
    }
}
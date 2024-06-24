package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometriesTest {

    @Test
    void testConstructor() {
        Sphere s = new Sphere(1, new Point(1.5, 0, 0));
        Triangle t = new Triangle(new Point(3, 0, 2),
                new Point(3, 2, -1),
                new Point(3, -2, -1));
        Plane p = new Plane(new Point(4, 0, 2),
                new Point(4, 2, -1),
                new Point(4, -2, -1));
        Geometries g = new Geometries(s, t, p);
        assertNotNull(g);
    }

    @Test
    void testFindIntersections() {//TODO:
        Sphere s = new Sphere(1, new Point(1.5, 0, 0));
        Triangle t = new Triangle(new Point(3, 0, 2),
                new Point(3, 2, -1),
                new Point(3, -2, -1));
        Plane p = new Plane(new Point(4, 0, 2),
                new Point(4, 2, -1),
                new Point(4, -2, -1));
        // =============== Boundary Values Tests ==================
        //TC01: empty collection
        Geometries empty = new Geometries();
        assertNull(empty.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0))),
                "no intersection");

        Geometries g = new Geometries(s, t, p);
        //TC02: no intersection
        assertNull(g.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(-1, 0, 0))),
                "no intersection");

        var result1 = g.findIntersections(
                new Ray(new Point(3.5, 0, 0),
                        new Vector(1, 0, 0)));
        //TC03: only one intersection
        assertEquals(1, result1.size(), "wrong intersection amount");
        assertEquals(List.of(new Point(4, 0, 0)), result1, "wrong intersection");

        var result2 = g.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0)));
        //TC04: all the geometries intersect
        assertEquals(4, result2.size(), "wrong intersection amount");
        assertEquals(List.of(new Point(0.5, 0, 0),
                new Point(2.5, 0, 0),
                new Point(3, 0, 0),
                new Point(4, 0, 0)
        ), result2, "wrong intersection");

        // ============ Equivalence Partitions Tests ==============
        var result3 = g.findIntersections(new Ray(new Point(2.75, 0, 0), new Vector(1, 0, 0)));
        //TC05: some of the geometries intersect
        assertEquals(4, result3.size(), "wrong intersection amount");
        assertEquals(List.of(new Point(3, 0, 0), new Point(4, 0, 0)), result3,
                "wrong intersection");
    }
}
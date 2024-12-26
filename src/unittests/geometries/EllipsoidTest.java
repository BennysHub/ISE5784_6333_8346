package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Ellipsoid} class.
 * Verifies the behavior of normal computation and ray-ellipsoid intersection logic.
 *
 * @author Benny Avrahami
 */
class EllipsoidTest {

    private static final Point center = new Point(0, 0, 0);
    private static final Vector radii = new Vector(2, 1, 1);
    private static final Ellipsoid ellipsoid = new Ellipsoid(center, radii);

    /**
     * Test method for {@link Ellipsoid#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the ellipsoid's surface (positive x-axis)
        Point p1 = new Point(2, 0, 0);
        assertEquals(new Vector(1, 0, 0), ellipsoid.getNormal(p1),
                "The normal at point (2,0,0) should be (1,0,0).");

        // TC02: Normal at a point on the ellipsoid's surface (positive y-axis)
        Point p2 = new Point(0, 1, 0);
        assertEquals(new Vector(0, 1, 0), ellipsoid.getNormal(p2),
                "The normal at point (0,1,0) should be (0,1,0).");

        // TC03: Normal at a point on the ellipsoid's surface (positive z-axis)
        Point p3 = new Point(0, 0, 1);
        assertEquals(new Vector(0, 0, 1), ellipsoid.getNormal(p3),
                "The normal at point (0,0,1) should be (0,0,1).");

        // =============== Boundary Values Tests ==================

        // TC04: Normal at a point on the ellipsoid's surface (negative x-axis)
        Point p4 = new Point(-2, 0, 0);
        assertEquals(new Vector(-1, 0, 0), ellipsoid.getNormal(p4),
                "The normal at point (-2,0,0) should be (-1,0,0).");

        // TC05: Normal at a point on the ellipsoid's surface (off-axis point)
        Point p5 = new Point(1, 0.5, 0);
        assertEquals(new Vector(1, 0.5, 0).normalize(), ellipsoid.getNormal(p5),
                "The normal at point (1,0.5,0) should be normalized.");
    }

    /**
     * Test method for {@link Ellipsoid#findGeoIntersections(Ray)}.
     */
    @Test
    void testFindGeoIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the ellipsoid at two points
        Ray ray1 = new Ray(new Point(-3, 0, 0), new Vector(1, 0, 0));
        List<Point> result1 = ellipsoid.findGeoIntersections(ray1).stream()
                .map(Intersectable.GeoPoint::point)
                .toList();
        assertNotNull(result1, "Ray should intersect the ellipsoid at two points.");
        assertEquals(2, result1.size(), "Incorrect number of intersection points.");
        assertEquals(List.of(new Point(-2, 0, 0), new Point(2, 0, 0)), result1,
                "Incorrect intersection points for ray intersecting ellipsoid.");

        // TC02: Ray originates inside the ellipsoid
        Ray ray2 = new Ray(new Point(1, 0.5, 0), new Vector(1, 0, 0));
        List<Point> result2 = ellipsoid.findGeoIntersections(ray2).stream()
                .map(Intersectable.GeoPoint::point)
                .toList();
        assertNotNull(result2, "Ray should intersect the ellipsoid at one point.");
        assertEquals(1, result2.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(2, 0.5, 0), result2.getFirst(),
                "Incorrect intersection point for ray originating inside the ellipsoid.");

        // TC03: Ray misses the ellipsoid (no intersections)
        Ray ray3 = new Ray(new Point(3, 3, 0), new Vector(1, 1, 0));
        assertNull(ellipsoid.findGeoIntersections(ray3), "Ray should not intersect the ellipsoid.");

        // =============== Boundary Values Tests ==================

        // TC10: Ray is a tangent to the ellipsoid
        Ray ray4 = new Ray(new Point(0, 1, 2), new Vector(1, -1, 0));
        assertNull(ellipsoid.findGeoIntersections(ray4), "Ray tangent to the ellipsoid should not intersect.");

        // TC11: Ray originates on the ellipsoid and points outward
        Ray ray5 = new Ray(new Point(2, 0, 0), new Vector(1, 0, 0));
        assertNull(ellipsoid.findGeoIntersections(ray5), "Ray starting on the ellipsoid and pointing outward should not intersect.");

        // TC12: Ray originates on the ellipsoid and points inward
        Ray ray6 = new Ray(new Point(2, 0, 0), new Vector(-1, 0, 0));
        List<Point> result6 = ellipsoid.findGeoIntersections(ray6).stream()
                .map(Intersectable.GeoPoint::point)
                .toList();
        assertNotNull(result6, "Ray should intersect the ellipsoid at one point.");
        assertEquals(1, result6.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(-2, 0, 0), result6.getFirst(),
                "Incorrect intersection point for ray originating on the ellipsoid and pointing inward.");
    }


    @Test
    void testTransformations() {
        Ellipsoid ellipsoid = new Ellipsoid(new Point(0, 0, 0), new Vector(1, 2, 3));

        // ============ Translation ==============
        Ellipsoid translatedEllipsoid = (Ellipsoid) ellipsoid.translate(new Vector(1, 1, 1));
        assertEquals(new Point(1, 1, 1), translatedEllipsoid.getCenter(), "Incorrect translation for ellipsoid center.");

        // ============ Scaling ==============
        Ellipsoid scaledEllipsoid = (Ellipsoid) ellipsoid.scale(new Vector(2, 2, 2));
        assertEquals(new Vector(2, 4, 6), scaledEllipsoid.getRadii(), "Incorrect scaling for ellipsoid radii.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 1, 0), Math.toRadians(90));
        Ellipsoid rotatedEllipsoid = (Ellipsoid) ellipsoid.rotate(rotation);
        assertEquals(new Point(0, 0, 0), rotatedEllipsoid.getCenter(), "Rotation should not affect the ellipsoid center.");
    }

}

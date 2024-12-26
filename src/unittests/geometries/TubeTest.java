package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Tube} class.
 *
 * @author
 * Benny Avrahami
 */
class TubeTest {

    /**
     * Test method for {@link geometries.Tube#getNormal(Point)}.
     * This method tests the normal calculation at a point on the tube's surface.
     */
    @Test
    void getNormal() {
        Tube tube = new Tube(1, new Ray(Point.ZERO, new Vector(1, 0, 0)));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the tube's curved surface.
        assertEquals(new Vector(0, 0, 1), tube.getNormal(new Point(1, 0, 1)),
                "The normal at point (1,0,1) should be perpendicular to the tube's surface.");

        // TC02: Normal at a point not directly aligned with the tube's axis.
        assertEquals(new Vector(0, 1, 0), tube.getNormal(new Point(2, 1, 0)),
                "The normal at point (2,1,0) should be perpendicular to the tube's surface.");

        // =============== Boundary Values Tests ==================

        // TC10: Normal at a point very close to the axis ray (near-zero offset).
        assertThrows(IllegalArgumentException.class,
                () -> tube.getNormal(new Point(0.000001, 0, 0)),
                "Expected exception when the point is too close to the tube's axis.");

        // TC11: Normal at a point exactly on the tube's axis.
        assertThrows(IllegalArgumentException.class,
                () -> tube.getNormal(Point.ZERO),
                "Expected exception when the point lies on the tube's axis.");

        // TC12: Normal at a point far from the tube's surface along a perpendicular line.
        assertEquals(new Vector(0, 0, 1), tube.getNormal(new Point(10, 0, 1)),
                "The normal should remain consistent regardless of the tube's length.");
    }

    /**
     * Test method for {@link geometries.Tube#findGeoIntersections(Ray)}.
     * This method tests the intersection logic of rays with the tube.
     */
    @Test
    void findGeoIntersections() {
        Tube tube = new Tube(1, new Ray(Point.ZERO, new Vector(1, 0, 0)));

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray starts outside and intersects the tube at two points
        Ray ray1 = new Ray(new Point(0, 2, 0), new Vector(1, -2, 0));
        List<Intersectable.GeoPoint> result1 = tube.findGeoIntersections(ray1);
        assertNotNull(result1, "Ray should intersect the tube at two points.");
        assertEquals(2, result1.size(), "Ray should intersect the tube at exactly two points.");

        // TC02: Ray starts inside the tube and intersects at one point
        Ray ray2 = new Ray(new Point(0.5, 0.5, 0), new Vector(1, 0, 0));
        List<Intersectable.GeoPoint> result2 = tube.findGeoIntersections(ray2);
        assertNotNull(result2, "Ray should intersect the tube at one point.");
        assertEquals(1, result2.size(), "Ray should intersect the tube at exactly one point.");

        // TC03: Ray starts outside and misses the tube
        Ray ray3 = new Ray(new Point(0, 2, 0), new Vector(0, 1, 0));
        List<Intersectable.GeoPoint> result3 = tube.findGeoIntersections(ray3);
        assertNull(result3, "Ray should not intersect the tube.");

        // =============== Boundary Values Tests ==================

        // TC10: Ray is a tangent to the tube (no intersection)
        Ray ray4 = new Ray(new Point(0, 1, 0), new Vector(1, 0, 0));
        List<Intersectable.GeoPoint> result4 = tube.findGeoIntersections(ray4);
        assertNull(result4, "Ray tangent to the tube should not intersect.");

        // TC11: Ray starts on the tube surface and goes inside
        Ray ray5 = new Ray(new Point(1, 0, 0), new Vector(1, 0, 1));
        List<Intersectable.GeoPoint> result5 = tube.findGeoIntersections(ray5);
        assertNotNull(result5, "Ray starting on the surface should intersect.");
        assertEquals(1, result5.size(), "Ray should intersect the tube at exactly one point.");

        // TC12: Ray starts on the tube surface and goes outside
        Ray ray6 = new Ray(new Point(1, 0, 0), new Vector(1, 1, 1));
        List<Intersectable.GeoPoint> result6 = tube.findGeoIntersections(ray6);
        assertNull(result6, "Ray starting on the surface and going outside should not intersect.");
    }

    @Test
    void testTransformations() {
        Tube tube = new Tube(1, new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)));

        // ============ Translation ==============
        Tube translatedTube = (Tube) tube.translate(new Vector(1, 1, 1));
        assertEquals(new Point(1, 1, 1), translatedTube.getAxisRay().getOrigin(), "Incorrect translation for tube axis origin.");

        // ============ Scaling ==============
        Tube scaledTube = (Tube) tube.scale(new Vector(2, 2, 2));
        assertEquals(2, scaledTube.getRadius(), "Incorrect scaling for tube radius.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 1, 0), Math.toRadians(90));
        Tube rotatedTube = (Tube) tube.rotate(rotation);
        assertEquals(new Vector(0, 0, -1), rotatedTube.getAxisRay().getDirection(), "Incorrect rotation for tube axis direction.");
    }

}

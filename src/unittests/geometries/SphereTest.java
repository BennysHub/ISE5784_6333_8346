package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Sphere} class.
 * Verifies the correct behavior of normal computation and ray-sphere intersection logic.
 *
 * @author Benny Avrahami
 */
class SphereTest {

    private static final Point p100 = new Point(1, 0, 0);
    private static final Vector v001 = Vector.UNIT_Z;
    private static final Sphere sphere = new Sphere(1, p100);

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     * Verifies that the normal at a given point in the sphere is calculated correctly.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the sphere's surface
        assertEquals(
                new Vector(-1, 0, 0),
                sphere.getNormal(Point.ZERO),
                "The normal at point (0,0,0) should be (-1,0,0)."
        );
    }

    /**
     * Test method for {@link Sphere#findGeoIntersections(Ray)}.
     * Verifies ray-sphere intersection behavior in various cases.
     */
    @Test
    void testFindIntersections() {
        // ============ Data for Tests ==============
        final Point gp1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        final Point gp2 = new Point(1.53484692283495, 0.844948974278318, 0);
        final Point gp3 = new Point(1, 0.5, Math.sqrt(3) / 2);
        final List<Point> exp = List.of(gp1, gp2);

        final Vector v310 = new Vector(3, 1, 0);
        final Vector v110 = new Vector(1, 1, 0);
        final Vector v100 = Vector.UNIT_X;
        final Point p01 = new Point(-1, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(
                sphere.findGeoIntersections(new Ray(p01, v110)),
                "Ray's line should be outside the sphere."
        );

        // TC02: Ray starts before and crosses the sphere (2 points)
        List<Point> intersectionPointsTC02 = sphere.findGeoIntersections(new Ray(p01, v310))
                .stream()
                .map(Intersectable.GeoPoint::point)
                .sorted(Comparator.comparingDouble(p -> p.distance(p01)))
                .toList();
        assertEquals(2, intersectionPointsTC02.size(), "Incorrect number of intersection points.");
        assertEquals(exp, intersectionPointsTC02, "Ray should cross the sphere at two points.");

        // TC03: Ray starts inside the sphere (1 point)
        List<Point> insideIntersectionTC03 = sphere.findGeoIntersections(new Ray(new Point(1, 0.5, 0), v001))
                .stream().map(Intersectable.GeoPoint::point).toList();
        assertEquals(1, insideIntersectionTC03.size(), "Incorrect number of intersection points.");
        assertEquals(gp3, insideIntersectionTC03.getFirst(), "Ray should intersect the sphere once from inside.");

        // TC04: Ray starts after the sphere (0 points)
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(3, 0, 0), v100)),
                "Ray should start after the sphere."
        );

        // ============ Boundary Values Tests ==============

        // TC05: Ray starts on the sphere and goes inside (1 point)
        List<Point> startOnSphereInside = sphere.findGeoIntersections(new Ray(new Point(1, 0, -1), v001))
                .stream().map(Intersectable.GeoPoint::point).toList();
        assertEquals(1, startOnSphereInside.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(1, 0, 1), startOnSphereInside.getFirst(), "Ray should intersect inside the sphere.");

        // TC06: Ray starts on the sphere and goes outside (0 points)
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(1, 0, 1), v001)),
                "Ray should start on the sphere and go outside."
        );

        // TC07: Ray starts at the sphere's center (1 point)
        List<Point> startAtCenter = sphere.findGeoIntersections(new Ray(p100, v001))
                .stream().map(Intersectable.GeoPoint::point).toList();
        assertEquals(1, startAtCenter.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(1, 0, 1), startAtCenter.getFirst(), "Ray should intersect the sphere once from the center.");

        // ============ Tangent Cases ==============

        // TC08: Ray is a tangent to the sphere, no intersection
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(1, 0, 1), v100)),
                "Ray tangent to the sphere should not intersect."
        );

        // TC09: Ray starts before the tangent point, no intersection
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(-1, 0, 1), v100)),
                "Ray starting before tangent point should not intersect."
        );

        // TC10: Ray starts after the tangent point, no intersection
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(2, 0, 1), v100)),
                "Ray starting after tangent point should not intersect."
        );

        // ============ Orthogonal Cases ==============

        // TC11: Ray orthogonal to sphere's center line, no intersection
        assertNull(
                sphere.findGeoIntersections(new Ray(new Point(3, 0, 0), Vector.UNIT_Y)),
                "Ray orthogonal to sphere's center line should not intersect."
        );

        // TC12: Ray starts inside the sphere and is orthogonal to the center line
        List<Point> orthogonalInside = sphere.findGeoIntersections(new Ray(new Point(0.5, 0, 0), v001))
                .stream().map(Intersectable.GeoPoint::point).toList();
        assertEquals(1, orthogonalInside.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(0.5, 0, Math.sqrt(3) / 2), orthogonalInside.getFirst(),
                "Ray inside sphere and orthogonal to center line should intersect once.");
    }

    @Test
    void testTransformations() {
        Sphere sphere = new Sphere(1, new Point(0, 0, 0));

        // ============ Translation ==============
        Sphere translatedSphere = (Sphere) sphere.translate(new Vector(1, 1, 1));
        assertEquals(new Point(1, 1, 1), translatedSphere.getCenter(), "Incorrect translation for sphere center.");

        // ============ Scaling ==============
        Sphere scaledSphere = (Sphere) sphere.scale(new Vector(2, 2, 2));
        assertEquals(2, scaledSphere.getRadius(), "Incorrect scaling for sphere radius.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 1, 0), Math.toRadians(90));
        Sphere rotatedSphere = (Sphere) sphere.rotate(rotation);
        assertEquals(new Point(0, 0, 0), rotatedSphere.getCenter(), "Rotation should not affect the center of a sphere.");
    }

}

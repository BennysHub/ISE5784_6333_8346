package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for Sphere geometry.
 */
class SphereTest {

    private static final Point p100 = new Point(1, 0, 0);
    private static final Vector v001 = Vector.UNIT_Z;
    private static final Sphere sphere = new Sphere(1, p100);

    /**
     * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
     * Verifies that the normal at a given point in the sphere is calculated correctly.
     */
    @Test
    void getNormal() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal at a point on the sphere with a center at (1,0,0) and radius 1
        assertEquals(new Vector(-1, 0, 0), sphere.getNormal(Point.ZERO),
                "The normal to the sphere at point (0,0,0) should be (1,0,0)");
    }

    /**
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {

        // ============ Data for Tests ==============
        // Points on sphere
        final Point gp1 = new Point(0.0651530771650466, 0.355051025721682, 0);
        final Point gp2 = new Point(1.53484692283495, 0.844948974278318, 0);
        final Point gp3 = new Point(1, 0.5, Math.sqrt(3) / 2);
        final var exp = List.of(gp1, gp2);

        final Vector v310 = new Vector(3, 1, 0);
        final Vector v110 = new Vector(1, 1, 0);
        final Vector v100 = Vector.UNIT_X;
        final Point p01 = new Point(-1, 0, 0);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray's line is outside the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray's line should be outside the sphere");

        // TC02: Ray starts before and crosses the sphere (2 points)
        List<Point> intersectionPointsTC02 = Objects.requireNonNull(sphere.findIntersections(new Ray(p01, v310)))
                .stream().sorted(Comparator.comparingDouble(p -> p.distance(p01))).toList();
        assertEquals(2, intersectionPointsTC02.size(), "Incorrect number of intersection points");
        assertEquals(exp, intersectionPointsTC02, "Ray should cross the sphere");

        // TC03: Ray starts inside the sphere (1 point)
        List<Point> insideIntersectionTC03 = sphere.findIntersections(new Ray(new Point(1, 0.5, 0), v001));
        assert insideIntersectionTC03 != null;
        assertEquals(1, insideIntersectionTC03.size(), "Incorrect number of intersection points");
        assertEquals(gp3, insideIntersectionTC03.getFirst(), "Ray should start inside the sphere");

        // TC04: Ray starts after the sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray should start after the sphere");

        // Boundary Values Tests (Ray's line crosses the sphere but not the center)

        // TC11: Ray starts at a sphere and goes inside (1 point)
        List<Point> startAtSphereInside = sphere.findIntersections(new Ray(new Point(1, 0, -1), v001));
        assert startAtSphereInside != null;
        assertEquals(1, startAtSphereInside.size(), "Incorrect number of intersection points");
        assertEquals(new Point(1, 0, 1), startAtSphereInside.getFirst(), "Ray should start at the sphere and go inside");

        // TC12: Ray starts at a sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(Point.ZERO, new Vector(-1, -2, 0))), "Ray should start at the sphere and go outside");

        // **** Group: Ray's line goes through the center

        // TC13: Ray starts before the sphere (2 points)
        List<Point> throughCenterBefore = sphere.findIntersections(new Ray(p01, v100));
        assert throughCenterBefore != null;
        assertEquals(2, throughCenterBefore.size(), "Incorrect number of intersection points");
        assertEquals(Point.ZERO, throughCenterBefore.get(0), "Ray should go through the center before the sphere");
        assertEquals(new Point(2, 0, 0), throughCenterBefore.get(1), "Ray should go through the center before the sphere");

        // TC14: Ray starts at a sphere and goes inside (1 point)
        List<Point> throughCenterInside = sphere.findIntersections(new Ray(Point.ZERO, v100));
        assert throughCenterInside != null;
        assertEquals(1, throughCenterInside.size(), "Incorrect number of intersection points");
        assertEquals(new Point(2, 0, 0), throughCenterInside.getFirst(), "Ray should start at the sphere and go inside");

        // TC15: Ray starts inside (1 point)
        List<Point> throughCenterStartInside = sphere.findIntersections(new Ray(new Point(0.5, 0, 0), v100));
        assert throughCenterStartInside != null;
        assertEquals(1, throughCenterStartInside.size(), "Incorrect number of intersection points");
        assertEquals(new Point(2, 0, 0), throughCenterStartInside.getFirst(), "Ray should start inside the sphere");

        // TC16: Ray starts at the center (1 point)
        List<Point> startAtCenter = sphere.findIntersections(new Ray(p100, v001));
        assert startAtCenter != null;
        assertEquals(1, startAtCenter.size(), "Incorrect number of intersection points");
        assertEquals(new Point(1, 0, 1), startAtCenter.getFirst(), "Ray should start at the center");

        // TC17: Ray starts at a sphere and goes outside (0 points)
        assertNull(sphere.findIntersections(new Ray(Point.ZERO, v100.scale(-1))), "Ray should start at the sphere and go outside");

        // TC18: Ray starts after a sphere (0 points)
        assertNull(sphere.findIntersections(new Ray(p01, v100.scale(-1))), "Ray should start after the sphere");

        // **** Group: Ray's line is tangent to the sphere (all tests 0 points)

        // TC19: Ray starts before the tangent point (1,0,1)
        assertNull(sphere.findIntersections(new Ray(new Point(-1, 0, 1), v100)), "Ray should start before the tangent point");

        // TC20: Ray starts at the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(1, 0, 1), v100)), "Ray should start at the tangent point");

        // TC21: Ray starts after the tangent point
        assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 1), v100)), "Ray should start after the tangent point");

        // **** Group: Special cases

        // TC22: Ray's line is outside, ray is orthogonal to ray start to sphere's center line
        Point orthogonalStart = new Point(3, 0, 0); // Example orthogonal start point
        Vector orthogonalDirection = Vector.UNIT_Y; // Example orthogonal direction
        assertNull(sphere.findIntersections(new Ray(orthogonalStart, orthogonalDirection)),
                "Ray should be orthogonal to sphere's center line");

        // TC23: Ray's is inside, ray is orthogonal to ray start to sphere's center line
        List<Point> startAInsideOrthogonal = sphere.findIntersections(new Ray(new Point(0.5, 0, 0), v001));
        assert startAInsideOrthogonal != null;
        assertEquals(1, startAInsideOrthogonal.size(), "Incorrect number of intersection points");
        assertEquals(new Point(0.5, 0, Math.sqrt(3) / 2), startAInsideOrthogonal.getFirst(), "Ray should start at the center");
    }
}


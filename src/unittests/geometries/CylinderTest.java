package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

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
        var cylinder = new Cylinder(1, new Ray(Point.ZERO, new Vector(1, 0, 0)), 2);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Normal on the curved surface of the cylinder.
        assertEquals(new Vector(0, 0, 1), cylinder.getNormal(new Point(1, 0, 1)),
                "Normal calculation is incorrect for a point on the curved surface.");

        // TC02: Normal on the front base of the cylinder.
        assertEquals(new Vector(-1, 0, 0), cylinder.getNormal(new Point(0, 0.5, 0.5)),
                "Normal calculation is incorrect for a point on the front base.");

        // TC03: Normal on the back base of the cylinder.
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(2, 0.5, 0.5)),
                "Normal calculation is incorrect for a point on the back base.");

        // =============== Boundary Values Tests ==================

        // TC04: Normal at the center front base of the cylinder.
        assertEquals(new Vector(-1, 0, 0), cylinder.getNormal(Point.ZERO),
                "Normal calculation is incorrect for the center of the front base.");

        // TC05: Normal at the center back base of the cylinder.
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(2, 0, 0)),
                "Normal calculation is incorrect for the center of the back base.");

        // TC06: Normal on the edge front base of the cylinder.
        assertEquals(new Vector(-1, 0, 0), cylinder.getNormal(new Point(0, 0, 1)),
                "Normal calculation is incorrect for an edge point on the front base.");

        // TC07: Normal on the-edge back base of the cylinder.
        assertEquals(new Vector(1, 0, 0), cylinder.getNormal(new Point(2, 0, 1)),
                "Normal calculation is incorrect for an edge point on the back base.");
    }

    /**
     * Test method for {@link geometries.Cylinder#findGeoIntersections(primitives.Ray, double)}.
     */
    @Test
    void findGeoIntersectionsTest() {
        var cylinder = new Cylinder(1, new Ray(Point.ORIGIN, new Vector(1, 0, 0)), 2);

        // ============ Equivalence Partitions Tests ==============

        // TC01: Ray intersects the curved surface twice
        var result1 = cylinder.findGeoIntersections(new Ray(new Point(-1, 0, 0.5), new Vector(1, 0, 0)), 5);
        assertNotNull(result1, "Ray should intersect the curved surface twice.");
        assertEquals(2, result1.size(), "Incorrect number of intersection points.");
        assertEquals(List.of(new Point(0, 0, 0.5), new Point(2, 0, 0.5)),
                result1.stream().map(Intersectable.GeoPoint::point).toList(),
                "Incorrect intersection points on the curved surface.");

        // TC02: Ray intersects the front base
        var result2 = cylinder.findGeoIntersections(new Ray(new Point(1, 0.5, 0.5), new Vector(-1, 0, 0)), 5);
        assertNotNull(result2, "Ray should intersect the front base.");
        assertEquals(1, result2.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(0, 0.5, 0.5), result2.getFirst().point(),
                "Incorrect intersection point on the front base.");

        // TC03: Ray intersects the back base
        var result3 = cylinder.findGeoIntersections(new Ray(new Point(1, 0.5, 0.5), new Vector(1, 0, 0)), 5);
        assertNotNull(result3, "Ray should intersect the back base.");
        assertEquals(1, result3.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(2, 0.5, 0.5), result3.getFirst().point(),
                "Incorrect intersection point on the back base.");

        // =============== Boundary Values Tests ==================

        // TC10: Ray is a tangent to the curved surface
        var result4 = cylinder.findGeoIntersections(new Ray(new Point(0, 1, 0.5), new Vector(1, 0, 0)), 5);
        assertNull(result4, "Ray tangent to the surface should have no intersection.");

        // TC11: Ray starts on the surface and points outward
        var result5 = cylinder.findGeoIntersections(new Ray(new Point(1, 0, 1), new Vector(1, 1, 0)), 5);
        assertNull(result5, "Ray starting on the surface and pointing outward should have no intersection.");

        // TC12: Ray starts inside the cylinder and intersects the back base center
        var result6 = cylinder.findGeoIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 0, 0)), 5);
        assertNotNull(result6, "Ray should intersect the back base from inside.");
        assertEquals(1, result6.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(2, 0, 0), result6.getFirst().point(),
                "Incorrect intersection point on the back base.");

        // TC12: Ray starts at the origin
        var result7 = cylinder.findGeoIntersections(new Ray(Point.ORIGIN, new Vector(1, 0, 0)), 5);
        assertNotNull(result7, "Ray should intersect the back base from inside.");
        assertEquals(1, result7.size(), "Incorrect number of intersection points.");
        assertEquals(new Point(2, 0, 0), result7.getFirst().point(),
                "Incorrect intersection point on the back base.");
    }

    @Test
    void testTransformations() {
        Cylinder cylinder = new Cylinder(1, new Ray(new Point(0, 0, 0), new Vector(1, 0, 0)), 2);

        // ============ Translation ==============
        Cylinder translatedCylinder = (Cylinder) cylinder.translate(new Vector(1, 1, 1));
        assertEquals(new Point(1, 1, 1), translatedCylinder.getAxisRay().getOrigin(), "Incorrect translation for cylinder axis origin.");

        // ============ Scaling ==============
        Cylinder scaledCylinder = (Cylinder) cylinder.scale(new Vector(2, 2, 2));
        assertEquals(2, scaledCylinder.getRadius(), "Incorrect scaling for cylinder radius.");
    //    assertEquals(4, scaledCylinder.getHigh(), "Incorrect scaling for cylinder height.");

        // ============ Rotation ==============
        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 1, 0), Math.toRadians(90));
        Cylinder rotatedCylinder = (Cylinder) cylinder.rotate(rotation);
        assertEquals(new Vector(0, 0, -1), rotatedCylinder.getAxisRay().getDirection(), "Incorrect rotation for cylinder axis direction.");
    }

}

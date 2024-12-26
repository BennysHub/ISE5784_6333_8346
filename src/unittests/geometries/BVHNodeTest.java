package geometries;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BVHNode} class.
 *
 * @author Benny
 */
class BVHNodeTest {

    /**
     * Test method for BVH construction and intersection detection.
     */
    @Test
    void testBVHConstructionAndIntersection() {
        // Create geometries
        Geometry sphere1 = new Sphere(1, new Point(0, 0, 0));
        Geometry sphere2 = new Sphere(1, new Point(2, 0, 0));
        Geometry triangle = new Triangle(
                new Point(1, 1, 0),
                new Point(3, 1, 0),
                new Point(2, 3, 0)
        );

        // Construct BVHNode
        BVHNode bvhNode = new BVHNode(List.of(sphere1, sphere2, triangle), BVHNode.BVHBuildMethod.SAH);

        // Test ray intersection
        Ray intersectingRay = new Ray(new Point(0, 0, 5), new Vector(0, 0, -1));
        List<Intersectable.GeoPoint> intersections = bvhNode.findIntersections(intersectingRay, Double.POSITIVE_INFINITY);
        assertNotNull(intersections, "Ray should intersect BVH geometries.");
        assertEquals(2, intersections.size(), "Incorrect number of intersections detected.");
    }

    /**
     * Test method for BVH transformations.
     */
//    @Test
//    void testTransformations() {
//        // Create geometries
//        Geometry sphere = new Sphere(1, new Point(0, 0, 0));
//        Geometry triangle = new Triangle(
//                new Point(1, 1, 0),
//                new Point(3, 1, 0),
//                new Point(2, 3, 0)
//        );
//
//        // Construct BVHNode
//        BVHNode bvhNode = new BVHNode(List.of(sphere, triangle), BVHNode.BVHBuildMethod.SAH);
//
//        // ============ Translation ==============
//        BVHNode translatedBVH = bvhNode.translate(new Vector(1, 1, 1));
//        assertEquals(new Point(1, 1, 1), ((Sphere) translatedBVH.getGeometries()[0]).getCenter(),
//                "Sphere center translation incorrect.");
//        assertEquals(new Point(2, 2, 1), ((Triangle) translatedBVH.getGeometries()[1]).getVertices().get(0),
//                "Triangle vertex translation incorrect.");
//
//        // ============ Scaling ==============
//        BVHNode scaledBVH = bvhNode.scale(new Vector(2, 2, 2));
//        assertEquals(2, ((Sphere) scaledBVH.getGeometries()[0]).getRadius(), "Sphere scaling incorrect.");
//        assertEquals(new Point(2, 2, 0), ((Triangle) scaledBVH.getGeometries()[1]).getVertices().get(0),
//                "Triangle vertex scaling incorrect.");
//
//        // ============ Rotation ==============
//        Quaternion rotation = Quaternion.fromAxisAngle(new Vector(0, 0, 1), Math.toRadians(90));
//        BVHNode rotatedBVH = bvhNode.rotate(rotation);
//        assertEquals(new Point(0, 0, 0), ((Sphere) rotatedBVH.getGeometries()[0]).getCenter(),
//                "Sphere center rotation incorrect.");
//        assertEquals(new Point(-1, 1, 0), ((Triangle) rotatedBVH.getGeometries()[1]).getVertices().get(0),
//                "Triangle vertex rotation incorrect.");
//    }

    /**
     * Test method for BVH intersection optimization.
     */
    @Test
    void testIntersectionOptimization() {
        // Create geometries
        Geometry sphere1 = new Sphere(1, new Point(0, 0, 0));
        Geometry sphere2 = new Sphere(1, new Point(2, 0, 0));

        // Construct BVHNode
        BVHNode bvhNode = new BVHNode(List.of(sphere1, sphere2), BVHNode.BVHBuildMethod.SAH);

        // Test ray that does not intersect any geometry
        Ray nonIntersectingRay = new Ray(new Point(0, 0, 5), new Vector(1, 0, 0));
        List<Intersectable.GeoPoint> nonIntersections = bvhNode.findIntersections(nonIntersectingRay, Double.POSITIVE_INFINITY);
        assertNull(nonIntersections, "Ray should not intersect any geometry.");

        // Test ray intersecting only one geometry
        Ray singleIntersectingRay = new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0));
        List<Intersectable.GeoPoint> singleIntersections = bvhNode.findIntersections(singleIntersectingRay, Double.POSITIVE_INFINITY);
        assertNotNull(singleIntersections, "Ray should intersect one geometry.");
        assertEquals(2, singleIntersections.size(), "Ray should intersect the sphere at two points.");
    }
}

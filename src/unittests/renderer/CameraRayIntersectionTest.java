package renderer;

import geometries.Intersectable;
import geometries.Plane;
import geometries.Sphere;
import geometries.Triangle;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import scene.Scene;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the Camera class's ability to calculate intersections with various geometries.
 */
public class CameraRayIntersectionTest {
    /**
     * The camera instance to use in all the tests with predefined location and direction.
     */
    final private Camera camera =
            Camera.getBuilder()
                    .setRayTracer(new SimpleRayTracer(new Scene("Test")))
                    .setImageWriter(new ImageWriter("Test", 1, 1))
                    .setLocation(new Point(0, 0, 0.5))
                    .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                    .setVpDistance(1).setVpSize(3, 3).build();

    /**
     * Sums up the number of intersection points found between a camera ray and a given geometry.
     *
     * @param nX            the number of horizontal pixels.
     * @param nY            the number of vertical pixels.
     * @param intersectable the geometry to intersect with the camera rays.
     * @return the total number of intersections found.
     */
    int sumIntersection(int nX, int nY, Intersectable intersectable) {
        int numOfIntersection = 0;
        for (int i = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++) {
                var intersections = intersectable.findIntersections(camera.constructRay(nX, nY, i, j));
                if (intersections != null)
                    numOfIntersection += intersections.size();
            }
        }
        return numOfIntersection;
    }

    /**
     * Asserts the expected number of intersections between camera rays and a given geometry.
     *
     * @param nX                    the number of horizontal pixels.
     * @param nY                    the number of vertical pixels.
     * @param intersectable         the geometry to intersect with the camera rays.
     * @param expectedIntersections the expected number of intersection points.
     */
    void assertTest(int nX, int nY, Intersectable intersectable, int expectedIntersections) {
        int result = sumIntersection(nX, nY, intersectable);
        assertEquals(expectedIntersections, result, "There supposed to be exactly " + expectedIntersections + " intersection points");
    }

    /**
     * Test method for camera-ray-sphere intersections.
     */
    @Test
    void testCameraSphereIntersection() {
        assertTest(3, 3, new Sphere(1, new Point(0, 0, -3)), 2);
        assertTest(3, 3, new Sphere(2.5, new Point(0, 0, -2.5)), 18);
        assertTest(3, 3, new Sphere(2, new Point(0, 0, -2)), 10);
        assertTest(3, 3, new Sphere(4, new Point(0, 0, 1.5)), 9);
        assertTest(3, 3, new Sphere(0.5, new Point(0, 0, 1)), 0);
    }

    /**
     * Test method for camera-ray-plane intersections.
     */
    @Test
    void testCameraPlaneIntersection() {
        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, 0, 1)), 9);
        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, 1, -2)), 9);
        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, -1, 1)), 6);
    }

    /**
     * Test method for camera-ray-triangle intersections.
     */
    @Test
    void testCameraTriangleIntersection() {
        assertTest(3, 3, new Triangle(new Point(1, -1, -2), new Point(0, 1, -2), new Point(-1, -1, -2)), 1);
        assertTest(3, 3, new Triangle(new Point(1, -1, -2), new Point(0, 20, -2), new Point(-1, -1, -2)), 2);
        assertTest(4, 4, new Triangle(new Point(1, -1, -2), new Point(0, 20, -2), new Point(-1, -1, -2)), 2);
    }
}
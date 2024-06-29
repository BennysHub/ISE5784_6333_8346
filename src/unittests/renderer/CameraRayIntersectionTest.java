package renderer;

import geometries.Intersectable;
import geometries.Plane;
import geometries.Sphere;
import geometries.Triangle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import primitives.Point;
import primitives.Vector;

public class CameraRayIntersectionTest {
    final private Camera camera =
            Camera.getBuilder().setLocation(new Point(0,0,0.5)).setDirection(new Vector(1, 0, 0), new Vector(0, 1, 0)).setVpDistance(1).setVpSize(3, 3).build();


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

    void assertTest(int nX, int nY, Intersectable intersectable, int expectedIntersections) {
        int result = sumIntersection(3, 3, intersectable);
        assertEquals(expectedIntersections, result, "There supposed to be exactly" + expectedIntersections + "intersection points");
    }


    @Test
    void testCameraSphereIntersection() {

        assertTest(3, 3, new Sphere(1, new Point(0, 0, -3)), 2);
        assertTest(3, 3, new Sphere(2.5, new Point(0, 0, -2.5)), 18);
        assertTest(3, 3, new Sphere(2, new Point(0, 0, -2)), 10);
        assertTest(3, 3, new Sphere(4, new Point(0, 0, 1.5)), 9);
        assertTest(3, 3, new Sphere(0.5, new Point(0, 0, 1)), 0);

    }

    @Test
    void testCameraPlaneIntersection() {

        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, 0, 1)), 9);
        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, 1, -2)), 9);
        assertTest(3, 3, new Plane(new Point(0, 0, -3), new Vector(0, -1, 1)), 6);

    }

    @Test
    void testCameraTriangleIntersection() {

        assertTest(3, 3, new Triangle(new Point(1, -1, -2), new Point(0, 1, -2), new Point(-1, -1, -2)), 1);
        assertTest(3, 3, new Triangle(new Point(1, -1, -2), new Point(0, 20, -2), new Point(-1, -1, -2)), 2);
    }
}

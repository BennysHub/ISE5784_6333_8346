package renderer;

import primitives.Point;
import primitives.Vector;
import scene.Scene;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import scene.Scene;

/**
 * Testing Camera Class
 *
 * @author Dan
 */
class CameraTest {
    /**
     * Camera builder for the tests
     */

    private final Camera.Builder cameraBuilder = Camera.getBuilder()
            .setLocation(Point.ZERO)
            .setDirection(new Vector(0, 0, -1), new Vector(0, -1, 0))
            .setScene(new Scene("Test"))
            .setResolution("Test", 4, 4)
            .setVpDistance(10);

    /**
     * Test method for
     * {@link renderer.Camera#constructRay(int, int)}.
     */
//    @Test
//    void testConstructRay() {
//        final String badRay = "Bad ray";
//
//        // ============ Equivalence Partitions Tests ==============
//        // EP01: 4X4 Inside (1,1)
//        Camera camera1 = cameraBuilder.setVpSize(8, 8).setImageWriter(new ImageWriter("Test 4x4", 4, 4)).build();
//        assertEquals(new Ray(Point.ZERO, new Vector(1, -1, -10)),
//                camera1.constructRay(1, 1), badRay);
//
//        // =============== Boundary Values Tests ==================
//        // BV01: 4X4 Corner (0,0)
//        assertEquals(new Ray(Point.ZERO, new Vector(3, -3, -10)),
//                camera1.constructRay( 0, 0), badRay);
//
//        // BV02: 4X4 Side (0,1)
//        assertEquals(new Ray(Point.ZERO, new Vector(1, -3, -10)),
//                camera1.constructRay( 1, 0), badRay);
//
//        // BV03: 3X3 Center (1,1)
//        Camera camera2 = cameraBuilder.setVpSize(6, 6).setImageWriter(new ImageWriter("Test 3x3", 3, 3)).build();
//        assertEquals(new Ray(Point.ZERO, new Vector(0, 0, -10)),
//                camera2.constructRay( 1, 1), badRay);
//
//        // BV04: 3X3 Center of Upper Side (0,1)
//        assertEquals(new Ray(Point.ZERO, new Vector(0, -2, -10)),
//                camera2.constructRay( 1, 0), badRay);
//
//        // BV05: 3X3 Center of Left Side (1,0)
//        assertEquals(new Ray(Point.ZERO, new Vector(2, 0, -10)),
//                camera2.constructRay(0, 1), badRay);
//
//        // BV06: 3X3 Corner (0,0)
//        assertEquals(new Ray(Point.ZERO, new Vector(2, -2, -10)),
//                camera2.constructRay(0, 0), badRay);
//
//    }

}

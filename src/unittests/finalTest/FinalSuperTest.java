package finalTest;

import geometries.Ellipsoid;
import geometries.Geometry;
import geometries.Sphere;
import lighting.AmbientLight;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.QualityLevel;
import scene.Scene;
import scene.SceneJsonParser;


public class FinalSuperTest {
    private static final int SNOW_GLOBE_FRAMES = 1;
    int SNOW_AMOUNT = 100000;// 400000;
    Scene scene = new SceneJsonParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene");


    /**
     * test the turnaround rendering of a complex scene with BVH and soft shadows
     */
    @Test
    public void bothSnowGlobe() {

        Point center = new Point(0, 40, 0); // Center of the circular path


        //snow cover
//        Vector down = new Vector(0, -1, 0);
//        Material snow = new Material().setKd(1d).setKs(0.1);
//        List<Point> cloud = null;

//        for (Point p : cloud) {
//            Ray r = new Ray(p, down);
//            Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
//            scene.geometries.add(new Sphere(0.2, s.point()).setMaterial(snow));
//        }

        Camera.Builder camera = Camera.builder()
                .setViewPlaneDistance(140)
                .setViewPlaneSize(320, 320)
                .setResolution(1440, 1440)
                .enableParallelStreams(true)
                .enableSoftShadows(false)
                .setSoftShadowsQuality(QualityLevel.MEDIUM)
                .enableAntiAliasing(true)
                .setAntiAliasingQuality(QualityLevel.LOW)
                .setScene(scene)
                .enableBVH(true);


        double radius = 100; // Radius of the circular path
        double angleStep = Math.PI / (SNOW_GLOBE_FRAMES / 2d);// / steps ;
        for (int i = 0; i < SNOW_GLOBE_FRAMES; i++) {
            double angle = i * angleStep;

            // Update the point's position
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            camera.
                    setPosition(new Point(x, 50, z))
                    .setOrientation(center, Vector.UNIT_Y);
            camera
                    .setImageName("snowGlobe" + i)
                    .build()
                    .renderImage()
                    .writeToImage();
            System.out.println("frame - " + i);
        }
    }


    @Test
    @Disabled("incomplete")
    void oneTestToRuleThemAll() {
        Scene scene = new SceneJsonParser("src/unittests/finalTest/objects.json", "Test Scene");
        int scale = 30;
        Camera.Builder camera = Camera.builder()
                .setPosition(new Point(-18 * scale, 6 * scale, -27 * scale))
                .setOrientation(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                //.setTarget(new Point(0, 1, 0))
                .setViewPlaneDistance(1500)
                .setViewPlaneSize(360, 360)
                .enableBVH(true)
                .setScene(scene);

        camera.setResolution(480 * 2, 270 * 2)
                .setImageName("finalTest")
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    public void testExecutionTime() {
        long startTime = System.nanoTime();

        // Code block whose performance you want to measure
        performTimeConsumingOperation();

        long endTime = System.nanoTime();
        long duration = endTime - startTime; // Duration in nanoseconds

        // Convert duration to milliseconds for easier interpretation
        double durationInMilliseconds = duration / 1_000_000.0;

        // Assert that the duration is within acceptable limits
        System.out.printf("Execution time: %.3f ms%n", durationInMilliseconds);
        //assertTrue(durationInMilliseconds < 1000, "Execution time exceeds the limit of 1000 ms");
    }

    private void performTimeConsumingOperation() {

        Camera.Builder camera = Camera.builder()
                .setPosition(new Point(-750, 600, -900))
                .setOrientation(new Point(0, 30, 0), Vector.UNIT_Y)
                .setViewPlaneDistance(500)
                .setViewPlaneSize(30, 56)
                .setScene(scene)
                .enableParallelStreams(true)
                .enableSoftShadows(false)
                .enableAntiAliasing(false)
                .setAntiAliasingQuality(QualityLevel.HIGH)
                .enableBVH(true)
                .setResolution(2560, 1440)
                .setImageName("snowGlobeBuildTime");

        for (int i = 0; i < SNOW_GLOBE_FRAMES; ++i)
            camera.build().renderImage().writeToImage();
    }

    @Test
    public void EllipsoidTest() {
        Scene testScene = new Scene("ad");
        Material material = new Material().setKd(0.05).setKs(0.1).setShininess(30).setEmission(new Color(java.awt.Color.BLUE));
        Geometry ellipsoid = new Ellipsoid(Point.ORIGIN, new Vector(20, 40, 20)).setMaterial(material);
        Geometry sphere = new Sphere(20, Point.ORIGIN).setMaterial(material);
        testScene.geometries.add(ellipsoid);
       // testScene.setAmbientLight(new AmbientLight(new Color(255,255,255), 1d));

        Camera.Builder camera = Camera.builder()
                .setPosition(new Point(200, 0, 0))
                .setViewPlaneDistance(50)
                .setOrientation(new Vector(-1, 0, 0), Vector.UNIT_Z)
                .setViewPlaneSize(40, 40)
                .setResolution(1080, 1080)
                .setScene(testScene)
                .setImageName("Ellipsoid");

        camera.build().renderImage().writeToImage();


    }
}


package finalTest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.QualityLevel;
import renderer.RenderSettings;
import scene.JsonSceneParser;
import scene.Scene;


public class FinalTest {
    int SNOW_AMOUNT = 100000;// 400000;
    private static int SNOW_GLOBE_FRAMES = 1;

    Scene scene = new JsonSceneParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene");


    /**
     * test the turnaround rendering of a complex scene with BVH and soft shadows
     */
    @Test
    public void bothSnowGlobe() {

        //snow cover
//        Vector down = new Vector(0, -1, 0);
//        Material snow = new Material().setKd(1d).setKs(0.1);
//        List<Point> cloud = Blackboard.getPointsOnCircle(
//                down, new Point(0, 100, 0), 50, SNOW_AMOUNT);

//        for (Point p : cloud) {
//            Ray r = new Ray(p, down);
//            Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
//            scene.geometries.add(new Sphere(0.2, s.point).setMaterial(snow));
//        }

        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-75, 60, -90))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 30, 0))
                .setVpDistance(150)
                .setVpSize(300, 300)

                //.setMultiThreading(8)
                //.setSoftShadows(true)
                .setScene(scene)
                .setBVH(true)
                .setResolution( 600, 600)
                .setImageName("snowGlobe_both");

        camera.build()
                .renderImage()
                .writeToImage();

        //set steps to more than 0 to use this test
        if (SNOW_GLOBE_FRAMES == 0) return;
        Point center = new Point(0, 30, 0); // Center of the circular path
        double radius = 35; // Radius of the circular path
        int steps = SNOW_GLOBE_FRAMES; // Number of steps for one complete revolution
        double angleStep = Math.PI / (steps / 2d);// / steps ;

        for (int i = 0; i < steps; i++) {
            double angle = i * angleStep;

            // Update the point's position
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            camera.setLocation(new Point(x * 3, 60, z * 3)).setTarget(center);

            camera.setResolution( 1920, 1080)
                    .setImageName("snowGlobe/turnaround_" + i)
                    .build()
                    .renderImage()
                    .writeToImage();
            System.out.println("frame - " + i);
        }
    }


    @Test
    @Disabled("incomplete")
    void oneTestToRuleThemAll() {
        Scene scene = new JsonSceneParser("src/unittests/finalTest/objects.json", "Test Scene");
        int scale = 30;
        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-18 * scale, 6 * scale, -27 * scale))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 1, 0))
                .setVpDistance(1500)
                .setVpSize(360, 360)
                .setBVH(true)
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

        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-75, 60, -90))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 30, 0))
                .setVpDistance(150)
                .setVpSize(300, 300)
                .setScene(scene)
                .setParallelStreams(true)
                .setSoftShadows(false)
                .setAntiAliasing(true)
                .setAntiAliasingQuality(QualityLevel.ULTRA)
                .setBVH(true)
                .setResolution(1440, 1440)
                .setImageName("snowGlobeBuildTime");

        for (int i = 0; i < SNOW_GLOBE_FRAMES; ++i)
            camera.build().renderImage().writeToImage();
    }
}


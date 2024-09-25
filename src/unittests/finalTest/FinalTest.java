package finalTest;

import geometries.Intersectable;
import geometries.Sphere;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.Camera;
import renderer.ImageWriter;
import renderer.SimpleRayTracer;
import renderer.super_sampling.Blackboard;
import scene.JsonSceneParser;
import scene.Scene;

import java.util.List;


public class FinalTest {
    int SNOW_AMOUNT = 100000;// 400000;
    private static int SNOW_GLOBE_FRAMES = 1;

    JsonSceneParser jsp = new JsonSceneParser("src/unittests/renderer/json/snowGlobe.json");
    Scene scene = jsp.scene;


    /**
     * test the turnaround rendering of a complex scene with BVH and soft shadows
     */
    @Test
    public void bothSnowGlobe() {

        //snow cover
        Vector down = new Vector(0, -1, 0);
        Material snow = new Material().setKd(1d).setKs(0.1);
        List<Point> cloud = Blackboard.getPointsOnCircle(
                down, new Point(0, 100, 0), 50, SNOW_AMOUNT);

        for (Point p : cloud) {
            Ray r = new Ray(p, down);
            Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
            scene.geometries.add(new Sphere(0.2, s.point).setMaterial(snow));
        }
        //glass cover
        scene.geometries.add(
                new Sphere(60, new Point(0, 40, 0))
                        .setMaterial(new Material().setKd(0d).setKs(0.9).setKt(0.9).setKr(0.2).setShininess(30)));

        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-75, 60, -90))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 30, 0))
                .setVpDistance(150)
                .setVpSize(300, 300)

                //.setMultiThreading(8)
                //.setSoftShadows(true)
                .setRayTracer(new SimpleRayTracer(scene))
                .setBVH(true)
                .setImageWriter(new ImageWriter("snowGlobe_both", 600, 600));

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

            camera.setImageWriter(new ImageWriter("snowGlobe/turnaround_" + i, 1920, 1080))
                    .build()
                    .renderImage()
                    .writeToImage();
            System.out.println("frame - " + i);
        }
    }


    @Test
    @Disabled("incomplete")
    void oneTestToRuleThemAll() {
        JsonSceneParser jsp = new JsonSceneParser("src/unittests/finalTest/objects.json");
        Scene scene = jsp.scene;
        int scale = 30;
        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-18 * scale, 6 * scale, -27 * scale))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 1, 0))
                .setVpDistance(1500)
                .setVpSize(360, 360)
                .setBVH(true)
                .setRayTracer(new SimpleRayTracer(scene));

        camera.setImageWriter(new ImageWriter("finalTest", 480 * 2, 270 * 2))
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
        Vector down = new Vector(0, -1, 0);
        Material snow = new Material().setKd(1d).setKs(0.1);
        List<Point> cloud = Blackboard.getPointsOnCircle(
                down, new Point(0, 100, 0), 50, SNOW_AMOUNT);

//        for (Point p : cloud) {
//            Ray r = new Ray(p, down);
//            Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
//            scene.geometries.add(new Sphere(0.2, s.point).setMaterial(snow));
//        }
        //glass cover
        scene.geometries.add(
                new Sphere(60, new Point(0, 40, 0))
                        .setMaterial(new Material().setKd(0d).setKs(0.9).setKt(0.9).setKr(0.2).setShininess(30)));

        Camera.Builder camera = Camera.getBuilder()
                .setLocation(new Point(-75, 60, -90))
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setTarget(new Point(0, 30, 0))
                .setVpDistance(150)
                .setVpSize(300, 300)

                .setMultiThreading(16)
                .setSoftShadows(true)
                .setRayTracer(new SimpleRayTracer(scene))
                //.duplicateScene(new Vector(-50, 0, 50))
                .setBVH(true)
                .setImageWriter(new ImageWriter("snowGlobeBuildTime", 1440, 1440));

        for (int i = 0; i < SNOW_GLOBE_FRAMES; ++i)
            camera.build().renderImage().writeToImage();
    }
}


package finalTest;

import geometries.Intersectable;
import geometries.Sphere;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.Camera;
import renderer.ImageWriter;
import renderer.SimpleRayTracer;
import renderer.super_sampling.Blackboard;
import scene.JsonSceneParser;
import scene.Scene;

import java.util.List;
import java.util.NoSuchElementException;


public class FinalTest {
    int SNOW_AMOUNT = 10000;

    JsonSceneParser jsp = new JsonSceneParser("src/unittests/renderer/json/snowGlobe.json");
    Scene scene = jsp.scene;


    /**
     * test the turnaround rendering of a complex scene
     */
    @Test
    public void simpleSnowGlobe() {
        //snow cover
        Vector down = new Vector(0, -1, 0);
        Material snow = new Material().setKd(1d).setKs(0.1);
        List<Point> cloud = Blackboard.getPointsOnCircle(
                down, new Point(0, 100, 0), 50, 0);
        for (Point p : cloud) {
            try {
                Ray r = new Ray(p, down);
                Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
                scene.geometries.add(new Sphere(0.5, s.point).setMaterial(snow));
            } catch (NoSuchElementException e) {
                continue;
            }
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
                .setBVH(false)
                .setSoftShadows(false)
                .setRayTracer(new SimpleRayTracer(scene));

        camera.setImageWriter(new ImageWriter("snowGlobe_simple", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }

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
            try {
                Ray r = new Ray(p, down);
                Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
                scene.geometries.add(new Sphere(0.5, s.point).setMaterial(snow));
            } catch (NoSuchElementException e) {
                continue;
            }
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
                .setVpSize(300, 300);


        camera.setBVH(true)
                .setSoftShadows(true)
                .setRayTracer(new SimpleRayTracer(scene));
        camera.setImageWriter(new ImageWriter("snowGlobe_both", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * test the turnaround rendering of a complex scene with BVH
     */
    @Test
    public void BVHSnowGlobe() {

        //snow cover
        Vector down = new Vector(0, -1, 0);
        Material snow = new Material().setKd(1d).setKs(0.1);
        List<Point> cloud = Blackboard.getPointsOnCircle(
                down, new Point(0, 100, 0), 50, SNOW_AMOUNT);
        for (Point p : cloud) {
            try {
                Ray r = new Ray(p, down);
                Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
                scene.geometries.add(new Sphere(0.5, s.point).setMaterial(snow));
            } catch (NoSuchElementException e) {
                continue;
            }
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
                .setVpSize(300, 300);

        camera.setBVH(true)
                .setSoftShadows(false)
                .setRayTracer(new SimpleRayTracer(scene));
        camera.setImageWriter(new ImageWriter("snowGlobe_onlyBVH", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * test the turnaround rendering of a complex scene with soft shadows
     */
    @Test
    @Disabled("too long")
    public void softShadowsSnowGlobe() {

        //snow cover
        Vector down = new Vector(0, -1, 0);
        Material snow = new Material().setKd(1d).setKs(0.1);
        List<Point> cloud = Blackboard.getPointsOnCircle(
                down, new Point(0, 100, 0), 50, 0);
        for (Point p : cloud) {
            try {
                Ray r = new Ray(p, down);
                Intersectable.GeoPoint s = r.findClosestGeoPoint(scene.geometries.findGeoIntersections(r));
                scene.geometries.add(new Sphere(0.5, s.point).setMaterial(snow));
            } catch (NoSuchElementException e) {
                continue;
            }
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
                .setVpSize(300, 300);

        camera.setBVH(false)
                .setSoftShadows(true)
                .setRayTracer(new SimpleRayTracer(scene));
        camera.setImageWriter(new ImageWriter("snowGlobe_onlySoftShadow", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
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
}

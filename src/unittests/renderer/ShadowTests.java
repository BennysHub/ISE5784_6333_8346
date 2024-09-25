package renderer;

import geometries.Intersectable;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.super_sampling.Blackboard;
import scene.JsonSceneParser;
import scene.Scene;

import java.util.List;
import java.util.NoSuchElementException;

import static java.awt.Color.*;

/**
 * Testing basic shadows
 *
 * @author Dan
 */
public class ShadowTests {
    /**
     * Scene of the tests
     */
    private final Scene scene = new Scene("Test scene");
    /**
     * Camera builder of the tests
     */
    private final Camera.Builder camera = Camera.getBuilder()
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setLocation(new Point(0, 0, 1000)).setVpDistance(1000)
            .setVpSize(162, 288)
            .setSoftShadows(false)
            .setRayTracer(new SimpleRayTracer(scene));

    /**
     * The sphere in the tests
     */
    private final Intersectable sphere = new Sphere(60d, new Point(0, 0, -200))
            .setEmission(new Color(BLUE))
            .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30));
    /**
     * The material of the triangles in the tests
     */
    private final Material trMaterial = new Material().setKd(0.5).setKs(0.5).setShininess(30);

    /**
     * Helper function for the tests in this module
     *
     * @param pictName     the name of the picture generated by a test
     * @param triangle     the triangle in the test
     * @param spotLocation the spotlight location in the test
     */
    private void sphereTriangleHelper(String pictName, Triangle triangle, Point spotLocation) {
        scene.geometries.add(sphere, triangle.setEmission(new Color(BLUE)).setMaterial(trMaterial));
        scene.lights.add(
                new SpotLight(new Color(400, 240, 0), spotLocation, new Vector(1, 1, -3))
                        .setKl(1E-5).setKq(1.5E-7).setSize(2));
        camera.setImageWriter(new ImageWriter(pictName, 1920, 1080))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of a sphere and triangle with point light and shade
     */
    @Test
    public void sphereTriangleInitial() {
        sphereTriangleHelper("shadowSphereTriangleInitial",
                new Triangle(new Point(-70, -40, 0), new Point(-40, -70, 0), new Point(-68, -68, -4)),
                new Point(-100, -100, 200));
    }

    /**
     * Sphere-Triangle shading - move triangle upright
     */
    @Test
    public void sphereTriangleMove1() {
        sphereTriangleHelper("shadowSphereTriangleMove1",
                new Triangle(new Point(-60, -30, 0), new Point(-30, -60, 0), new Point(-58, -58, -4)),
                new Point(-100, -100, 200));

    }

    /**
     * Sphere-Triangle shading - move triangle upper-righter
     */
    @Test
    public void sphereTriangleMove2() {
        sphereTriangleHelper("shadowSphereTriangleMove2",
                new Triangle(new Point(-50, -20, 0), new Point(-20, -50, 0), new Point(-48, -48, -4)),
                new Point(-100, -100, 200));

    }

    /**
     * Sphere-Triangle shading - move spot closer
     */
    @Test
    public void sphereTriangleSpot1() {
        sphereTriangleHelper("shadowSphereTriangleSpot1",
                new Triangle(new Point(-70, -40, 0), new Point(-40, -70, 0), new Point(-68, -68, -4)),
                new Point(-90, -90, 151));
    }

    /**
     * Sphere-Triangle shading - move spot even more closes
     */
    @Test
    public void sphereTriangleSpot2() {
        sphereTriangleHelper("shadowSphereTriangleSpot2",
                new Triangle(new Point(-70, -40, 0), new Point(-40, -70, 0), new Point(-68, -68, -4)),
                new Point(-80, -80, 101));

    }

    /**
     * Produce a picture of two triangles lighted by a spotlight with a Sphere
     * producing a shading
     */
    @Test
    public void trianglesSphere() {
        scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKs(0.8).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKs(0.8).setShininess(60)),
                new Sphere(30d, new Point(0, 0, -11))
                        .setEmission(new Color(BLUE))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30))
        );
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), 0.15));
        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(40, 40, 115), new Vector(-1, -1, -4))
                        .setKl(4E-4).setKq(2E-5).setSize(4));

        camera.setImageWriter(new ImageWriter("shadowTrianglesSphere", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * test the rendering from json file that contain a path to a stl file
     */
    @Test
    @Disabled("not good")
    public void stlShadow() {
        JsonSceneParser jsp = new JsonSceneParser("src/unittests/renderer/json/stlJson.json");
        Scene scene = jsp.scene;

        scene.lights.add(new PointLight(new Color(255, 255, 255).reduce(2), new Point(20, 15, 300)));
        scene.lights.add(new DirectionalLight(new Color(255, 255, 255).reduce(2), new Vector(-0.3, -0.3, 0)));
        scene.lights.add(new SpotLight(new Color(YELLOW), new Point(0, 100, 0), new Vector(0, -1, 0)).setNarrowBeam(3));

        Camera.Builder camera = Camera.getBuilder()
                .setDirection(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
                .setLocation(new Point(0, 220, 1000)).setVpDistance(1000)
                .setVpSize(200, 200)
                .setRayTracer(new SimpleRayTracer(scene));

        camera.setImageWriter(new ImageWriter("stlTurnaround/stlShadow", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }
}
package renderer;

import geometries.Geometry;
import geometries.Plane;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import lighting.LightSource;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.BLUE;
import static java.awt.Color.WHITE;

/**
 * The SoftShadowsTest class contains unit tests for rendering soft shadows.
 * It sets up various geometries and light sources to test the soft shadow rendering capabilities.
 */
public class SoftShadowsTest {

    /**
     * Shininess value for most of the geometries in the tests.
     */
    private static final int SHININESS = 301;

    /**
     * Diffusion attenuation factor for some geometries in the tests.
     */
    private static final Double3 KD3 = new Double3(0.2, 0.6, 0.4);

    /**
     * Specular attenuation factor for some geometries in the tests.
     */
    private static final Double3 KS3 = new Double3(0.2, 0.4, 0.3);

    private final Scene scene = new Scene("soft shadow");

    private final LightSource pointLight = new SpotLight(new Color(java.awt.Color.white), new Point(0, 0, 50), new Vector(0,0,-1))
            .setKl(0.02).setSize(2).setKc(0);

    private final Geometry plane = new Plane(Point.ZERO, new Vector(0, 0, 1))
            .setMaterial(new Material().setKd(KD3).setKs(KS3).setShininess(SHININESS));

    private final Geometry triangle = new Triangle(new Point(10, 10, 40), new Point(-10, -10, 40), new Point(-10, 10, 40))
            .setMaterial(new Material().setKd(KD3).setKs(KS3).setShininess(SHININESS).setKt(0.5));

    private final Camera.Builder camera1 = Camera.getBuilder()
            .setSoftShadows(true)
            .setRayTracer(new SimpleRayTracer(scene))
            .setLocation(new Point(0, 0, 1000))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(150, 150).setVpDistance(1000);

    /**
     * Tests the basic soft shadow rendering.
     */
    @Test
    public void softShadowBase() {
        scene.geometries.add(plane, triangle);
        scene.lights.add(pointLight);
        //scene.setAmbientLight(new AmbientLight(new Color(255, 191, 191), new Double3(0.15,0.15,0.3)));

        camera1.setImageWriter(new ImageWriter("softShadowBase", 1440, 1440))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Tests soft shadow rendering with triangles and a sphere.
     */
    @Test
    public void softShadowTrianglesSphere() {
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

        camera1.setImageWriter(new ImageWriter("softShadowTrianglesSphere", 600, 600))
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * test the case when the light source is under a plane but part of the light is above
     */
    @Test
    void underTheHorizon() {
        Material ground = new Material().setKd(KD3).setKs(KS3).setShininess(SHININESS);
        scene.geometries.add(
                new Triangle(new Point(15, 0, 15), new Point(-15, 0, -15), new Point(-15, 0, 15)).setMaterial(ground),
                new Triangle(new Point(15, 0, 15), new Point(-15, 0, -15), new Point(15, 0, -15)).setMaterial(ground),
                new Sphere(3, new Point(0, 1, 0)).setMaterial(new Material().setKd(new Double3(0.8, 0.263, 0.145)).setKs(0.1).setShininess(15))
        );

        scene.setAmbientLight(new AmbientLight(new Color(255, 191, 191), new Double3(0.15, 0.15, 0.3)));
        scene.lights.add(
                new PointLight(new Color(255, 255, 255), new Point(20, -1, 0)).setKl(0.02).setKc(0).setSize(3)
        );

        final Camera.Builder camera2 = Camera.getBuilder()
                .setSoftShadows(true)
                .setRayTracer(new SimpleRayTracer(scene))
                .setLocation(new Point(-1, 6, -1))
                .setTarget(Point.ZERO)
                .setVpSize(150, 150).setVpDistance(30);

        camera2.setImageWriter(new ImageWriter("underTheHorizonTest", 1440, 1440))
                .build()
                .renderImage()
                .writeToImage();
    }
}

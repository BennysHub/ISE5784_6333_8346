package renderer;

import geometries.Geometry;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import java.util.List;

import static java.awt.Color.*;

/**
 * Test rendering a basic image
 *
 * @author Dan
 */
public class LightsTests {
    /**
     * Shininess value for most of the geometries in the tests
     */
    private static final int SHININESS = 301;
    /**
     * Diffusion attenuation factor for some geometries in the tests
     */
    private static final double KD = 0.5;
    /**
     * Diffusion attenuation factor for some geometries in the tests
     */
    private static final Double3 KD3 = new Double3(0.2, 0.6, 0.4);
    /**
     * Specular attenuation factor for some geometries in the tests
     */
    private static final double KS = 0.5;
    /**
     * Specular attenuation factor for some geometries in the tests
     */
    private static final Double3 KS3 = new Double3(0.2, 0.4, 0.3);
    /**
     * Radius of the sphere
     */
    private static final double SPHERE_RADIUS = 50d;
    /**
     * First scene for some tests
     */
    private final Scene scene1 = new Scene("Test scene");
    /**
     * Second scene for some tests
     */
    private final Scene scene2 = new Scene("Test scene")
            .setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.15)));
    /**
     * First camera builder for some tests
     */
    private final Camera.Builder camera1 = Camera.getBuilder()
            .setScene(scene1)
            .setLocation(new Point(0, 0, 1000))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(150, 150).setVpDistance(1000);
    /**
     * Second camera builder for some tests
     */
    private final Camera.Builder camera2 = Camera.getBuilder()
            .setScene(scene2)
            .setLocation(new Point(0, 0, 1000))
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setVpSize(200, 200).setVpDistance(1000);
    /**
     * Material for some geometries in the tests
     */
    private final Material material = new Material().setKd(KD3).setKs(KS3).setShininess(SHININESS);
    /**
     * Light color for tests with triangles
     */
    private final Color trianglesLightColor = new Color(800, 500, 250);
    /**
     * Light color for tests with a sphere
     */
    private final Color sphereLightColor = new Color(800, 500, 0);
    /**
     * Color of the sphere
     */
    private final Color sphereColor = new Color(BLUE).reduce(2);
    /**
     * Center of the sphere
     */
    private final Point sphereCenter = new Point(0, 0, -50);
    /**
     * The triangles' vertices for the tests with triangles
     */
    private final Point[] vertices =
            {
                    // the shared left-bottom:
                    new Point(-110, -110, -150),
                    // the shared right-top:
                    new Point(95, 100, -150),
                    // the right-bottom
                    new Point(110, -110, -150),
                    // the left-top
                    new Point(-75, 78, 100)
            };
    /**
     * Position of the light in tests with a sphere
     */
    private final Point sphereLightPosition = new Point(-50, -50, 25);
    /**
     * Light direction (directional and spot) in tests with a sphere
     */
    private final Vector sphereLightDirection = new Vector(1, 1, -0.5);
    /**
     * Position of the light in tests with triangles
     */
    private final Point trianglesLightPosition = new Point(30, 10, -100);
    /**
     * Light direction (directional and spot) in tests with triangles
     */
    private final Vector trianglesLightDirection = new Vector(-2, -2, -2);

    /**
     * The sphere in appropriate tests
     */
    private final Geometry sphere = new Sphere(SPHERE_RADIUS, sphereCenter)
            .setMaterial(new Material().setKd(KD).setKs(KS).setShininess(SHININESS).setEmission(sphereColor));
    /**
     * The first triangle in appropriate tests
     */
    private final Geometry triangle1 = new Triangle(vertices[0], vertices[1], vertices[2])
            .setMaterial(material);
    /**
     * The first triangle in appropriate tests
     */
    private final Geometry triangle2 = new Triangle(vertices[0], vertices[1], vertices[3])
            .setMaterial(material);

    /**
     * Produce a picture of a sphere lighted by a directional light
     */
    @Test
    public void sphereDirectional() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new DirectionalLight(sphereLightColor, sphereLightDirection));

        camera1.setResolution(500, 500)
                .setImageName("lightSphereDirectional")
                .build()
                .renderImage()
                .writeToImage();
    }


    /**
     * Produce a picture of a sphere lighted by a point light
     */
    @Test
    public void spherePoint() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new PointLight(sphereLightColor, sphereLightPosition)
                .setKl(0.001).setKq(0.0002));

        camera1.setResolution(500, 500)
                .setImageName("lightSpherePoint")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    public void sphereSpot() {
        scene1.geometries.add(sphere);
        scene1.lights.add(new SpotLight(sphereLightColor, sphereLightPosition, sphereLightDirection)
                .setKl(0.001).setKq(0.0001));

        camera1.setResolution( 500, 500)
                .setImageName("lightSphereSpot")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a directional light
     */
    @Test
    public void trianglesDirectional() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new DirectionalLight(trianglesLightColor, trianglesLightDirection));

        camera2.setResolution( 500, 500)
                .setImageName("lightTrianglesDirectional")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a point light
     */
    @Test
    public void trianglesPoint() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new PointLight(trianglesLightColor, trianglesLightPosition)
                .setKl(0.001).setKq(0.0002));

        camera2.setResolution( 500, 500)
                .setImageName("lightTrianglesPoint")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a spotlight
     */
    @Test
    public void trianglesSpot() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection)
                .setKl(0.001).setKq(0.0001));

        camera2.setResolution(500, 500)
                .setImageName("lightTrianglesSpot")

                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by all lights
     */
    @Test
    public void trianglesAllLights() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.setBackground(new Color(DARK_GRAY))
                .setAmbientLight(new AmbientLight(new Color(PINK), 0.05))
                .setLights(List.of(
                        new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection).setKl(0.005).setKq(0.0005),
                        new DirectionalLight(trianglesLightColor.reduce(5), trianglesLightDirection),
                        new PointLight(trianglesLightColor, trianglesLightPosition.subtract(new Point(25, 25, 10))).setKl(0.0020).setKq(0.0008)));

        camera2.setResolution( 500, 500)
                .setImageName("allLightsTriangles")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of sphere affected by all lights
     */
    @Test
    public void sphereAllLights() {
        scene1.geometries.add(sphere);
        scene1.setBackground(new Color(DARK_GRAY))
                .setAmbientLight(new AmbientLight(new Color(RED), 0.05))
                .setLights(List.of(
                        new SpotLight(sphereLightColor, sphereLightPosition.subtract(new Point(-100, 0, 0)), new Vector(-1, 0.5, -2)).setKl(0.001).setKq(0.0001),
                        new DirectionalLight(sphereLightColor.reduce(2), sphereLightDirection.subtract(new Point(0.5, 0, 0))),
                        new PointLight(sphereLightColor.reduce(2), sphereLightPosition.subtract(new Point(-120, -75, 0)))
                ));

        camera1.setResolution(500, 500)
                .setImageName("allLightsSphere")

                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of a sphere lighted by a narrow spotlight
     */
    @Test
    public void sphereSpotSharp() {
        scene1.geometries.add(sphere);
        scene1.lights
                .add(new SpotLight(sphereLightColor, sphereLightPosition, new Vector(1, 1, -0.5)).setNarrowBeam(10)
                        .setKl(0.001).setKq(0.00004));

        camera1.setResolution( 500, 500)
                .setImageName("lightSphereSpotSharp")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a narrow spotlight
     */
    @Test
    public void trianglesSpotSharp() {
        scene2.geometries.add(triangle1, triangle2);
        scene2.lights.add(new SpotLight(trianglesLightColor, trianglesLightPosition, trianglesLightDirection).setNarrowBeam(10)
                .setKl(0.001).setKq(0.00004));


        camera2.setResolution( 500, 500)
                .setImageName("lightTrianglesSpotSharp")
                .build()
                .renderImage()
                .writeToImage();
    }

}

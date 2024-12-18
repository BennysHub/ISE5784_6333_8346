/**
 *
 */
package renderer;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Tests for reflection and transparency functionality, test for partial
 * shadows
 * (with transparency)
 *
 * @author dzilb
 */
public class ReflectionRefractionTests {
    /**
     * Scene for the tests
     */
    private final Scene scene = new Scene("Test scene");
    /**
     * Camera builder for the tests with triangles
     */
    private final Camera.Builder cameraBuilder = Camera.builder()
            .setOrientation(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setScene(scene);

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    public void twoSpheres() {
        scene.geometries.add(
                new Sphere(50d, new Point(0, 0, -50))
                        .setMaterial(new Material().setKd(0.4).setKs(0.3).setShininess(100).setKt(0.3).setEmission(new Color(BLUE))),
                new Sphere(25d, new Point(0, 0, -50))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(100).setEmission(new Color(RED))));
        scene.lights.add(
                new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2))
                        .setKl(0.0004).setKq(0.0000006));

        cameraBuilder.setPosition(new Point(0, 0, 1000)).setViewPlaneDistance(1000)
                .setViewPlaneSize(150, 150)
                .setResolution(500, 500)
                .setImageName("refractionTwoSpheres")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of a sphere lighted by a spotlight
     */
    @Test
    public void twoSpheresOnMirrors() {
        scene.geometries.add(
                new Sphere(400d, new Point(-950, -900, -1000))
                        .setMaterial(new Material().setKd(0.25).setKs(0.25).setShininess(20)
                                .setKt(new Double3(0.5, 0, 0)).setEmission(new Color(0, 50, 100))),
                new Sphere(200d, new Point(-950, -900, -1000))
                        .setMaterial(new Material().setKd(0.25).setKs(0.25).setShininess(20).setEmission(new Color(100, 50, 20))),
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500),
                        new Point(670, 670, 3000))
                        .setMaterial(new Material().setKr(1d).setEmission(new Color(20, 20, 20))),
                new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500),
                        new Point(-1500, -1500, -2000))
                        .setMaterial(new Material().setKr(new Double3(0.5, 0, 0.4)).setEmission(new Color(20, 20, 20))));
        scene.setAmbientLight(new AmbientLight(new Color(255, 255, 255), 0.1));
        scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4))
                .setKl(0.00001).setKq(0.000005));

        cameraBuilder.setPosition(new Point(0, 0, 10000)).setViewPlaneDistance(10000)
                .setViewPlaneSize(2500, 2500)
                .setResolution(500, 500)
                .setImageName("reflectionTwoSpheresMirrored")
                .build()
                .renderImage()
                .writeToImage();
    }

    /**
     * Produce a picture of two triangles lighted by a spotlight with a
     * partially
     * transparent Sphere producing partial shadow
     */
    @Test
    public void trianglesTransparentSphere() {
        scene.geometries.add(
                new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135),
                        new Point(75, 75, -150))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),
                new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
                        .setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(60)),
                new Sphere(30d, new Point(60, 50, -50))
                        .setMaterial(new Material().setKd(0.2).setKs(0.2).setShininess(30).setKt(0.6).setEmission(new Color(BLUE))));
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), 0.15));
        scene.lights.add(
                new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1))
                        .setKl(4E-5).setKq(2E-7));

        cameraBuilder.setPosition(new Point(0, 0, 1000)).setViewPlaneDistance(1000)
                .setViewPlaneSize(200, 200)
                .setResolution(600, 600)
                .setImageName("refractionShadow")
                .build()
                .renderImage()
                .writeToImage();
    }
}

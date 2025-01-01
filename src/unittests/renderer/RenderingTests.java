package renderer;

import geometries.Ellipsoid;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;
import scene.SceneJsonParser;

import static java.awt.Color.*;

/**
 * Test rendering a basic image
 *
 * @author Dan
 */
public class RenderingTests {
    /**
     * Scene of the tests
     */
    private final Scene scene = new Scene("Test scene");
    /**
     * Camera builder of the tests
     */
    private final Camera.Builder camera = Camera.builder()
            .setScene(scene)
            .setPosition(Point.ZERO).setOrientation(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setViewPlaneDistance(100)
            .setViewPlaneSize(500, 500);


//    private final Camera.Builder camera = Camera.getBuilder()
//            .setScene(scene)
//            .setLocation(Point.ZERO)
//            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
//            .changeTarget(new Point(0, 0, -100), new Point(100, 40, 1))
//            .setVpDistance(100)
//            .setVpSize(500, 500);


    /**
     * Produce a scene with basic 3D model and render it into a png image with a
     * grid
     */
    @Test
    public void renderTwoColorTest() {

        scene.geometries.add(new Sphere(50d, new Point(0, 0, -100)),
                new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)),
                new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100)),
                new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100)));
        scene.setAmbientLight(new AmbientLight(new Color(255, 191, 191), Double3.ONE))
                .setBackground(new Color(75, 127, 90));
        camera.setResolution(1000, 1000)
                .setImageName("base render test")
                .build()
                .renderImage()
                .printGrid(100, new Color(YELLOW))
                .writeToImage();
    }

    /**
     * Produce a scene with basic 3D model - including individual lights of the
     * bodies and render it into a png image with a grid
     */
    @Test
    public void renderMultiColorTest() {
        scene.geometries.add( // center
                new Sphere(50, new Point(0, 0, -100)),
                // up left
                new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
                        .setMaterial(new Material().setEmission(new Color(GREEN))),
                // down left
                new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                        .setMaterial(new Material().setEmission(new Color(RED))),
                // down right
                new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                        .setMaterial(new Material().setEmission(new Color(BLUE))));
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.2, 0.2, 0.2)));

        camera
                .setResolution(1000, 1000);


        double R = 150;  // Radius of the circle
        double h = -0;  // x-coordinate of the center
        double k = -100;  // y-coordinate of the center

        for (double t = 0; t <= 1; t += 0.1) {
            double x = h + R * Math.cos(2 * Math.PI * t);
            double y = k + R * Math.sin(2 * Math.PI * t);
            System.out.println("Point on circle: (" + x + ", " + y + ")" + " distance: " + Math.sqrt((x - h) * (x - h) + (y - k) * (y - k)));

            camera


                    .keepInFocus(new Point(0, 0, -100), new Point(x, 0, y))
                    .setImageName("color render test" + (int) (t * 100))
                    .build()
                    .renderImage()
                    .printGrid(100, new Color(WHITE))
                    .writeToImage();
        }

    }

    /**
     * Test for JSON-based scene - for bonus
     */
    @Test
    public void basicRenderJson() {
        final Scene scene1 = new SceneJsonParser("src/unittests/renderer/json/twoColorJson.json", "testScene");

        final Camera.Builder camera = Camera.builder()
                .setScene(scene1)
                .setPosition(Point.ZERO).setOrientation(new Vector(0, 0, -1), new Vector(0, 1, 0)) //changed
                .setViewPlaneDistance(100)
                .setViewPlaneSize(500, 500);

        camera.setResolution(1000, 1000)
                .setImageName("json render test")
                .build()
                .renderImage()
                .printGrid(100, new Color(YELLOW))
                .writeToImage();
    }
}


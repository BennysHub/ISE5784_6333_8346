package renderer;

import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Vector;
import scene.JsonSceneParser;
import scene.Scene;

import static java.awt.Color.*;

/**
 * Test rendering a basic image
 *
 * @author Dan
 */
public class RenderTests {
    /**
     * Scene of the tests
     */
    private final Scene scene = new Scene("Test scene");
    /**
     * Camera builder of the tests
     */
    private final Camera.Builder camera = Camera.getBuilder()
            .setRayTracer(new SimpleRayTracer(scene))
            .setLocation(Point.ZERO).setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)) //changed
            .setVpDistance(100)
            .setVpSize(500, 500);

    /**
     * Produce a scene with basic 3D model and render it into a png image with a
     * grid
     */
    @Test
    public void renderTwoColorTest() {

        scene.geometries.add(new Sphere(50d, new Point(0, 0, -100)),
                new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100)), // up
                // left
                new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100),
                        new Point(-100, -100, -100)), // down
                // left
                new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))); // down
        scene.setAmbientLight(new AmbientLight(new Color(255, 191, 191), Double3.ONE))
                .setBackground(new Color(75, 127, 90));

        // right
        //        camera
        camera.setImageWriter(new ImageWriter("base render test", 1000, 1000))
                .build()
                .renderImage()
                .printGrid(100, new Color(YELLOW))
                .writeToImage();
    }

    // For stage 6 - please disregard in stage 5

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
                        .setEmission(new Color(GREEN)),
                // down left
                new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                        .setEmission(new Color(RED)),
                // down right
                new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                        .setEmission(new Color(BLUE)));
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.2, 0.2, 0.2)));

        camera
                .setImageWriter(new ImageWriter("color render test", 1000, 1000))
                .build()
                .renderImage()
                .printGrid(100, new Color(WHITE))
                .writeToImage();
    }

    /**
     * test the function in the bonus
     * {@link renderer.Camera.Builder#setTarget(Point)}.
     * {@link renderer.Camera.Builder#rotateVectors(double)}.
     */
    @Test
    public void renderMultipleAnglesTest() {
        int testNum = 3;
        Camera.Builder camera = Camera.getBuilder()
                .setRayTracer(new SimpleRayTracer(scene))
                .setLocation(Point.ZERO).setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)) //changed
                .setVpDistance(100)
                .setVpSize(500, 500);

        scene.geometries.add( // center
                new Sphere(50, new Point(0, 0, -100)),
                // up left
                new Triangle(new Point(-100, 0, -100), new Point(0, 100, -100), new Point(-100, 100, -100))
                        .setEmission(new Color(GREEN)),
                // down left
                new Triangle(new Point(-100, 0, -100), new Point(0, -100, -100), new Point(-100, -100, -100))
                        .setEmission(new Color(RED)),
                // down right
                new Triangle(new Point(100, 0, -100), new Point(0, -100, -100), new Point(100, -100, -100))
                        .setEmission(new Color(BLUE)));
        scene.setAmbientLight(new AmbientLight(new Color(WHITE), new Double3(0.2, 0.2, 0.2)));

        for (int i = 0; i < testNum; ++i) {
            camera.setLocation(new Point((i * 10) - 50, 0, 0))
                    .setTarget(new Point(0, 0, -100))
                    .rotateVectors(10)
                    .setImageWriter(new ImageWriter("render Multiple Angles Test:" + i, 1000, 1000))
                    .build()
                    .renderImage()
                    .printGrid(100, new Color(YELLOW))
                    .writeToImage();
        }


    }

    /**
     * Test for XML based scene - for bonus
     */
    @Test
    public void basicRenderXml() {
        JsonSceneParser jsp = new JsonSceneParser("src/unittests/renderer/twoColorJson.json");
        final Scene scene1 = jsp.scene;
        //final Scene scene1 = Scene.loadFromJson("src/unittests/renderer/twoColorJson.json");

        final Camera.Builder camera = Camera.getBuilder()
                .setRayTracer(new SimpleRayTracer(scene1))
                .setLocation(Point.ZERO).setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)) //changed
                .setVpDistance(100)
                .setVpSize(500, 500);

        camera.setImageWriter(new ImageWriter("xml render test", 1000, 1000))
                .build()
                .renderImage()
                .printGrid(100, new Color(YELLOW))
                .writeToImage();
    }
}


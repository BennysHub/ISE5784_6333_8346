package renderer.super_sampling;

import geometries.Sphere;
import geometries.Triangle;
import lighting.SpotLight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.QualityLevel;
import scene.Scene;

import static java.awt.Color.BLUE;

public class AntiAliasingTest {

    Scene scene = new Scene("");

    private final Camera.Builder cameraBase = Camera.builder()
            .setOrientation(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setPosition(new Point(0, 0, 1000)).setViewPlaneDistance(1000)
            .setViewPlaneSize(200, 200)
            .setScene(scene)
            .setResolution(500, 500);

    @BeforeEach
    public void setUp() {
        scene.geometries.add(
                new Sphere(60d, new Point(0, 0, -200)).setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30).setEmission(new Color(BLUE))),
                new Triangle(new Point(-70, -40, 0), new Point(-40, -70, 0), new Point(-68, -68, -4)).setMaterial(new Material().setKd(0.5).setKs(0.5).setShininess(30).setEmission(new Color(BLUE))));

        scene.lights.add(
                new SpotLight(new Color(400, 240, 0), new Point(-100, -100, 200), new Vector(1, 1, -3)).setKl(1E-5).setKq(1.5E-7));
    }


    @Test
    public void antiAliasingOff() {
        cameraBase
                .setImageName("AntiAliasing-Off")
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    public void antiAliasingLow() {
        cameraBase
                .setImageName("AntiAliasing-Low")
                .enableAntiAliasing(true)
                .setAntiAliasingQuality(QualityLevel.LOW)
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    public void antiAliasingMedium() {
        cameraBase
                .setImageName("AntiAliasing-Medium")
                .enableAntiAliasing(true)
                .setAntiAliasingQuality(QualityLevel.MEDIUM)
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    public void antiAliasingHigh() {
        cameraBase
                .setImageName("AntiAliasing-High")
                .enableParallelStreams(true)
                .enableAntiAliasing(true)
                .setAntiAliasingQuality(QualityLevel.HIGH)
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    public void antiAliasingUltra() {
        cameraBase
                .setImageName("AntiAliasing-Ultra")
                .enableAntiAliasing(true)
                .enableParallelStreams(true)
                .setAntiAliasingQuality(QualityLevel.ULTRA)
                .build()
                .renderImage()
                .writeToImage();
    }


}

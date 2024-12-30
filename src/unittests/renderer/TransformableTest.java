package renderer;

import geometries.BVHNode;
import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import scene.Scene;
import scene.SceneJsonParser;

public class TransformableTest {
    Scene scene = new SceneJsonParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene");
    Point center = new Point(0, 40, 0);
    Camera.Builder camera = Camera.builder()
            .setViewPlaneSize(320, 320)
            .setResolution(1440, 1440)
            .setViewPlaneCenter(new Point(-20, 45, -20))
            .setOrientation(center, Vector.UNIT_Y)
            .enableParallelStreams(true)
            .setScene(scene)
            .enableBVH(true)
            .setBVHBuildMethod(BVHNode.BVHBuildMethod.SAH)
            .enableSoftShadows(false)
            .setSoftShadowsQuality(QualityLevel.HIGH);

    @Test
    void testScaling() {
        scene.geometries.scale(new Vector(2, 1, 2));

        camera
                .setImageName("snowGlobeScale")
                .build()
                .renderImage()
                .writeToImage();
    }


    @Test
    void testTranslating() {
        scene.geometries.translate(new Vector(50, -8, 50));

        camera
                .setImageName("snowGlobeTranslate")
                .build()
                .renderImage()
                .writeToImage();
    }

    @Test
    void testRotation() {

        int frames = 1;

        scene.geometries.scale(2);

        for (int i = 0; i < frames; i++) {
            camera.setImageName("snowGlobeRotation" + i)
                    .build()
                    .rotate(Vector.UNIT_Y, 2 * Math.PI / frames)
                    .translateY(5)
                    .renderImage()
                    .writeToImage();
        }


    }


}

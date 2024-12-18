package renderer.super_sampling;

import geometries.Sphere;
import geometries.Triangle;
import lighting.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import renderer.Camera;
import scene.Scene;

class DOFRenderingTest {


    private final Scene scene = new Scene("Test scene");


    private final Camera.Builder camera = Camera.builder()
            .setOrientation(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setPosition(new Point(0, 0, 700))
            .setViewPlaneDistance(650)
            .setViewPlaneSize(50, 300)
            .setScene(scene)
            .enableParallelStreams(true)
            .enableDepthOfField(true)
            .setApertureSize(2)
            .setFocalLength(50)
            .setResolution(1800, 300);


    @Test
    public void depthOFFieldSpheres() {

        Sphere base = new Sphere(20, new Point(0, 0, 0));

        scene.geometries.add(

                base.translateX(-105)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(-35).translateZ(-100)
                        .setMaterial(new Material().setKd(new Double3(0, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(35).translateZ(-200)
                        .setMaterial(new Material().setKd(new Double3(0, 0, 0.5)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(105).translateZ(-300)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80))

        );

        scene.lights.add(
                new DirectionalLight(new Color(500, 500, 500), new Vector(0, 0, -1)));


        for (int i = 0; i < 4; i++) {
            camera
                    .setFocalLength(50 + i * 100)
                    .setImageName("DOFSpheres" + i)
                    .build()
                    .renderImage()
                    .writeToImage();
        }


    }


    @Test
    public void depthOFFieldTriangles() {
        Triangle base = new Triangle(new Point(0, 20, 0), new Point(18, -18, 0), new Point(-18, -18, 0));

        scene.geometries.add(
                base.translateX(-105)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(-35).translateZ(-100)
                        .setMaterial(new Material().setKd(new Double3(0, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(35).translateZ(-200)
                        .setMaterial(new Material().setKd(new Double3(0, 0, 0.5)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.translateX(105).translateZ(-300)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80))
        );

        scene.lights.add(
                new DirectionalLight(new Color(300, 300, 300), new Vector(0, 0, -1)));


        for (int i = 0; i < 4; i++) {
            camera
                    .setFocalLength(50 + i * 100)
                    .setImageName("DOFTriangle" + i)
                    .build()
                    .renderImage()
                    .writeToImage();
        }
    }

    @Test
    public void somthingIntheRain() {

        Point center = new Point(3, 39, -432);
        var a = Blackboard.generateFibonacciSphere(center, 3, 45);
        for (Point p: a)
            System.out.println( p + "  distance: " + p.distance(center));

    }


}
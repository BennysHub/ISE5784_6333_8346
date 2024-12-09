package renderer;

import geometries.Sphere;
import geometries.Triangle;
import lighting.DirectionalLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

class DOFRenderingTest {


    private final Scene scene = new Scene("Test scene");


    private final Camera.Builder camera = Camera.getBuilder()
            .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
            .setLocation(new Point(0, 0, 700))
            .setVpDistance(650)
            .setVpSize(50, 300)
            .setScene(scene)
            .setParallelStreams(true)
            .setDepthOfField(true)
            .setApertureSize(2)
            .setFocalLength(50)
            .setResolution(1800, 300);


    @Test
    public void depthOFFieldSpheres() {

        Sphere base = new Sphere(20, new Point(0, 0, 0));

        scene.geometries.add(

                base.moveX(-105)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(-35).moveZ(-100)
                        .setMaterial(new Material().setKd(new Double3(0, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(35).moveZ(-200)
                        .setMaterial(new Material().setKd(new Double3(0, 0, 0.5)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(105).moveZ(-300)
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
                base.moveX(-105)
                        .setMaterial(new Material().setKd(new Double3(0.5, 0, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(-35).moveZ(-100)
                        .setMaterial(new Material().setKd(new Double3(0, 0.5, 0)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(35).moveZ(-200)
                        .setMaterial(new Material().setKd(new Double3(0, 0, 0.5)).setKs(new Double3(0.3, 0.3, 0.3)).setShininess(80)),
                base.moveX(105).moveZ(-300)
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
    public void  somthingIntheRain(){

        Vector to = new Vector(1, 0, 0);
        Vector up = new Vector(0,1,0);
        Vector right = to.crossProduct(up);


        Vector newTo = new Vector(0, 1, 0);

        //Vector newTo = target.subtract(this.location).normalize();
        //Vector axis = to.isParallel(newTo) ? originalNormal.perpendicular() : to.crossProduct(newTo);
        Vector axis =  to.crossProduct(newTo);

        double angle = Math.acos(to.dotProduct(newTo) / (to.length() * newTo.length()));
        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);
        Vector newUp = rotationMatrix.multiply(up);
        Vector newRight  = newTo.crossProduct(newUp);


        System.out.println("to :" + to + "  up :" + up +  "  right :" + right + "\nto :" + newTo + "  up :" + newUp +  "  right :" + newRight);
    }




}
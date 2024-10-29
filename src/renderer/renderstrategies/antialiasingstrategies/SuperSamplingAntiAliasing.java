package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.Render;
import renderer.ViewPlane;

//SSAA, we can add MSAA, FXAA, TAA, SMAA
public class SuperSamplingAntiAliasing extends Render {


    private static final int SSAA_SAMPLE_COUNT = 3;

    public SuperSamplingAntiAliasing(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }


    public Color calcalatePixelColor(int x, int y) {
        //return superSamplingAntiAliasing(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, 1);
        //return superSamplingAntiAliasingCorners(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, 1);

        Point[] pixelSamples = new Point[4];
        Vector rightScale = viewPlane.right.scale(viewPlane.pixelWidth / 2);
        Vector upScale = viewPlane.up.scale(viewPlane.pixelHeight / 2);
        Point center = viewPlane.getPixelCenter(x, y);
        pixelSamples[0] = center.add(rightScale).add(upScale);
        pixelSamples[1] = center.add(rightScale).add(upScale.scale(-1));
        pixelSamples[2] = center.add(rightScale.scale(-1)).add(upScale);
        pixelSamples[3] = center.add(rightScale.scale(-1)).add(upScale.scale(-1));

        Ray[] rays = new Ray[4];
        Color[] colors = new Color[4];
        for (int i = 0; i < 4; i++) {
            rays[i] = new Ray(camaraLocation, pixelSamples[i]);
            colors[i] = rayTracer.traceRay(rays[i]);
        }
        return newFunction(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, colors[2], colors[0], colors[3], colors[1], 1);
    }

    private Color superSamplingAntiAliasing(Point center, double pixelWidth, double pixelHeight, int depth) {
        Point[] pixelSamples = new Point[4];
        Vector rightScale = viewPlane.right.scale(pixelWidth / 3);
        Vector upScale = viewPlane.up.scale(pixelHeight / 3);

        pixelSamples[0] = center.add(rightScale).add(upScale);
        pixelSamples[1] = center.add(rightScale).add(upScale.scale(-1));
        pixelSamples[2] = center.add(rightScale.scale(-1)).add(upScale);
        pixelSamples[3] = center.add(rightScale.scale(-1)).add(upScale.scale(-1));

        Ray[] rays = new Ray[4];
        Color[] colors = new Color[4];
        for (int i = 0; i < 4; i++) {
            rays[i] = new Ray(camaraLocation, pixelSamples[i]);
            colors[i] = rayTracer.traceRay(rays[i]);
        }

        if (depth < SSAA_SAMPLE_COUNT ) {//&& Color.variance(colors) > 0.005
            Color accumulatedColor = Color.BLACK;
            for (int i = 0; i < 4; i++) {
                accumulatedColor = accumulatedColor.add(superSamplingAntiAliasing(pixelSamples[i], pixelWidth / 2, pixelHeight / 2, depth + 1));
            }
            return accumulatedColor.reduce(4);
        }
        return Color.average(colors);
    }

    private Color superSamplingAntiAliasingCorners(Point center, double pixelWidth, double pixelHeight, int depth) {
        Point[] pixelSamples = new Point[4];
        Vector rightScale = viewPlane.right.scale(pixelWidth / 2);
        Vector upScale = viewPlane.up.scale(pixelHeight / 2);

        pixelSamples[0] = center.add(rightScale).add(upScale);
        pixelSamples[1] = center.add(rightScale).add(upScale.scale(-1));
        pixelSamples[2] = center.add(rightScale.scale(-1)).add(upScale);
        pixelSamples[3] = center.add(rightScale.scale(-1)).add(upScale.scale(-1));

        Ray[] rays = new Ray[4];
        Color[] colors = new Color[4];
        for (int i = 0; i < 4; i++) {
            rays[i] = new Ray(camaraLocation, pixelSamples[i]);
            colors[i] = rayTracer.traceRay(rays[i]);
        }

        if (depth < SSAA_SAMPLE_COUNT) {//&& Color.variance(colors) > 0.001
            Color accumulatedColor = Color.BLACK;
            for (int i = 0; i < 4; i++) {
                accumulatedColor = accumulatedColor.add(superSamplingAntiAliasingCorners(pixelSamples[i], pixelWidth / 2, pixelHeight / 2, depth + 1));
            }
            return accumulatedColor.reduce(4);
        }

        return Color.average(colors);
    }


    private Color newFunction(Point center, double pixelWidth, double pixelHeight, Color topLeft, Color topRight, Color bottomLeft, Color bottomRight, int depth) {


        if (depth < SSAA_SAMPLE_COUNT) {// && Color.variance(topLeft, topRight, bottomLeft, bottomRight) > 0.001
            Point[] pixelSamples = new Point[5];
            Vector rightScale = viewPlane.right.scale(pixelWidth / 2);
            Vector upScale = viewPlane.up.scale(pixelHeight / 2);

            pixelSamples[0] = center.add(upScale);
            pixelSamples[1] = center.add(rightScale);
            pixelSamples[2] = center.add(upScale.scale(-1));
            pixelSamples[3] = center.add(rightScale.scale(-1));
            pixelSamples[4] = center;

            Ray[] rays = new Ray[5];
            Color[] colors = new Color[5];
            for (int i = 0; i < 5; i++) {
                rays[i] = new Ray(camaraLocation, pixelSamples[i]);
                colors[i] = rayTracer.traceRay(rays[i]);
            }
            rightScale = viewPlane.right.scale(pixelWidth / 4);
            upScale = viewPlane.up.scale(pixelHeight / 4);

            Color accumulatedColor = newFunction(center.add(rightScale.scale(-1)).add(upScale), pixelWidth / 2, pixelHeight / 2, topLeft, colors[0], colors[3], colors[4], depth + 1);
            accumulatedColor = accumulatedColor.add(newFunction(center.add(rightScale).add(upScale), pixelWidth / 2, pixelHeight / 2, colors[0], topRight, colors[4], colors[1], depth + 1));
            accumulatedColor = accumulatedColor.add(newFunction(center.add(rightScale.scale(-1)).add(upScale.scale(-1)), pixelWidth / 2, pixelHeight / 2, colors[3], colors[4], bottomLeft, colors[2], depth + 1));
            accumulatedColor = accumulatedColor.add(newFunction(center.add(rightScale).add(upScale.scale(-1)), pixelWidth / 2, pixelHeight / 2, colors[4], colors[1], colors[2], bottomRight, depth + 1));
            return accumulatedColor.reduce(4);
        }
        return Color.average(topLeft, topRight, bottomLeft, bottomRight);
    }


    @Override
    public void renderImage() {

    }
}

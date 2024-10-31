package renderer.anti_aliasing_rendering;

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


    private static final int SSAA_SAMPLE_COUNT = 3;//2^3 = max of 64

    public SuperSamplingAntiAliasing(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }

    @Override
    protected void castRay(int x, int y) {
        Color pixelColor = superSamplingAntiAliasing(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, 0);
        imageWriter.writePixel(x, y, pixelColor);
    }

    private Color superSamplingAntiAliasing(Point center, double pixelWidth, double pixelHeight, int depth) {
        Point[] pixelSamples = new Point[4];
        Vector rightScale = viewPlane.right.scale(pixelWidth / 4);
        Vector upScale = viewPlane.up.scale(pixelHeight / 4);

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

        if (depth < SSAA_SAMPLE_COUNT && Color.variance(colors) > 0.005) {
            Color accumulatedColor = Color.BLACK;
            for (int i = 0; i < 4; i++) {
                accumulatedColor = accumulatedColor.add(superSamplingAntiAliasing(pixelSamples[i], pixelWidth / 2, pixelHeight / 2, depth + 1));
            }
            return accumulatedColor.reduce(4);
        }
        return Color.average(colors);
    }
}

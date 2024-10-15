package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.*;

public class SuperSamplingAntiAliasing extends PixelColoringStrategy {


    private static final int SSAA_SAMPLE_COUNT = 3;//4^x witch is 4^3 = 64

    public SuperSamplingAntiAliasing(ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(viewPlane, rayTracer, camaraLocation);
    }

    @Override
    public Color calcalatePixelColor(int x, int y) {
        return superSamplingAntiAliasing(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, SSAA_SAMPLE_COUNT);
    }

    //TODO: since we first sample from the center of 4 subPixels and not the absolute corners of the pixel sometime the variance will be 0 since the change in color is after the subpixel center
    //to deal with it we can start from the absolute 4 corners and use another recursive approach so the sample be spread on all pixel area
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

        if (depth < 3 && calculateVariance(colors) > 0.01) {
            Color accumulatedColor = Color.BLACK;
            for (int i = 0; i < 4; i++) {
                accumulatedColor = accumulatedColor.add(superSamplingAntiAliasing(pixelSamples[i], pixelWidth / 4, pixelHeight / 4, depth + 1));
            }
            return accumulatedColor.reduce(4);
        }

        return colors[0].add(colors[1], colors[2], colors[3]).reduce(4);
    }

    public double calculateVariance(Color... colors) {
        int n = colors.length;
        double meanR = 0, meanG = 0, meanB = 0;
        for (Color color : colors) {
            meanR += color.getR();
            meanG += color.getG();
            meanB += color.getB();
        }
        meanR /= n;
        meanG /= n;
        meanB /= n;

        double varianceR = 0, varianceG = 0, varianceB = 0;
        for (Color color : colors) {
            varianceR += Math.pow(color.getR() - meanR, 2);
            varianceG += Math.pow(color.getG() - meanG, 2);
            varianceB += Math.pow(color.getB() - meanB, 2);
        }
        varianceR /= n;
        varianceG /= n;
        varianceB /= n;

        return (varianceR + varianceG + varianceB) / 3; // Average variance of r, g, b
    }


}

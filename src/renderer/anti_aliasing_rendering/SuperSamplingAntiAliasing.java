package renderer.anti_aliasing_rendering;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public class SuperSamplingAntiAliasing extends SSAA4X {


    public SuperSamplingAntiAliasing(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }

    Color[] pixelColors = new Color[viewPlane.nX * viewPlane.nY];


    @Override
    protected void calculatePixelColor(int x, int y) {
        superSampledColors[x][y] = adaptiveSuperSampling(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth / 2, viewPlane.pixelHeight / 2, 0);
        //superSampledColors[x][y] = adaptiveSuperSampling(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth / 2, viewPlane.pixelHeight / 2);
    }


    private Color adaptiveSuperSampling(Point center, double pixelWidth, double pixelHeight, int depth) {
        // Get the corners of the current pixel
        Point[] pixelCorners = getPixelCorners(center, pixelWidth, pixelHeight);

        // Base case: if max depth reached, calculate direct colors
        if (depth >= 2) {
            Color[] pixelSamples = new Color[4];
            for (int i = 0; i < 4; i++) {
                pixelSamples[i] = calculatePointlColor(pixelCorners[i]);
            }
            return Color.average(pixelSamples);
        }

        // If not uniform, subdivide further
        Color accumulatedColor = Color.BLACK;
        for (Point subPixelCenter : pixelCorners) {
            accumulatedColor = accumulatedColor.add(adaptiveSuperSampling(subPixelCenter, pixelWidth / 2, pixelHeight / 2, depth + 1));
        }

        return accumulatedColor.reduce(4);
    }

//    private Color adaptiveSuperSampling(Point center, double pixelWidth, double pixelHeight, int depth) {
//        if (depth >= MAX_DEPTH) {
//            return calculatePointColor(center);
//        }
//
//        Color accumulatedColor = Color.BLACK;
//        Point[] pixelCorners = getPixelCorners(center, pixelWidth, pixelHeight);
//
//        for (Point subPixelCorner : pixelCorners) {
//            accumulatedColor = accumulatedColor.add(
//                    adaptiveSuperSampling(subPixelCorner, pixelWidth / 2, pixelHeight / 2, depth + 1)
//            );
//        }
//
//        return accumulatedColor;
//    }



    private Color adaptiveSuperSampling(Point center, double pixelWidth, double pixelHeight) {
        // Get the corners of the current pixel

        Color accumulatedColor = Color.BLACK;
        Point[] pixelCorners = getPixelCorners(center, pixelWidth, pixelHeight);

        for (Point subPixelCenter: pixelCorners){
            Point[] subPixelCorners = getPixelCorners(subPixelCenter, pixelWidth/2, pixelHeight/2);
            for (Point subPixelCorner: subPixelCorners){
                Point[] subPixelCorners2 = getPixelCorners(subPixelCorner, pixelWidth/2, pixelHeight/2);
                for (Point subPixelCorner2: subPixelCorners2)
                    accumulatedColor = accumulatedColor.add(calculatePointlColor(subPixelCorner2));
            }

        }
        return accumulatedColor.reduce(64);
    }


    private Point[] getPixelCorners(Point pixelCenter, double pixelWidth, double pixelHeight) {
        Vector rightOffset = viewPlane.right.scale(pixelWidth / 2);
        Vector upOffset = viewPlane.up.scale(pixelHeight / 2);
        return new Point[]{
                pixelCenter.add(rightOffset).add(upOffset),
                pixelCenter.add(rightOffset).add(upOffset.scale(-1)),
                pixelCenter.add(rightOffset.scale(-1)).add(upOffset),
                pixelCenter.add(rightOffset.scale(-1)).add(upOffset.scale(-1))
        };
    }


    private Color calculatePointlColor(Point point) {
        return rayTracer.traceRay(new Ray(cameraPosition, point));
    }

}

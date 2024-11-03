package renderer.anti_aliasing_rendering;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public class AntiAliasingUltra extends SSAA4X {

    private static final int SSAA_SAMPLE_COUNT = 3;

    ColoredPoint[][] pixelColors = new ColoredPoint[viewPlaneHelper.nX][viewPlaneHelper.nY];

    public AntiAliasingUltra(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }

    @Override
    public void parallelStreamsRender() {
        //base anti aliasing
        parallelStreamsRender(viewPlaneHelper.nX, viewPlaneHelper.nY);
        addUltra();

    }


    @Override
    protected void calcColor(int x, int y) {
        Point pixelCenter = viewPlaneHelper.getPixelCenter(x, y);
        Ray ray = new Ray(camaraLocation, viewPlaneHelper.getPixelCenter(x, y));
        pixelColors[x][y] = new ColoredPoint(pixelCenter, rayTracer.traceRay(ray));
    }


    private void addUltra() {
        for (int x = 0; x < imageWriter.getNx(); x++) {
            for (int y = 0; y < imageWriter.getNy(); y++) {
                imageWriter.writePixel(x, y, addUltra(x, y));
            }
        }
    }

    private Color addUltra(int x, int y) {
//        ColoredPoint leftUp = pixelColors[x][y];
//        ColoredPoint rightUp = pixelColors[x][y + 1];
//        ColoredPoint leftDown = pixelColors[x + 1][y];
//        ColoredPoint rightDown = pixelColors[x + 1][y + 1];
////        if (Color.variance(leftUp.color, rightUp.color, leftDown.color, rightDown.color) > 0.001) {
////            Point pixelCenter = viewPlane.getPixelCenter(x, y);
////            Color pixelCenterColor = rayTracer.traceRay(new Ray(camaraLocation, pixelCenter));
////            ColoredPoint centerColoredPoint = new ColoredPoint(pixelCenter, pixelCenterColor);
////            Color a = aa(leftUp, centerColoredPoint, 0);
////            Color b = aa(rightUp, centerColoredPoint, 0);
////            Color c = aa(leftDown, centerColoredPoint, 0);
////            Color d = aa(rightDown, centerColoredPoint, 0);
////
////            return Color.average(a, b, c, d);
////        }
//
//
//        //return Color.average(leftUp.color, rightUp.color, leftDown.color, rightDown.color);

        return superSamplingAntiAliasingCorners(viewPlane.getPixelCenter(x, y), viewPlane.pixelWidth, viewPlane.pixelHeight, 0);
    }

    private Color xSample(ColoredPoint coloredPoint1, ColoredPoint coloredPoint2, int depth) {
        if (depth < SSAA_SAMPLE_COUNT ) {//&& Color.variance(coloredPoint1.color, coloredPoint2.color) > 0.001
            Point between = betweenPoint(coloredPoint1.point, coloredPoint1.point);
            Color betweenColor = rayTracer.traceRay(new Ray(camaraLocation, between));
            ColoredPoint betweenColoredPoint = new ColoredPoint(between, betweenColor);
            Color point1ToBetween = xSample(coloredPoint1, betweenColoredPoint, depth + 1);
            Color betweenToPoint2 = xSample(betweenColoredPoint, coloredPoint2, depth + 1);
            return Color.average(point1ToBetween, betweenToPoint2);
        }

        return Color.average(coloredPoint1.color, coloredPoint2.color);
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

        if (depth < SSAA_SAMPLE_COUNT ) {//&& Color.variance(colors) > 0.001
            Color accumulatedColor = Color.BLACK;
            for (int i = 0; i < 4; i++) {
                accumulatedColor = accumulatedColor.add(superSamplingAntiAliasingCorners(pixelSamples[i], pixelWidth / 2, pixelHeight / 2, depth + 1));
            }
            return accumulatedColor.reduce(4);
        }

        return Color.average(colors);
    }

    private Point betweenPoint(Point a, Point b) {
        return new Point((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2, (a.getZ() + b.getZ()) / 2);
    }


    public record ColoredPoint(Point point, Color color) {

        @Override
        public String toString() {
            return "ColoredPoint{" +
                    "point=" + point +
                    ", color=" + color +
                    '}';
        }
    }

}

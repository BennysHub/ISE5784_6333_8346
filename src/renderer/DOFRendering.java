package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import renderer.super_sampling.Blackboard;

public class DOFRendering extends Render  {


    final double apertureSize;
    final ViewPlane focalPlane;

    public DOFRendering(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation, double apertureSize, double focalLength) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
        this.apertureSize = apertureSize;
        double distanceFromCamara = camaraLocation.distance(viewPlane.center);
        double scaleRatio = (distanceFromCamara + focalLength) / distanceFromCamara;
        focalPlane = new ViewPlane(viewPlane.right, viewPlane.up, viewPlane.direction,viewPlane.vpHeight * scaleRatio, viewPlane.vpWidth * scaleRatio, viewPlane.center.add(viewPlane.direction.scale(focalLength)), viewPlane.nX, viewPlane.nY);
    }

    @Override
    protected void castRay(int x, int y) {
        Point pixelCenter = viewPlane.getPixelCenter(x, y);
        Point focalPoint = focalPlane.getPixelCenter(x, y);

        Point[] points = Blackboard.getDiskPoints(pixelCenter, apertureSize, viewPlane.direction, QualityLevel.MEDIUM);
        Color totalColor = Color.BLACK;
        for (Point point : points) {
            Ray ray = new Ray(point, focalPoint);
            totalColor = totalColor.add(rayTracer.traceRay(ray));
        }


        imageWriter.writePixel(x, y, totalColor.reduce(points.length));
    }





}

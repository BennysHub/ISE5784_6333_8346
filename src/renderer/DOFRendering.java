package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import renderer.super_sampling.Blackboard;

/**
 * Implements Depth of Field (DOF) rendering, simulating a camera lens's focus and aperture effects.
 * This class uses multiple rays per pixel to achieve realistic DOF effects.
 *
 * <p>The focal plane determines the sharp focus area, while the aperture size controls
 * the blurriness of out-of-focus areas.</p>
 *
 * @author Benny Avrahami
 */
public class DOFRendering extends Render {

    /** The focal plane where the image appears sharp. */
    private final ViewPlane focalPlane;

    /** Precomputed sample points within the aperture for simulating depth of field. */
    private final Point[] apertureSamplePoints;

    /**
     * Constructs a DOFRendering instance.
     *
     * @param imageWriter   The image writer for outputting the rendered image.
     * @param viewPlane     The primary view plane.
     * @param rayTracer     The ray tracer for handling intersections and shading.
     * @param cameraPosition The camera's position in the scene.
     * @param apertureSize  The size of the camera aperture, controlling DOF effects.
     * @param focalLength   The distance from the camera to the focal plane.
     * @param qualityLevel  The sampling quality level for the aperture.
     */
    public DOFRendering(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point cameraPosition, double apertureSize, double focalLength, QualityLevel qualityLevel) {
        super(imageWriter, viewPlane, rayTracer, cameraPosition);

        double distanceFromCamera = cameraPosition.distance(viewPlane.center);
        double scaleRatio = (distanceFromCamera + focalLength) / distanceFromCamera;

        // Calculate the focal plane's size and position
        focalPlane = new ViewPlane(
                viewPlane.right,
                viewPlane.up,
                viewPlane.direction,
                viewPlane.vpHeight * scaleRatio,
                viewPlane.vpWidth * scaleRatio,
                viewPlane.center.add(viewPlane.direction.scale(focalLength)),
                viewPlane.nX,
                viewPlane.nY
        );

        // Precompute aperture sample points for ray offsets
        apertureSamplePoints = Blackboard.getDiskPoints(viewPlane.center, apertureSize, viewPlane.direction, qualityLevel);
    }

    /**
     * Casts multiple rays per pixel, simulating the effects "depth of field"
     *
     * @param x The x-coordinate of the pixel in the image.
     * @param y The y-coordinate of the pixel in the image.
     */
    @Override
    protected void castRay(int x, int y) {
        Point pixelCenter = viewPlane.getPixelCenter(x, y);
        Point focalPoint = focalPlane.getPixelCenter(x, y);

        // Adjust aperture sample points relative to the pixel center
        Point[] shiftedAperturePoints = Blackboard.movePoints(apertureSamplePoints, pixelCenter.subtract(viewPlane.center));

        // Aggregate color contributions from rays
        Color accumulatedColor = Color.BLACK;
        for (Point aperturePoint : shiftedAperturePoints) {
            Ray ray = new Ray(aperturePoint, focalPoint);
            accumulatedColor = accumulatedColor.add(rayTracer.traceRay(ray));
        }

        // Average the accumulated color and write to the pixel
        imageWriter.writePixel(x, y, accumulatedColor.reduce(shiftedAperturePoints.length));
    }
}

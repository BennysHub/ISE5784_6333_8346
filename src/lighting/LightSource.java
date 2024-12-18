package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;

/**
 * Abstract base class representing a light source in a 3D scene.
 * <p>
 * This class extends the {@link Light} class, adding the ability to compute light direction,
 * intensity, and sampling for advanced lighting effects, such as soft shadows.
 * </p>
 *
 * <p>
 * Subclasses must implement specific behaviors for computing intensity, direction, and sampling
 * based on the type of light source (e.g., point light, spotlight, directional light).
 * </p>
 *
 * @author Benny Avrahami
 */
public abstract class LightSource extends Light {

    /**
     * Precomputed sample points for the light source, used for soft shadows or other sampling-based effects.
     */
    protected Point[] samplePoints;

    /**
     * The quality level used for sampling the light source.
     * Higher quality levels result in more samples, improving the smoothness of effects like soft shadows.
     */
    protected QualityLevel samplingQuality;

    /**
     * Constructs a {@code LightSource} object with the specified intensity.
     *
     * @param intensity The color representing the light intensity.
     */
    public LightSource(Color intensity) {
        super(intensity);
    }

    /**
     * Computes the light intensity at a given surface point, taking into account the light source's position.
     * <p>
     * This method is typically used in rendering calculations to determine how much light
     * a specific point on an object receives from this light source.
     * </p>
     *
     * @param surfacePoint     The point on the surface where the light intensity is evaluated.
     * @param lightSourcePoint A specific point of the light source contributing to the illumination.
     * @return The computed intensity of the light at the given surface point.
     */
    public abstract Color computeIntensity(Point surfacePoint, Point lightSourcePoint);

    /**
     * Computes the direction of light from the light source to a target point.
     * <p>
     * This method calculates the direction vector for lighting effects, such as shading
     * and shadow determination, based on the geometry of the scene.
     * </p>
     *
     * @param targetPoint      The point where the light direction is evaluated.
     * @param lightSourcePoint A specific point on the light source contributing to the illumination.
     * @return The direction vector from the light source to the target point.
     */
    public abstract Vector computeDirection(Point targetPoint, Point lightSourcePoint);

    /**
     * Computes sample points for the light source based on the specified quality level.
     * <p>
     * This method is used to precompute sample points for advanced lighting effects,
     * such as soft shadows. Higher quality levels result in more samples,
     * providing smoother transitions between shadowed and lit areas.
     * </p>
     *
     * @param qualityLevel The desired quality level for light sampling.
     */
    public abstract void computeSamples(QualityLevel qualityLevel);

    /**
     * Retrieves the precomputed sample points for the light source.
     * <p>
     * These sample points are used for advanced lighting effects, such as soft shadows,
     * and must be precomputed by calling {@link #computeSamples(QualityLevel)} before using this method.
     * </p>
     *
     * @return An array of precomputed sample points representing the light source.
     *         If sampling has not been computed, this method may return {@code null}.
     */
    public Point[] getSamplePoints() {
        return samplePoints;
    }

}

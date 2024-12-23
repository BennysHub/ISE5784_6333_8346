package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;
import renderer.super_sampling.Blackboard;

import static primitives.Util.isZero;

/**
 * Class representing a point light source in a 3D scene.
 * A point light emits light equally in all directions and is defined by its position and intensity.
 * Supports advanced lighting effects such as soft shadows through sampling.
 *
 * <p>This light source can attenuate light intensity over distance using constant, linear, and quadratic factors,
 * providing flexibility in creating realistic lighting effects.</p>
 *
 * <p>The class also supports soft shadows by allowing the light to be sampled as an area light.
 * The sampling quality can be overridden per light or set globally for the scene.</p>
 *
 * <p>Usage Example:</p>
 * <pre>
 *     PointLight light = new PointLight(new Color(255, 255, 255), new Point(0, 10, 0))
 *         .setKc(1)
 *         .setKl(0.1)
 *         .setKq(0.01)
 *         .setRadius(5)
 *         .setSamplingQuality(QualityLevel.HIGH);
 * </pre>
 *
 * @author Benny Avrahami
 */
public class PointLight extends LightSource {

    /**
     * The position of the light source.
     */
    protected final Point position;

    /**
     * The radius of the light source, used for generating soft shadows.
     */
    protected double radius = 0d;

    /**
     * Constant attenuation factor.
     */
    private double kC = 1;

    /**
     * Linear attenuation factor.
     */
    private double kL = 0;

    /**
     * Quadratic attenuation factor.
     */
    private double kQ = 0;

    /**
     * Constructs a point light with the specified intensity and position.
     * By default, the light behaves as a point source with no radius or attenuation.
     *
     * @param intensity The color representing the light intensity.
     * @param position  The position of the light source in the scene.
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
        samplePoints = new Point[]{position}; // Default to a single point source
    }

    /**
     * Sets the constant attenuation factor.
     * This factor reduces the light intensity regardless of distance.
     *
     * @param kc The constant attenuation factor.
     * @return The current {@code PointLight} instance for chaining.
     */
    public PointLight setKc(double kc) {
        this.kC = kc;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     * This factor reduces the light intensity proportionally to the distance.
     *
     * @param kl The linear attenuation factor.
     * @return The current {@code PointLight} instance for chaining.
     */
    public PointLight setKl(double kl) {
        this.kL = kl;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     * This factor reduces the light intensity proportionally to the square of the distance.
     *
     * @param kq The quadratic attenuation factor.
     * @return The current {@code PointLight} instance for chaining.
     */
    public PointLight setKq(double kq) {
        this.kQ = kq;
        return this;
    }

    /**
     * Sets the radius of the light source, enabling it to act as an area light.
     * The radius determines the size of the light and the area over which soft shadows are computed.
     *
     * @param radius The radius of the light source.
     * @return The current {@code PointLight} instance for chaining.
     */
    public PointLight setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    /**
     * Overrides the sampling quality for this specific light source.
     * If this method is not called, the global scene quality will be used when computing samples.
     *
     * @param quality The desired quality level for sampling this light source.
     * @return The current {@code PointLight} instance for chaining.
     */
    public PointLight setSamplingQuality(QualityLevel quality) {
        this.samplingQuality = quality;
        return this;
    }

    /**
     * Precomputes sample points for the light source based on the radius and sampling quality.
     * <p>
     * If the radius is zero, the light behaves as a point source and no additional samples are generated.
     * </p>
     *
     * @param samplingQuality The global quality level to use for sampling if no local quality is set.
     */
    @Override
    public void computeSamples(QualityLevel samplingQuality) {
        if (isZero(radius)) return; // Single point light behavior if radius is zero

        // Compute samples based on the effective quality level
        samplePoints = Blackboard.getSpherePoints(position, radius, this.samplingQuality != null ? this.samplingQuality : samplingQuality);
    }

    /**
     * Computes the light intensity at a given surface point, considering the attenuation factors.
     *
     * @param surfacePoint     The point on the surface where the intensity is evaluated.
     * @param lightSourcePoint A specific sample point on the light source.
     * @return The computed intensity of the light at the surface point.
     */
    @Override
    public Color computeIntensity(Point surfacePoint, Point lightSourcePoint) {
        double d = surfacePoint.distance(lightSourcePoint);
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    /**
     * Computes the direction vector of light from the light source to a target point.
     *
     * @param targetPoint      The point where the direction is computed.
     * @param lightSourcePoint A specific sample point on the light source.
     * @return The normalized direction vector from the light source to the target point.
     */
    @Override
    public Vector computeDirection(Point targetPoint, Point lightSourcePoint) {
        return targetPoint.subtract(lightSourcePoint).normalize();// TODO: vector zero case
    }
}

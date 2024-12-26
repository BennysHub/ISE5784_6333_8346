package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;
import utils.Blackboard;

import static utils.Util.alignZero;
import static utils.Util.isZero;

/**
 * Class representing a spotlight in a 3D scene.
 * A spotlight is a directional light source with intensity
 * that decreases based on distance and angle from its beam direction.
 *
 * <p>Spotlights provide focused lighting effects that are commonly used in scenes to highlight specific areas or objects.
 * The beam can be adjusted for size, intensity, and focus to simulate real-world spotlight behavior.</p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Direction-based intensity falloff.</li>
 *     <li>Adjustable beam focus for narrow or wide lighting effects.</li>
 *     <li>Supports radius-based soft shadow generation.</li>
 *     <li>Allows attenuation based on distance with configurable factors (constant, linear, quadratic).</li>
 * </ul>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * {@code
 * // Create a spotlight with a warm color, positioned at (0, 10, 0), pointing downwards
 * SpotLight spotlight = new SpotLight(new Color(255, 200, 150), new Point(0, 10, 0), new Vector(0, -1, 0))
 *         .setKc(1)                            // Constant attenuation
 *         .setKl(0.05)                         // Linear attenuation
 *         .setKq(0.002)                        // Quadratic attenuation
 *         .setRadius(3)                        // Light source radius for soft shadows
 *         .setBeamFocus(5)                     // Narrow beam focus
 *         .setSamplingQuality(QualityLevel.HIGH); // High-quality sampling
 *
 * // Add the spotlight to the scene
 * scene.addLight(spotlight);
 * }
 * </pre>
 *
 * <p>This configuration creates a focused spotlight pointing downwards, with soft shadows and a narrow beam.</p>
 *
 * @author Benny Avrahami
 */
public class SpotLight extends PointLight {

    /**
     * The direction of the spotlight's beam.
     */
    private final Vector direction;

    /**
     * The focus factor of the spotlight beam. Higher values result in a narrower, more focused beam.
     */
    private int beamFocus = 1;

    /**
     * Constructs a spotlight with the specified intensity, position, and direction.
     *
     * @param intensity The color representing the light intensity.
     * @param position  The position of the light source in the scene.
     * @param direction The direction of the spotlight's beam.
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize(); // Ensure the beam direction is normalized
    }

    /**
     * Sets the beam focus factor for the spotlight.
     * <p>
     * A higher beam focus narrows the beam's spread, creating a more concentrated light effect.
     * This is achieved by raising the dot product of the direction to a power equal to the beam focus factor.
     * </p>
     *
     * @param beamFocus The focus factor of the beam. Must be positive.
     * @return The current {@code SpotLight} instance for method chaining.
     */
    public SpotLight setBeamFocus(int beamFocus) {
        if (beamFocus <= 0) throw new IllegalArgumentException("Beam focus must be positive.");
        this.beamFocus = beamFocus;
        return this;
    }

    @Override
    public SpotLight setKc(double kc) {
        super.setKc(kc);
        return this;
    }

    @Override
    public SpotLight setKl(double kl) {
        super.setKl(kl);
        return this;
    }

    @Override
    public SpotLight setKq(double kq) {
        super.setKq(kq);
        return this;
    }

    @Override
    public SpotLight setRadius(double radius) {
        super.setRadius(radius);
        return this;
    }

    @Override
    public SpotLight setSamplingQuality(QualityLevel quality) {
        super.setSamplingQuality(quality);
        return this;
    }

    /**
     * Precomputes sample points for soft shadows based on the light source's radius and direction.
     * <p>
     * If the radius is non-zero,
     * a disk of sample points is generated on the plane perpendicular to the light's direction.
     * Otherwise, the spotlight behaves as a single point source.
     * </p>
     *
     * @param samplingQuality The quality level for sample generation. Affects the number of sample points.
     */
    @Override
    public void computeSamples(QualityLevel samplingQuality) {
        if (isZero(radius)) return; // No sampling if the radius is zero

        samplePoints = Blackboard.getDiskPoints(
                position,
                radius,
                direction,
                this.samplingQuality != null ? this.samplingQuality : samplingQuality
        );
    }

    /**
     * Computes the light intensity at a given surface point, considering the spotlight's attenuation and focus.
     * <p>
     * The intensity is reduced based on the angle between the spotlight's direction and the vector
     * pointing to the surface.
     * If the surface point is outside the spotlight's beam (negative dot product), the intensity is zero.
     * </p>
     *
     * @param surfacePoint     The point on the surface where the intensity is evaluated.
     * @param lightSourcePoint A specific sample point on the light source.
     * @return The computed intensity of the spotlight at the surface point.
     */
    @Override
    public Color computeIntensity(Point surfacePoint, Point lightSourcePoint) {
        // Calculate the direction strength (dot product of spotlight direction and light-to-surface vector)
        double directionStrength = alignZero(direction.dotProduct(surfacePoint.subtract(lightSourcePoint).normalize()));

        // If the surface is outside the beam's effective direction, return no light
        if (directionStrength <= 0) return Color.BLACK;

        // Scale the base intensity by the beam focus factor
        return super.computeIntensity(surfacePoint, lightSourcePoint).scale(Math.pow(directionStrength, beamFocus));
    }
}

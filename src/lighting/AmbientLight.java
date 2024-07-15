package lighting;

import primitives.Color;
import primitives.Double3;

/**
 * The AmbientLight class represents ambient light in a scene.
 * It is characterized by an intensity that affects the color of objects uniformly.
 *
 * @author Benny Avrahami and Tzvi Yisrael
 */
public class AmbientLight {

    /**
     * The intensity of the ambient light
     */
    private final Color intensity;

    /**
     * A static constant representing no ambient light
     */
    public static final AmbientLight NONE = new AmbientLight(Color.BLACK, 0d);

    /**
     * Constructs an AmbientLight object with specified intensity and attenuation factors.
     *
     * @param iA the color intensity of the ambient light
     * @param kA the attenuation factor as a Double3 object
     */
    public AmbientLight(Color iA, Double3 kA) {
        intensity = iA.scale(kA);
    }

    /**
     * Constructs an AmbientLight object with specified intensity and attenuation factors.
     *
     * @param iA the color intensity of the ambient light
     * @param kA the attenuation factor as a Double object
     */
    public AmbientLight(Color iA, Double kA) {
        intensity = iA.scale(kA);
    }

    /**
     * Returns the intensity of the ambient light.
     *
     * @return the color intensity of the ambient light
     */
    public Color getIntensity() {
        return intensity;
    }
}

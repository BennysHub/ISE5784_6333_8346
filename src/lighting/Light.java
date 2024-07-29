package lighting;

import primitives.Color;

/**
 * The Light class represents a light source in a scene.
 * It is characterized by the color of the light.
 *
 * <p>This class is abstract and serves as a base class for specific types of light sources.</p>
 *
 * @author Benny Avrahami and Tzvi Yisrael
 */
abstract class Light {
    /**
     * The intensity of the light.
     */
    protected final Color intensity;

    /**
     * Constructs a Light object with the specified intensity.
     *
     * @param intensity the color representing the light intensity
     */
    public Light(Color intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the intensity of the light.
     *
     * @return the color intensity of the light
     */
    public Color getIntensity() {
        return intensity;
    }
}

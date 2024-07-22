package lighting;

import primitives.Color;

/**
 * The Light class represents light in a scene.
 * It is characterized by the color of the light.
 *
 * @author Benny Avrahami and Tzvi Yisrael
 */
abstract class Light {
    /**
     * The intensity of the light
     */
    protected final Color intensity;

    /**
     * Constructs a Light object with specified intensity.
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

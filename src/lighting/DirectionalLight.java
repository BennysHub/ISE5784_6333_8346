package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Class representing a directional light source in a 3D scene.
 * A directional light has a fixed direction and intensity.
 */
public class DirectionalLight extends Light implements LightSource {
    private final Vector direction;

    /**
     * Constructs a directional light with the specified intensity and direction.
     *
     * @param intensity the color representing the light intensity
     * @param direction the direction of the light
     */
    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return intensity;
    }

    @Override
    public Vector getL(Point p) {
        return direction;
    }
}

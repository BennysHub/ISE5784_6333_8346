package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static java.lang.Math.max;

/**
 * Class representing a spotlight in a 3D scene.
 * A spotlight is a point light with a specific direction and intensity that decreases with distance and angle.
 */
public class SpotLight extends PointLight {
    private final Vector direction;

    /**
     * Constructs a spotlight with the specified intensity, position, and direction.
     *
     * @param intensity the color representing the light intensity
     * @param position  the position of the light source
     * @param direction the direction of the light
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity(p).scale(max(0, direction.dotProduct(getL(p))));
    }
}

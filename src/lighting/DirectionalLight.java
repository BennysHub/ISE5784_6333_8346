package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

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
    public Color getIntensity(Point p, Point lightPoint) {
        return intensity;
    }

    @Override
    public Point[] getLightSample(Point p, int samplesCount) {
        return new Point[]{Point.POSITIVE_INFINITY};
    }

    @Override
    public Vector getL(Point to, Point lightPoint) {
        return direction;
    }

}

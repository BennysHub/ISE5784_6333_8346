package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;

/**
 * Class representing a directional light source in a 3D scene.
 * A directional light has a fixed direction and intensity.
 */
public class DirectionalLight extends LightSource {

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
        samplePoints = new Point[]{Point.POSITIVE_INFINITY};
    }

    @Override
    public void computeSamples(QualityLevel sampleCount) {}

    @Override
    public Color computeIntensity(Point p, Point lightPoint) {
        return intensity;
    }

    @Override
    public Vector computeDirection(Point targetPoint, Point lightSourcePoint) {
        return direction;
    }

}

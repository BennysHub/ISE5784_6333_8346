package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.super_sampling.Blackboard;

import static primitives.Util.alignZero;

/**
 * Class representing a spotlight in a 3D scene.
 * A spotlight is a point light with a specific direction and intensity that decreases with distance and angle.
 */
public class SpotLight extends PointLight {

    private final Vector direction;

    private int narrowBeam = 1;

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

    public SpotLight(Color intensity, Point position, Vector direction, double size) {
        this(intensity, position, direction);
        this.radius = size;
        setLightSamples();
    }

    @Override
    protected void setLightSamples() {
        lightSamples = Blackboard.getDiskPoints(position, radius, direction);
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

    /**
     * Sets the beamFocus for the spotLight direction
     *
     * @param narrowBeam the strength of the beam will be more focus
     * @return the current PointLight instance for chaining
     */
    public PointLight setNarrowBeam(int narrowBeam) {
        this.narrowBeam = narrowBeam;
        return this;
    }

    @Override
    public Color getIntensity(Point p, Point lightPoint) {
        double directionStrength = alignZero(direction.dotProduct(p.subtract(lightPoint).normalize()));
        return super.getIntensity(p, lightPoint).scale(directionStrength <= 0 ? 0 : Math.pow(directionStrength, narrowBeam));
    }

    @Override
    public Point[] getLightSample(Point p, int samplesCount) {
        return samplesCount == 1 ? new Point[]{position} : lightSamples;
    }
}

package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.super_sampling.Blackboard;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Class representing a spotlight in a 3D scene.
 * A spotlight is a point light with a specific direction and intensity that decreases with distance and angle.
 */
public class SpotLight extends PointLight {

    private final Vector direction;

    private int beamFocus = 1;

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
     * @param beamFocus the strength of the beam will be more focus
     * @return the current PointLight instance for chaining
     */
    public PointLight setNarrowBeam(int beamFocus) {
        this.beamFocus = beamFocus;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double directionStrength = alignZero(direction.dotProduct(p.subtract(position).normalize()));
        return super.getIntensity(p).scale(directionStrength <= 0 ? 0 : Math.pow(directionStrength, beamFocus));
    }

    @Override
    public List<Ray> getRaysBeam(Point p, int numOfRays) {
        return Blackboard.constructRays(Blackboard.getPointsOnSphere(direction, position, size, numOfRays), p);
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position) - size;
    }//disk min distance???
}

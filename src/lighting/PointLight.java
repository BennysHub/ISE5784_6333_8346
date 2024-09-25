package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.super_sampling.Blackboard;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.compareSign;

/**
 * Class representing a point light source in a 3D scene.
 * A point light has a position and its intensity decreases with distance.
 */
public class PointLight extends Light implements LightSource {


    /**
     * The position of the light source.
     */
    protected final Point position;

    /**
     * The size of the light. use for soft shadows
     */
    protected double size = 0d;

    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;

    /**
     * Constructs a point light with the specified intensity and position.
     *
     * @param intensity the color representing the light intensity
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kc the constant attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKc(double kc) {
        this.kC = kc;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kl the linear attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKl(double kl) {
        this.kL = kl;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kq the quadratic attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKq(double kq) {
        this.kQ = kq;
        return this;
    }

    /**
     * Gets the size/radius of the light.
     *
     * @return the size of the light
     */
    public Double getSize() {
        return size;
    }

    /**
     * Sets the size/radius of the light.
     *
     * @param size the size/radius of the light
     * @return the current PointLight instance for chaining
     */
    public PointLight setSize(double size) {
        this.size = size;
        return this;
    }

    /**
     * Gets the position of the light source.
     *
     * @return the position of the light source
     */
    public Point getPosition() {
        return position;
    }

    @Override
    public Color getIntensity(Point p) {
        final double d = p.distance(position);
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    @Override
    public Vector getL(Point p, Vector n) {//original return p.subtract(position).normalize();
        Point point = position.add(n.scale(size));
        return  p.subtract(point).normalize();

    }

    @Override
    public List<Ray> getRaysBeam(Point p, Vector n, int numOfRays) {
        return Blackboard.constructRays(position, p, n, size, numOfRays);
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position) - size;
    }
}

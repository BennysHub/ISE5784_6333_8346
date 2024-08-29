package lighting;

import primitives.*;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;
import static primitives.Util.isZero;
import static primitives.Util.random;

/**
 * Class representing a point light source in a 3D scene.
 * A point light has a position and its intensity decreases with distance.
 */
public class PointLight extends Light implements LightSource {


    /**
     * The position of the light source.
     */
    protected final Point position;

    private double size = 0d;

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

    public Double getSize(){
        return size;
    }

    public Point getPosition() {
        return position;
    }

    @Override
    public Color getIntensity(Point p) {
        final double d = p.distance(position);
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position);
    }
}

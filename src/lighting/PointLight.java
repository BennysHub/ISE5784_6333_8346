package lighting;

import primitives.*;
import renderer.RenderSettings;
import renderer.super_sampling.Blackboard;

import java.util.ArrayList;
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
     * Size of the light. Use for soft shadows
     */
    protected double size = 0d;

    List<Point> lightSourceSample = null;

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

    @Override
    public Color getIntensity(Point p) {
        double d = p.distance(position) - size;
        if (alignZero(d) <= 0)
            d =0;
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    @Override
    public List<Ray> getRaysBeam(Point p, int numOfRays) {
//        if (lightSourceSample == null)
//            lightSourceSample = Blackboard.getSphereSampleWithZNormal(position, size);
//        return Blackboard.constructRays(Blackboard.rotatePointsOnSphere(lightSourceSample, new Vector(0,0,1), n, position), p);
        return Blackboard.constructRays(Blackboard.getPointsOnSphere(p.subtract(position).normalize(), position, size, numOfRays), p);


//        var x = Blackboard.applyJitter( Blackboard.warpToDisk(Blackboard.grid169, size), size/numOfRays );
//        var y = Blackboard.convertTo3D(x, position, n.perpendicular(), n.perpendicular().crossProduct(n));
//        y = Blackboard.addSphereDepth(y, position, size, n);
//        return Blackboard.constructRays(y, n);


    }

    @Override
    public List<Point> getLightSample(Point p, int samplesCount) {
        return Blackboard.getPointsOnSphere(p.subtract(position).normalize(), position, size, samplesCount);
    }

    @Override
    public Vector getL(Point to, Point lightPoint) {
        return to.subtract(lightPoint).normalize();
    }

}

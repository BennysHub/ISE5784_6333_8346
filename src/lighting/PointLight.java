package lighting;

import primitives.Color;
import primitives.Double2;
import primitives.Point;
import primitives.Vector;
import renderer.QualityLevel;
import renderer.super_sampling.Blackboard;

import static primitives.Util.isZero;

/**
 * Class representing a point light source in a 3D scene.
 * A point light has a position and its intensity decreases with distance.
 */
public class PointLight extends Light implements LightSource {


    /**
     * The position of the light source.
     */
    protected final Point position;

    protected QualityLevel lightSampleQuality = null;

    private Double2[] lightSamples = null;

    /**
     * Size of the light. Use for soft shadows
     */
    protected double radius = 0d;



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

    public PointLight(Color intensity, Point position, double radius) {
        this(intensity, position);
        this.radius = radius;
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
    public Double getRadius() {
        return radius;
    }

    public PointLight setLightSampleQuality(QualityLevel sampleQuality){
        lightSampleQuality = sampleQuality;
        return this;
    }

    @Override
    public void setLightSample(QualityLevel sampleQuality) {
        lightSamples = Blackboard.getCirclePoints(radius,
                lightSampleQuality != null ? lightSampleQuality : sampleQuality);

    }

    @Override
    public Color getIntensity(Point p, Point lightPoint) {
        //what if the point is inside the light sphere??
        double d = p.distance(lightPoint);
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    @Override
    public Point[] getLightSample(Point p) {
        if (lightSamples == null || isZero(radius))
            return new Point[]{position};

        Vector normal = p.subtract(position).normalize();
        Point[] diskPoints = Blackboard.convertTo3D( lightSamples, position, normal);
        return Blackboard.addSphereDepth(diskPoints, position, radius, normal);
    }

    @Override
    public Vector getL(Point to, Point lightPoint) {
        return to.subtract(lightPoint).normalize();
    }

}

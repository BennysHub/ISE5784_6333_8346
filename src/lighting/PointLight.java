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
        double d = p.distance(position) - size;
        if (alignZero(d) <= 0)
            d =0;
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }


    //since light can have size there can be a problem with "back lightning" where we try to avoid it by  compareSing(nl , nv) and if they have the same sing
    //we know we don't try to light the BACK of the object,
    //the problem is if light has size meaning there can be a potential ray where nl and nv are the same but if l is from the center the sing of nl and nv are
    //different and that course the color not to be calculated, a good example will be a light where center is under a plane but since it has size some of the
    //light body go over the plane but the plane front won't be lighted since nl and nv are different and plane front is consider as BACK hens not lighted
    //to work around this problem we need to send l from the "maximum height of the light body" in a direction of n where n is the normal vector of a point we calculate
    //in our example since part of the light is above the plane if the plane normal is up we send a ray from the light top and this course to sing flip because now
    //n and l are less than 90 degrees rather if we took from the center we'll get more than 90 degrees and will consider as back lighting (if we look at the plane top v.n is also less than 90)
    //if the plane normal is down.....
    @Override
    public Vector getL(Point p) {//original
        return p.subtract(position).normalize();
    }

    @Override
    public List<Ray> getRaysBeam(Point p, Vector n, int numOfRays) {
//        if (lightSourceSample == null)
//            lightSourceSample = Blackboard.getSphereSampleWithZNormal(position, size);
//        return Blackboard.constructRays(Blackboard.rotatePointsOnSphere(lightSourceSample, new Vector(0,0,1), n, position), p);
        return Blackboard.constructRays(Blackboard.getPointsOnSphere(n, position, size, numOfRays), p);


//        var x = Blackboard.applyJitter( Blackboard.warpToDisk(Blackboard.grid169, size), size/numOfRays );
//        var y = Blackboard.convertTo3D(x, position, n.perpendicular(), n.perpendicular().crossProduct(n));
//        y = Blackboard.addSphereDepth(y, position, size, n);
//        return Blackboard.constructRays(y, n);


    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position) - size;
    }//circle


    @Override
    public List<Point> findExtreme(Vector vector) {
        return (alignZero(size) == 0d) ? List.of(position) : List.of(position.add(vector.scale(size)), position.add(vector.scale(-size)));
    }
}

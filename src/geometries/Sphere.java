package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents a sphere in 3D space, defined by its center point and radius.
 * Extends {@link RadialGeometry} to inherit the radius property.
 *
 * <p>A sphere is mathematically defined as the set of all points at a constant distance (radius)
 * from a given center point in three-dimensional space.</p>
 *
 * @author Benny Avrahami
 */
public class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a sphere with the specified radius and center.
     *
     * @param radius The radius of the sphere.
     * @param center The center point of the sphere.
     */
    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point spherePoint) {
        return spherePoint.subtract(center).normalize();
    }

    @Override
    protected Sphere translateHelper(Vector translationVector) {
        return new Sphere(radius, center.add(translationVector));
    }

    @Override
    protected Geometry rotateHelper(Quaternion rotation) {
        return this;
    }

    @Override
    protected Geometry scaleHelper(Vector scale) {
        if (scale.getX() == scale.getY() && scale.getY() == scale.getZ()) {
            return new Sphere(radius * scale.getX(), center); // Uniform scaling
        }
        return new Ellipsoid(center, new Vector(radius * scale.getX(), radius * scale.getY(), radius * scale.getZ())); // Non-uniform scaling
    }

    @Override
    protected void calculateAABBHelper() {
        Point min = new Point(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        Point max = new Point(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        aabb = new AABB(min, max);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {

        // Calculate coefficients for the quadratic equation
        Vector oc = ray.getOrigin().subtract(center);
        double b = oc.dotProduct(ray.getDirection());//we don't multiply by 2 since we can ...
        double c = oc.dotProduct(oc) - radiusSquared;
        double discriminant = b * b - c;// we don't multiply c by 4a since a = dir^2 which is one, and 4 since we didn't multiply b by 2, so b^2 is 4*x.

        // Check if there are valid intersection points
        if (discriminant <= 0)
            return null; // No intersections

        // Compute intersection parameters t1 and t2
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t2 = -b + sqrtDiscriminant;

        // If t2 <= 0 so t1, it indicates that the ray is moving away from the sphere.
        // There are no intersection points in this case.
        if (t2 <= 0)
            return null;

        Point p2 = ray.getPoint(t2);
        double t1 = -b - sqrtDiscriminant;

        //if t1 >= max distance (so is t2) even if its valid intersection it still out of boundary
        if (t1 - maxDistance >= 0)
            return null;

        // If t1 > 0 so t2, It means the ray enters the sphere and exits from the other side (two intersection points)
        if (t2 - maxDistance < 0)
            return t1 > 0 ?
                    List.of(new GeoPoint(this, ray.getPoint(t1)), new GeoPoint(this, p2))
                    : List.of(new GeoPoint(this, p2));
        else
            return t1 > 0 ? List.of(new GeoPoint(this, ray.getPoint(t1))) : null;
    }

    @Override
    public String toString() {
        return String.format("Sphere{center=%s, radius=%f}", center, radius);
    }

    public Point getCenter() {
        return center;
    }
}

package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * Represents a sphere in 3D space, defined by its center point and radius.
 * Inherits the radius property from RadialGeometry.
 *
 * @author Benny Avrahami
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a Sphere with the specified radius and center.
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
        // The normal to the sphere at a given point is the vector from the center to the point.
        return spherePoint.subtract(center).normalize();
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        // If the ray starts at the sphere's center, return the point on the sphere's surface
        if (ray.getOrigin().equals(center))
            return List.of(new GeoPoint(this, ray.getPoint(radius)));

        // Calculate coefficients for the quadratic equation
        Vector oc = ray.getOrigin().subtract(center);
        double b = oc.dotProduct(ray.getDirection());//we don't multiply by 2 since we can ...
        double c = oc.dotProduct(oc) - radiusSquared;
        double discriminant = b * b - c;// we don't multiply c by 4a since a = dir^2 which is one, and 4 since we didn't multiply b by 2 so b^2 is 4*x.

        // Check if there are valid intersection points
        if (alignZero(discriminant) <= 0)
            return null; // No intersections

        // Compute intersection parameters t1 and t2
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t2 = alignZero(-b + sqrtDiscriminant);

        // If t2 <= 0 so t1, it indicates that the ray is moving away from the sphere.
        // There are no intersection points in this case.
        if (t2 <= 0)
            return null;

        Point p2 = ray.getPoint(t2);
        double t1 = alignZero(-b - sqrtDiscriminant);

        //if t1 >= max distance (so is t2) even if its valid intersection it still out of boundary
        if (alignZero(t1 - maxDistance) >= 0)
            return null;

        // If t1 > 0 so t2, It means the ray enters the sphere and exits from the other side (two intersection points)
        if (alignZero(t2 - maxDistance) < 0)
            return t1 > 0 ?
                    List.of(new GeoPoint(this, ray.getPoint(t1)), new GeoPoint(this, p2))
                    : List.of(new GeoPoint(this, p2));
        else
            return t1 > 0 ? List.of(new GeoPoint(this, ray.getPoint(t1))) : null;
    }
}

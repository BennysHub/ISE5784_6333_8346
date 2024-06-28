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
    public List<Point> findIntersections(Ray ray) {
        // If the ray starts at the sphere's center, return the point on the sphere's surface
        if (ray.getHead().equals(center))
            return List.of(ray.getPoint(radius));

        // Calculate coefficients for the quadratic equation
        Vector oc = ray.getHead().subtract(center);
        double b = oc.dotProduct(ray.getDirection());//we don't multiply by 2 since we can ...
        double c = oc.dotProduct(oc) - radius * radius;
        double discriminant = b * b - c;// we don't multiply c by 4a since a = dir^2 which is one, and 4 since we didn't multiply b by 2 so b^2 is 4*x.

        // Check if there are valid intersection points
        if (alignZero(discriminant) <= 0) {
            return null; // No intersections
        }

        // Compute intersection parameters t1 and t2
        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t2 = (-b + sqrtDiscriminant);

        // If t2 <= 0 so t1, it indicates that the ray is moving away from the sphere.
        // There are no intersection points in this case.
        if (alignZero(t2) <= 0)
            return null;

        Point p2 = ray.getPoint(t2);
        double t1 = (-b - sqrtDiscriminant);

        // If t1 > 0 so t2, It means the ray enters the sphere and exits from the other side (two intersection points)
        if (alignZero(t1) > 0) {
            Point p1 = ray.getPoint(t1);
            return List.of(p1, p2); // Two valid intersection points
        } else {//If t2 is positive and t2 is negative, it means the ray starts inside the sphere.
            return List.of(p2); // Only t2 is positive
        }
    }
}

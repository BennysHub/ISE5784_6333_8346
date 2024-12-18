package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a cylinder in 3D space, defined by its axis ray, radius, and height.
 * Inherits the radius property from {@link RadialGeometry} and extends {@link Tube} with a finite height.
 *
 * <p>A cylinder consists of a tube capped with two flat, circular bases.</p>
 *
 * @author Benny Avrahami
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     */
    private final double height;

    /**
     * Constructs a Cylinder with the specified radius, axis ray, and height.
     *
     * @param radius   The radius of the cylinder.
     * @param axisRay  The axis ray of the cylinder.
     * @param height   The height of the cylinder.
     */
    public Cylinder(double radius, Ray axisRay, double height) {
        super(radius, axisRay);
        this.height = height;
    }

    @Override
    public Vector getNormal(Point pointOnSurface) {
        // The origin of the cylinder's axis ray
        Point rayOrigin = axisRay.getOrigin();

        // The direction of the axis ray, which is normalized
        Vector rayDirection = axisRay.getDirection();

        // If the point is at the origin of the axis ray
        if (pointOnSurface.equals(rayOrigin))
            return rayDirection.scale(-1); // Normal vector points opposite to the ray direction

        // Vector from the ray's origin to the point on the surface
        Vector vectorToPoint = pointOnSurface.subtract(rayOrigin);

        // Projection of vectorToPoint onto the ray direction
        double distanceAlongRay = vectorToPoint.dotProduct(rayDirection);

        // If the projection is zero, the point is on the front flat base
        if (isZero(distanceAlongRay))
            return rayDirection.scale(-1); // Normal vector points opposite to the ray direction

        // The closest point on the axis ray to the given point
        Point closestPointOnAxis = rayOrigin.add(rayDirection.scale(distanceAlongRay));

        // If the projection equals the cylinder's height, the point is on the back flat base
        if (isZero(distanceAlongRay - height))
            return rayDirection; // Normal vector points in the same direction as the ray direction

        // For points on the curved surface, calculate the normal vector
        return pointOnSurface.subtract(closestPointOnAxis).normalize();
    }

    @Override
    protected void calculateAABBHelper() {

    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        // Implementation is not yet defined
        return null;
    }

}

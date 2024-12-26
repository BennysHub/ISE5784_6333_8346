package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tube in 3D space, defined by its axis ray and radius.
 * Inherits the radius property from RadialGeometry.
 *
 * <p>A tube is essentially a cylinder with an infinite length.</p>
 *
 * @author Benny Avrahami
 */
public class Tube extends RadialGeometry {
    /**
     * The axis ray of the tube, which is a line that runs through its center.
     */
    protected final Ray axisRay;

    /**
     * Constructs a Tube with the specified radius and axis ray.
     *
     * @param radius  The radius of the tube.
     * @param axisRay The axis ray of the tube.
     */
    public Tube(double radius, Ray axisRay) {
        super(radius);
        this.axisRay = axisRay;
    }

    @Override
    public Vector getNormal(Point pointOnSurface) {
        // The origin point of the tube's axis ray
        Point rayOrigin = axisRay.getOrigin();

        // The direction of the axis ray, which is normalized
        Vector rayDirection = axisRay.getDirection();

        // Vector from the ray origin to the point on the surface
        Vector vectorToSurface = pointOnSurface.subtract(rayOrigin);

        // Projection of vectorToSurface onto rayDirection
        double distanceAlongRay = vectorToSurface.dotProduct(rayDirection);

        // The closest point on the axis ray to the given surface point
        Point closestPointOnAxis = axisRay.getPoint(distanceAlongRay);

        // The normal vector at the surface point
        Vector surfaceNormal = pointOnSurface.subtract(closestPointOnAxis);

        return surfaceNormal.normalize();
    }

    @Override
    public Tube rotateHelper(Quaternion rotation) {
        Ray rotatedAxisRay = new Ray(
                rotation.rotate( axisRay.getOrigin()),
                rotation.rotate(axisRay.getDirection())
        );
        return new Tube(radius, rotatedAxisRay);
    }

    @Override
    public Tube scaleHelper(Vector scaleVector) {
        if (scaleVector.getX() != (scaleVector.getY()) || scaleVector.getX() != (scaleVector.getZ())) {
            throw new UnsupportedOperationException("Non-uniform scaling is not supported for Tube.");
        }
        double scaledRadius = radius * scaleVector.getX();
        return new Tube(scaledRadius, axisRay);
    }

    @Override
    public Tube translateHelper(Vector translationVector) {
        return new Tube(radius, new Ray(axisRay.getOrigin().add(translationVector), axisRay.getDirection()));
    }

    @Override
    protected void calculateAABBHelper() {
        aabb = new AABB();
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Vector v = axisRay.getDirection();
        Point p0 = ray.getOrigin();
        Vector dir = ray.getDirection();

        Vector deltaP = p0.subtract(axisRay.getOrigin());

        // Quadratic coefficients
        double a = dir.subtract(v.scale(dir.dotProduct(v))).lengthSquared();
        double b = 2 * dir.subtract(v.scale(dir.dotProduct(v))).dotProduct(deltaP.subtract(v.scale(deltaP.dotProduct(v))));
        double c = deltaP.subtract(v.scale(deltaP.dotProduct(v))).lengthSquared() - radius * radius;

        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return null; // No intersections
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        List<GeoPoint> intersections = new ArrayList<>();

        if (t1 > 0 && t1 <= maxDistance) {
            intersections.add(new GeoPoint(this, ray.getPoint(t1)));
        }
        if (t2 > 0 && t2 <= maxDistance) {
            intersections.add(new GeoPoint(this, ray.getPoint(t2)));
        }

        return intersections.isEmpty() ? null : intersections;
    }

    public Ray getAxisRay() {
        return axisRay;
    }
}

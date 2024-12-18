package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
    protected void calculateAABBHelper() {

    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        // Implementation is not yet defined
        return null;
    }


    @Override
    protected Geometry translateHelper(Vector translationVector) {
        return null;
    }

    @Override
    protected Geometry rotateHelper(Vector axis, double angleInRadians) {
        return null;
    }

    @Override
    protected Geometry scaleHelper(Vector scale) {
        return null;
    }

    @Override
    public Geometry scale(double scale) {
        return null;
    }

}

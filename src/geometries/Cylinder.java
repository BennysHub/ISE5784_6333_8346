package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static utils.Util.alignZero;
import static utils.Util.isZero;

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
        Point rayOrigin = axisRay.getOrigin();
        Vector rayDirection = axisRay.getDirection();
        Vector vectorToPoint = pointOnSurface.subtract(rayOrigin);

        double projection = vectorToPoint.dotProduct(rayDirection);

        if (isZero(projection)) {
            return rayDirection.scale(-1); // Point is on the bottom base
        }

        if (isZero(projection - height)) {
            return rayDirection; // Point is on the top base
        }

        //The Point is on the curved surface
        Point closestPointOnAxis = rayOrigin.add(rayDirection.scale(projection));
        return pointOnSurface.subtract(closestPointOnAxis).normalize();
    }

    @Override
    public Cylinder rotateHelper(Quaternion rotation) {
        Point rotatedOrigin = rotation.rotate(axisRay.getOrigin());
        Vector rotatedDirection = rotation.rotate(axisRay.getDirection());
        return new Cylinder(radius, new Ray(rotatedOrigin, rotatedDirection), height);
    }

    @Override
    public Cylinder scaleHelper(Vector scaleVector) {
        Vector scaledDirection = axisRay.getDirection().scale(scaleVector.getZ());
        double scaledHeight = height * scaleVector.getZ();
        double scaledRadius = radius * scaleVector.getX(); // Assume uniform scaling in X and Y for radius
        Point scaledOrigin = axisRay.getOrigin().scale(scaleVector);
        return new Cylinder(scaledRadius, new Ray(scaledOrigin, scaledDirection.normalize()), scaledHeight);
    }

    @Override
    public Cylinder translateHelper(Vector translationVector) {
        return new Cylinder(radius, new Ray(axisRay.getOrigin().add(translationVector), axisRay.getDirection()), height);
    }

    @Override
    protected void calculateAABBHelper() {
        Vector axisDirection = axisRay.getDirection().normalize();
        Point bottomBaseCenter = axisRay.getOrigin();
        Point topBaseCenter = bottomBaseCenter.add(axisDirection.scale(height));

        Point min = new Point(
                Math.min(bottomBaseCenter.getX(), topBaseCenter.getX()) - radius,
                Math.min(bottomBaseCenter.getY(), topBaseCenter.getY()) - radius,
                Math.min(bottomBaseCenter.getZ(), topBaseCenter.getZ()) - radius
        );

        Point max = new Point(
                Math.max(bottomBaseCenter.getX(), topBaseCenter.getX()) + radius,
                Math.max(bottomBaseCenter.getY(), topBaseCenter.getY()) + radius,
                Math.max(bottomBaseCenter.getZ(), topBaseCenter.getZ()) + radius
        );

        aabb = new AABB(min, max);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = new ArrayList<>();

        // Find intersections with the curved surface
        List<GeoPoint> curvedIntersections = super.findGeoIntersectionsHelper(ray, maxDistance);
        if (curvedIntersections != null) {
            for (GeoPoint gp : curvedIntersections) {
                double projection = axisRay.getDirection().dotProduct(gp.point().subtract(axisRay.getOrigin()));
                if (alignZero(projection) >= 0 && alignZero(projection - height) <= 0) {
                    intersections.add(gp);
                }
            }
        }

        // Find intersections with the bottom base
        Vector bottomNormal = axisRay.getDirection().scale(-1);
        Plane bottomBase = new Plane(axisRay.getOrigin(), bottomNormal);
        List<GeoPoint> bottomIntersections = bottomBase.findGeoIntersections(ray, maxDistance);
        if (bottomIntersections != null) {
            for (GeoPoint gp : bottomIntersections) {
                if (gp.point().distanceSquared(axisRay.getOrigin()) <= radius * radius) {
                    intersections.add(new GeoPoint(this, gp.point()));
                }
            }
        }

        // Find intersections with the top base
        Point topBaseCenter = axisRay.getOrigin().add(axisRay.getDirection().scale(height));
        Plane topBase = new Plane(topBaseCenter, axisRay.getDirection());
        List<GeoPoint> topIntersections = topBase.findGeoIntersections(ray, maxDistance);
        if (topIntersections != null) {
            for (GeoPoint gp : topIntersections) {
                if (gp.point().distanceSquared(topBaseCenter) <= radius * radius) {
                    intersections.add(new GeoPoint(this, gp.point()));
                }
            }
        }

        return intersections.isEmpty() ? null : intersections;
    }

    @Override
    public double signedDistance(Point point) {
        Vector v = point.subtract(axisRay.getOrigin());
        double projectedHeight = v.dotProduct(axisRay.getDirection());

        if (projectedHeight < 0) {
            // Below bottom cap
            return v.length() - radius;
        } else if (projectedHeight > height) {
            // Above top cap
            return point.subtract(axisRay.getPoint(height)).length() - radius;
        }

        // Distance to the curved surface
        Vector perpendicular = v.subtract(axisRay.getDirection().scale(projectedHeight));
        return perpendicular.length() - radius;
    }

}

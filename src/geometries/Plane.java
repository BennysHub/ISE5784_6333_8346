package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

/**
 * Represents a plane in 3D space, defined by a point on the plane and a normal vector.
 * The plane is assumed to be infinite in an extent.
 *
 * @author Benny Avrahami
 */
public class Plane extends Geometry {
    /**
     * A point on the plane.
     */
    private final Point planePoint;

    /**
     * The normalized vector perpendicular to the plane.
     */
    private final Vector planeNormalizedVector;

    /**
     * Constructs a Plane through three points in space.
     * The points must not be collinear and must not be the same.
     *
     * @param p1 The first point on the plane.
     * @param p2 The second point on the plane.
     * @param p3 The third point on the plane.
     * @throws IllegalArgumentException If the points are collinear or not distinct.
     */
    public Plane(Point p1, Point p2, Point p3) {
        // An exception will be thrown when creating vectors if points are similar or all on the same line.
        Vector vector1 = p2.subtract(p1);
        Vector vector2 = p3.subtract(p1);
        Vector vector3 = vector1.crossProduct(vector2);
        planePoint = p1;
        planeNormalizedVector = vector3.normalize();
    }

    /**
     * Constructs a Plane with a point on the plane and a normal vector.
     *
     * @param planePoint        A point on the plane.
     * @param planeNormal The normal vector of the plane, which will be normalized.
     */
    public Plane(Point planePoint, Vector planeNormal) {
        this.planePoint = planePoint;
        this.planeNormalizedVector = planeNormal.normalize();
    }

    /**
     * Gets the normalized normal vector of the plane.
     *
     * @return The normalized normal vector of the plane.
     */
    public Vector getNormal() {
        return planeNormalizedVector;
    }

    @Override
    protected Intersectable duplicateObjectHelper(Vector vector){
        return new Plane(planePoint.add(vector), planeNormalizedVector).setMaterial(this.getMaterial());
    }

    @Override
    public Vector getNormal(Point planePoint) {
        return getNormal(); // The normal is the same everywhere on an infinite plane.
    }

    @Override
    protected void calculateAABBHelper() {
        aabb = new AABB();
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Point p0 = ray.getOrigin();
        if (planePoint.equals(p0)) return null;

        Vector dir = ray.getDirection();

        // Check if the ray is parallel to the plane
        double nv = planeNormalizedVector.dotProduct(dir);
        if (isZero(nv)) return null; // The ray is parallel to the plane

        double t = planeNormalizedVector.dotProduct(planePoint.subtract(p0)) / nv;
        return alignZero(t) <= 0 || alignZero(t - maxDistance) >= 0 ? null // The intersection is behind the ray's start point
                : List.of(new GeoPoint(this, ray.getPoint(t)));
    }
}


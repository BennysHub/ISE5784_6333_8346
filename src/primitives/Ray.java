package primitives;



import geometries.Intersectable;
import geometries.Intersectable.GeoPoint;

import java.util.List;

/**
 * Represents a ray in 3D space, defined by a starting point (origin) and a direction vector.
 * The direction vector is normalized to ensure it is a unit vector.
 *
 * @author Benny Avrahami
 */
public class Ray {
    /**
     * A small constant value used to adjust the ray's starting point for precision purposes.
     */
    private static final double DELTA = 0.1;

    /**
     * The starting point of the ray.
     */
    final private Point origin;

    /**
     * The direction vector of the ray, normalized to be a unit vector.
     */
    final private Vector direction;

    /**
     * Constructs a new Ray with the specified starting point and direction.
     * The direction vector is normalized upon construction.
     *
     * @param point  The starting point of the ray.
     * @param vector The direction vector of the ray.
     */
    public Ray(Point point, Vector vector) {
        origin = point;
        direction = vector.normalize();
        if(direction.equals(Vector.ZERO))
            throw new IllegalArgumentException("Ray direction can't be defined by Vector Zero.");
    }

    /**
     * Constructs a Ray from two points. The ray's origin is the first point, and its direction
     * is derived from the vector connecting the two points.
     *
     * @param p1 The starting point of the ray.
     * @param p2 The target point used to calculate the direction.
     */
    public Ray(Point p1, Point p2) {
        this(p1, p2.subtract(p1)) ;
    }

    /**
     * Constructs a new Ray with the specified starting point, direction, and normal vector.
     * Adjusts the starting point slightly along the normal vector to avoid precision issues
     * in geometric calculations.
     *
     * @param origin    The starting point of the ray.
     * @param direction The direction vector of the ray. <b>Must be normalized</b>.
     * @param normal    The normal vector at the starting point.
     */
    public Ray(Point origin, Vector direction, Vector normal) {
        this(origin.add(normal.scale(direction.dotProduct(normal) > 0 ? DELTA : -DELTA)), direction);
    }

    /**
     * Gets the origin point of the ray.
     *
     * @return The origin point of the ray.
     */
    public Point getOrigin() {
        return origin;
    }

    /**
     * Gets the direction vector of the ray.
     *
     * @return The direction vector of the ray.
     */
    public Vector getDirection() {
        return direction;
    }

    /**
     * Calculates a point on the ray at a specified distance from the origin.
     *
     * @param t The distance from the origin along the direction vector.
     * @return A point on the ray at the specified distance.
     */
    public Point getPoint(double t) {
            return origin.add(direction.scale(t));// TODO: vector zero case
    }

    /**
     * Finds the closest geometric intersection point from a list of intersection points to the ray's origin.
     *
     * @param geoPointList A list of geometric intersection points.
     * @return The closest geometric intersection point to the ray's origin, or null if the list is empty or null.
     */
    public Intersectable.GeoPoint findClosestGeoPoint(List<Intersectable.GeoPoint> geoPointList) {
        if (geoPointList == null) return null;
        Intersectable.GeoPoint closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Intersectable.GeoPoint gP : geoPointList) {
            double distance = gP.point().distanceSquared(origin);
            if (distance < shortestDistance) {
                closest = gP;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    /**
     * Finds the closest point from a list of points to the ray's origin.
     *
     * @param points A list of points.
     * @return The closest point to the ray's origin, or null if the list is empty or null.
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestGeoPoint(points.stream().map(p -> new GeoPoint(null, p)).toList()).point();
    }

    /**
     * Provides a string representation of the Ray, showing the origin and direction.
     *
     * @return A string describing the ray.
     */
    @Override
    public String toString() {
        return String.format("Ray ➞ %s in direction %s", origin, direction);
    }

    /**
     * Compares this ray with another object for equality.
     * Two rays are considered equal if their origin points and direction vectors are the same.
     *
     * @param obj The object to compare with this ray.
     * @return true if the object is a Ray and has the same origin and direction, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other
                && origin.equals(other.origin)
                && direction.equals(other.direction);
    }
}

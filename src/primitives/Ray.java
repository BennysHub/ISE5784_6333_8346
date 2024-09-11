package primitives;

import geometries.Intersectable.GeoPoint;

import java.util.List;

/**
 * Represents a ray in 3D space, defined by a starting point (head) and a direction vector.
 * The direction vector is normalized to ensure it is a unit vector.
 *
 * @author Benny Avrahami
 */
public class Ray {
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
    }

    /**
     * Constructs a new Ray with the specified starting point, direction, and normal.
     * Adjusts the starting point to avoid precision issues in geometric calculations.
     *
     * @param origin      The starting point of the ray.
     * @param direction The direction vector of the ray. <b>Must be normalized</b>
     * @param normal    The normal vector at the starting point.
     */
    public Ray(Point origin, Vector direction, Vector normal) {
        this.direction = direction.normalize();
        Vector epsVector = normal.scale(direction.dotProduct(normal) > 0 ? DELTA : -DELTA);
        this.origin = origin.add(epsVector);
    }

    /**
     * Gets the head point of the ray.
     *
     * @return The head point of the ray.
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
     * Calculates a point on the ray at a specified distance from the head.
     *
     * @param t the distance from the head point along the direction vector
     * @return a point on the ray at the specified distance
     */
    public Point getPoint(double t) {
        try {
            return origin.add(direction.scale(t));
        } catch (IllegalArgumentException ignore) {
            return origin;
        }
    }

    /**
     * Finds the closest geometric point from a list of intersection points to the ray's head.
     *
     * @param geoPointList a list of geometric intersection points
     * @return the closest geometric intersection point to the ray's head, or null if the list is empty
     */
    public GeoPoint findClosestGeoPoint(List<GeoPoint> geoPointList) {
        if (geoPointList == null) return null;
        GeoPoint closest = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (GeoPoint gP : geoPointList) {
            double distance = gP.point.distanceSquared(origin);
            if (distance < shortestDistance) {
                closest = gP;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    /**
     * Finds the closest point from a list of points to the ray's head.
     *
     * @param points a list of points
     * @return the closest point to the ray's head, or null if the list is empty
     */
    public Point findClosestPoint(List<Point> points) {
        return points == null || points.isEmpty() ? null
                : findClosestGeoPoint(points.stream().map(p -> new GeoPoint(null, p)).toList()).point;
    }

    @Override
    public String toString() {
        return String.format("Ray ➞ %s in direction %s", origin, direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other
                && origin.equals(other.origin)
                && direction.equals(other.direction);
    }
}

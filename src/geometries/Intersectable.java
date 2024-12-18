package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * An abstract class representing geometric shapes that can be intersected by a ray.
 * <p>
 * This class provides methods to find intersection points with a given ray, both in terms of
 * geometric points and points associated with specific geometries.
 * It also supports bounding volume
 * hierarchy (BVH) optimization through an axis-aligned bounding box (AABB).
 * <p>
 * Subclasses must implement the {@link #findGeoIntersectionsHelper(Ray, double)} method to define the
 * specific logic for finding intersections.
 */
public abstract class Intersectable implements Transformable {
    /**
     * Axis-Aligned Bounding Box (AABB) for bounding volume hierarchy (BVH).
     */
    protected AABB aabb;

    /**
     * Builds the AABB for the intersectable geometry.
     * This method should be overridden by subclasses to calculate their specific AABB.
     */
    protected abstract void calculateAABBHelper();

    /**
     * Ensures the AABB is calculated if it has not been already.
     */
    public void calculateAABB() {
        if (aabb == null) calculateAABBHelper();
    }

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     *
     * @param ray         the ray to intersect with this geometric shape.
     * @param maxDistance the maximum distance within which to search for intersections.
     * @return a list of intersection points, or {@code null} if there are no intersections.
     */
    public final List<Point> findIntersections(Ray ray, double maxDistance) {
        List<GeoPoint> geoList = findGeoIntersections(ray, maxDistance);
        return (geoList == null || geoList.isEmpty()) ? null
                : geoList.stream().map(gp -> gp.point).toList();
    }

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     * This version of the method uses the default maximum distance ({@link Double#POSITIVE_INFINITY}).
     *
     * @param ray the ray to intersect with this geometric shape.
     * @return a list of intersection points, or {@code null} if there are no intersections.
     */
    public final List<Point> findIntersections(Ray ray) {
        return findIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method delegates to {@link #findGeoIntersectionsHelper(Ray, double)}.
     *
     * @param ray the ray to intersect with this geometric shape.
     * @return a list of geometric intersection points, or {@code null} if there are no intersections.
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        return findGeoIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * If an AABB is present, it checks for intersection with the AABB first.
     *
     * @param ray         the ray to intersect with this geometric shape.
     * @param maxDistance the maximum distance within which to search for intersections.
     * @return a list of geometric intersection points, or {@code null} if there are no intersections.
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance) {
        return aabb != null && !aabb.intersects(ray) ? null : findGeoIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method must be implemented by subclasses.
     *
     * @param ray         the ray to intersect with this geometric shape.
     * @param maxDistance the maximum distance within which to search for intersections.
     * @return a list of geometric intersection points, or {@code null} if there are no intersections.
     */
    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance);


    /**
     * A record representing a geometric point of intersection.
     * <p>
     * Each {@code GeoPoint} contains the specific geometry associated with the intersection
     * and the 3D point where the intersection occurs.
     *
     * @param geometry the geometry associated with the intersection.
     * @param point    the intersection point in 3D space.
     */
    public record GeoPoint(Geometry geometry, Point point) {

        /**
         * Constructs a {@code GeoPoint} with the specified geometry and intersection point.
         *
         * @param geometry the geometry associated with the intersection.
         * @param point    the intersection point in 3D space.
         */
        public GeoPoint {}

        @Override
        public String toString() {
            return String.format("GeoPoint{geometry=%s, point=%s}", geometry.getClass().getName(), point);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GeoPoint(Geometry geometry1, Point point1))) return false;
            return geometry1 == geometry && point1.equals(point);
        }
    }
}

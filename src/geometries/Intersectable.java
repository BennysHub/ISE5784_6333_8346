package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * An abstract class representing geometric shapes that can be intersected by a ray.
 * Implementing classes will provide the logic to find intersection points.
 */
public abstract class Intersectable {
    /**
     * Axis-Aligned Bounding Box for BVH
     */
    protected AABB aabb;

    /**
     * build AABB for the Intersectable
     */
    protected void calculateAABBHelper() {}

    public void calculateAABB() {
        if (aabb == null) calculateAABBHelper();
    }

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     *
     * @param ray         the ray to intersect with this geometric shape
     * @param maxDistance the maximum distance within which to search for intersections
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray, double maxDistance) {
        List<GeoPoint> geoList = findGeoIntersections(ray, maxDistance);
        return (geoList == null || geoList.isEmpty()) ? null
                : geoList.stream().map(gp -> gp.point).toList();
    }

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     * This version of the method uses the default maximum distance (Double.POSITIVE_INFINITY).
     *
     * @param ray the ray to intersect with this geometric shape
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        return findIntersections(ray, Double.POSITIVE_INFINITY);
    }


    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method uses the helper method implemented by subclasses.
     *
     * @param ray the ray to intersect with this geometric shape
     * @return a list of geometric intersection points, or null if there are no intersections
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        return findGeoIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method uses the helper method implemented by subclasses.
     *
     * @param ray         the ray to intersect with this geometric shape
     * @param maxDistance the maximum distance within which to search for intersections
     * @return a list of geometric intersection points, or null if there are no intersections
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance) {
        return aabb != null && !aabb.intersects(ray) ? null : findGeoIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method must be implemented by subclasses.
     *
     * @param ray         the ray to intersect with this geometric shape
     * @param maxDistance the maximum distance within which to search for intersections
     * @return a list of geometric intersection points, or null if there are no intersections
     */
    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance);

    public Intersectable duplicateObject(Vector vector){
        return duplicateObjectHelper(vector);
    }

    protected abstract Intersectable duplicateObjectHelper(Vector vector);

    /**
     * A class representing a geometric point of intersection.
     */
    public static class GeoPoint {
        /**
         * The geometry associated with this intersection point
         */
        public final Geometry geometry;

        /**
         * The point of intersection
         */
        public final Point point;

        /**
         * Constructs a GeoPoint with the specified geometry and intersection point.
         *
         * @param geometry the geometry of the intersection
         * @param point    the intersection point
         */
        public GeoPoint(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return String.format("GeoPoint{geometry=%s, point=%s}", geometry.getClass().getName(), point);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof GeoPoint other)) return false;
            return other.geometry == geometry && other.point.equals(point);
        }
    }

}

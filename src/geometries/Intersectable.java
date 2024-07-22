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
     * A class representing a geometric point of intersection.
     */
    public static class GeoPoint {
        /**
         * The geometry associated with this intersection point
         */
        public Geometry geometry;

        /**
         * The point of intersection
         */
        public Point point;

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

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     *
     * @param ray the ray to intersect with this geometric shape
     * @return a list of intersection points, or null if there are no intersections
     */
    public final List<Point> findIntersections(Ray ray) {
        List<GeoPoint> geoList = findGeoIntersections(ray);
        return geoList == null ? null
                : geoList.stream().map(gp -> gp.point).toList();
    }

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method must be implemented by subclasses.
     *
     * @param ray the ray to intersect with this geometric shape
     * @return a list of geometric intersection points, or null if there are no intersections
     */
    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray);

    /**
     * Finds the geometric intersection points of a given ray with this geometric shape.
     * This method uses the helper method implemented by subclasses.
     *
     * @param ray the ray to intersect with this geometric shape
     * @return a list of geometric intersection points, or null if there are no intersections
     */
    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        return findGeoIntersectionsHelper(ray);
    }
}

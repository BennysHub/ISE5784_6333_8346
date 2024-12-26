package geometries;
import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * The {@code Intersectable} interface defines the behavior for geometric objects
 * that can be intersected by a ray in 3D space.
 *
 * <p>Implementing classes must provide a method for finding intersections
 * between a ray and the geometry.
 * This is a fundamental operation in 3D graphics and rendering pipelines,
 * where rays are used to compute visibility, shading,
 * and other effects.</p>
 *
 * <p>The interface also includes a default method to handle cases where the
 * maximum distance for intersections is not specified.</p>
 *
 * @author
 * Benny Avrahami
 */
public interface Intersectable {

    /**
     * Finds all intersection points between the geometry and a given ray,
     * up to a specified maximum distance from the ray's origin.
     *
     * @param ray         The ray to check for intersections.
     * @param maxDistance The maximum distance from the ray's origin to consider for intersections.
     * @return A list of {@link GeoPoint} objects representing intersection points,
     *         or {@code null} if there are no intersections within the specified distance.
     */
    List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance);

    /**
     * Finds all intersection points between the geometry and a given ray,
     * with no maximum distance constraint.
     *
     * <p>This is a convenience method that delegates to
     * {@link #findGeoIntersections(Ray, double)} with {@link Double#POSITIVE_INFINITY}
     * as the maximum distance.</p>
     *
     * @param ray The ray to check for intersections.
     * @return A list of {@link GeoPoint} objects representing intersection points,
     *         or {@code null} if there are no intersections.
     */
    default List<GeoPoint> findGeoIntersections(Ray ray) {
        return findGeoIntersections(ray, Double.POSITIVE_INFINITY);
    }


    /**
     * A record representing a geometric point of intersection.
     * <p>
     * Each {@code GeoPoint} contains the specific geometry associated with the intersection
     * and the 3D point where the intersection occurs.
     * </p>
     *
     * @param geometry the geometry associated with the intersection.
     * @param point    the intersection point in 3D space.
     */
     record GeoPoint(Geometry geometry, Point point) {

        /**
         * Returns a string representation of this {@code GeoPoint}.
         *
         * @return a string in the format "GeoPoint{geometry=..., point=...}".
         */
        @Override
        public String toString() {
            return String.format("GeoPoint{geometry=%s, point=%s}", geometry.getClass().getName(), point);
        }

        /**
         * Compares this {@code GeoPoint} with another object for equality.
         * Two {@code GeoPoint} objects are considered equal if their geometry and point are identical.
         *
         * @param obj the object to compare with this {@code GeoPoint}.
         * @return {@code true} if the specified object is equal to this {@code GeoPoint}, {@code false} otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // Same object
            if (!(obj instanceof GeoPoint(Geometry otherGeometry, Point otherPoint))) return false; // Different type
            return geometry == otherGeometry && point.equals(otherPoint);
        }
    }

}

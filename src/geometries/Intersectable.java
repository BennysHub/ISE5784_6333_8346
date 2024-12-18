package geometries;
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
}

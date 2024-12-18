package geometries;

import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * An abstract base class representing a geometric object in 3D space.
 *
 * <p>This class serves as a foundation for all geometric shapes, providing common properties and behavior:
 * <ul>
 *     <li>Material properties, which define the geometry's optical characteristics.</li>
 *     <li>Axis-Aligned Bounding Box (AABB) for spatial optimizations.</li>
 *     <li>Methods for geometric transformations such as translation, scaling, and rotation.</li>
 *     <li>Intersection detection with rays.</li>
 * </ul>
 * Subclasses must implement specific geometric behavior for intersection detection, transformations,
 * and normal calculation.</p>
 *
 * @author
 * Benny Avrahami
 */
public abstract class Geometry implements Transformable, Intersectable {

    /**
     * The Axis-Aligned Bounding Box (AABB) for the geometry.
     * Used for spatial partitioning and intersection optimizations.
     */
    protected AABB aabb;

    /**
     * Material properties of the geometry, such as reflectivity, transparency, and shininess.
     */
    private Material material = new Material();

    /**
     * Calculates the AABB for the geometry.
     * This is a lazy computation performed only when needed.
     */
    public void calculateAABB() {
        if (aabb == null) {
            calculateAABBHelper();
        }
    }

    /**
     * Helper method to calculate the AABB.
     * Must be implemented by subclasses to define the geometry's specific bounding box.
     */
    protected abstract void calculateAABBHelper();

    @Override
    public List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance) {
        // Skip intersection checks if the ray does not intersect the AABB.
        return (aabb != null && !aabb.intersects(ray)) ? null : findGeoIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Finds the intersection points of a ray with this geometry, up to a maximum distance.
     * Must be implemented by subclasses to define specific intersection behavior.
     *
     * @param ray         The ray to check for intersections.
     * @param maxDistance The maximum distance for valid intersections.
     * @return A list of intersection points, or {@code null} if none exist.
     */
    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance);

    /**
     * Retrieves the material properties of the geometry.
     *
     * @return The current {@link Material} of the geometry.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties of the geometry.
     *
     * @param material The new material to apply.
     * @return The current {@code Geometry} instance for method chaining.
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at a given point.
     * Must be implemented by subclasses.
     *
     * @param point The point on the geometry where the normal is to be calculated.
     * @return The normal vector at the specified point.
     */
    public abstract Vector getNormal(Point point);

    @Override
    public final Geometry translate(Vector translationVector) {
        return translateHelper(translationVector).setMaterial(material);
    }

    @Override
    public final Geometry rotate(Vector axis, double angleInRadians) {
        return rotateHelper(axis, angleInRadians).setMaterial(material);
    }

    @Override
    public final Geometry scale(Vector scale) {
        return scaleHelper(scale).setMaterial(material);
    }

    /**
     * Helper method for translating the geometry.
     * Must be implemented by subclasses to define translation behavior.
     *
     * @param translationVector The vector defining the translation direction and magnitude.
     * @return A new {@code Geometry} instance representing the translated geometry.
     */
    protected abstract Geometry translateHelper(Vector translationVector);

    /**
     * Helper method for rotating the geometry.
     * Must be implemented by subclasses to define rotation behavior.
     *
     * @param axis           The axis of rotation.
     * @param angleInRadians The angle of rotation in radians.
     * @return A new {@code Geometry} instance representing the rotated geometry.
     */
    protected abstract Geometry rotateHelper(Vector axis, double angleInRadians);

    /**
     * Helper method for scaling the geometry.
     * Must be implemented by subclasses to define scaling behavior.
     *
     * @param scale The scaling factors for each axis.
     * @return A new {@code Geometry} instance representing the scaled geometry.
     */
    protected abstract Geometry scaleHelper(Vector scale);
}

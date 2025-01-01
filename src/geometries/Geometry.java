package geometries;

import primitives.*;

import java.util.List;

/**
 * Abstract base class representing a geometric object in 3D space.
 * <p>
 * Provides common properties and behavior for geometric shapes, including:
 * <ul>
 *     <li>Material properties to define the optical characteristics.</li>
 *     <li>Axis-Aligned Bounding Box (AABB) for spatial optimizations.</li>
 *     <li>Geometric transformations such as translation, rotation, and scaling.</li>
 *     <li>Intersection detection with rays.</li>
 * </ul>
 * Subclasses must implement specific behavior for transformations, intersection detection, and normal calculations.
 * </p>
 *
 * @author
 * Benny Avrahami
 */
public abstract class Geometry implements Intersectable, Transformable, SignedDistance {

    /**
     * The Axis-Aligned Bounding Box (AABB) for the geometry.
     * Used for spatial partitioning and optimization.
     */
    protected AABB aabb;

    /**
     * The material properties of the geometry, such as reflectivity, transparency, and shininess.
     */
    private Material material = new Material();

    /**
     * Lazily calculates the AABB for the geometry if it has not been calculated yet.
     */
    public void calculateAABB() {
        if (aabb == null) {
            calculateAABBHelper();
        }
    }

    /**
     * Calculates the AABB for the geometry.
     * Must be implemented by subclasses to define the specific bounding box.
     */
    protected abstract void calculateAABBHelper();

    /**
     * Finds intersections of a ray with this geometry, considering a maximum distance.
     * <p>
     * The method skips intersection checks if the ray does not intersect the AABB.
     * </p>
     *
     * @param ray         The ray to intersect with.
     * @param maxDistance The maximum allowable distance for intersections.
     * @return A list of intersection points or {@code null} if no intersections exist.
     */
    @Override
    public List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance) {
        return (aabb != null && !aabb.intersects(ray)) ? null : findGeoIntersectionsHelper(ray, maxDistance);
    }

    /**
     * Abstract method for finding intersections with a ray.
     * Subclasses must implement this to define specific intersection logic.
     *
     * @param ray         The ray to intersect with.
     * @param maxDistance The maximum allowable distance for intersections.
     * @return A list of intersection points or {@code null} if no intersections exist.
     */
    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance);

    /**
     * Gets the material properties of the geometry.
     *
     * @return The material of the geometry.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties of the geometry.
     *
     * @param material The material to set.
     * @return The current geometry instance for method chaining.
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Calculates the normal vector at a specified point on the geometry.
     * Must be implemented by subclasses.
     *
     * @param point The point on the geometry.
     * @return The normal vector at the specified point.
     */
    public abstract Vector getNormal(Point point);

    @Override
    public Geometry translate(Vector translationVector) {
        Geometry translatedGeometry = translateHelper(translationVector);
        translatedGeometry.setMaterial(material);

        if (aabb != null) {
            translatedGeometry.aabb = aabb.translate(translationVector);
        }

        return translatedGeometry;
    }

    @Override
    public Geometry rotate(Quaternion rotation) {
        Geometry rotatedGeometry = rotateHelper(rotation);
        rotatedGeometry.setMaterial(material);

        if (aabb != null) {
            // Since the exact rotation of AABB is not possible,
            // calculate a new AABB that encloses the rotated geometry.

            //rotatedGeometry.aabb = aabb.rotate(rotation);
            rotatedGeometry.calculateAABB();
        }

        return rotatedGeometry;
    }

    @Override
    public Geometry scale(Vector scaleVector) {
        Geometry scaledGeometry = scaleHelper(scaleVector);
        scaledGeometry.setMaterial(material);

        if (aabb != null) {
            scaledGeometry.aabb = aabb.scale(scaleVector);
        }

        return scaledGeometry;
    }

    @Override
    public Geometry scale(double scaleFactor) {
        return scale(new Vector(scaleFactor, scaleFactor, scaleFactor));
    }

    @Override
    public Geometry translateX(double dx) {
        return translate(new Vector(dx, 0, 0));
    }

    @Override
    public Geometry translateY(double dy) {
        return translate(new Vector(0, dy, 0));
    }

    @Override
    public Geometry translateZ(double dz) {
        return translate(new Vector(0, 0, dz));
    }

    /**
     * Helper method to handle translation. Must be implemented by subclasses.
     *
     * @param translationVector The vector defining the translation.
     * @return A new translated geometry.
     */
    protected abstract Geometry translateHelper(Vector translationVector);

    /**
     * Helper method to handle rotation. Must be implemented by subclasses.
     *
     * @param rotation The quaternion representing the rotation.
     * @return A new rotated geometry.
     */
    protected abstract Geometry rotateHelper(Quaternion rotation);

    /**
     * Helper method to handle scaling. Must be implemented by subclasses.
     *
     * @param scaleVector The vector defining the scaling factors along each axis.
     * @return A new scaled geometry.
     */
    protected abstract Geometry scaleHelper(Vector scaleVector);


}

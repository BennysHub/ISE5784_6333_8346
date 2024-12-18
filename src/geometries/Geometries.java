package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.*;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 * Provides support for optimizing ray intersections using a Bounding Volume Hierarchy (BVH).
 *
 * <p>The class can perform geometric transformations (translation, rotation, scaling)
 * and calculate intersection points with rays either using the BVH structure for optimization
 * or directly with individual geometries.</p>
 *
 * @author
 * Benny Avrahami
 */
public class Geometries implements Intersectable {

    /**
     * List of geometries contained in this collection.
     */
    private final List<Geometry> geometries;

    /**
     * The root node of the Bounding Volume Hierarchy (BVH) for optimized intersections.
     */
    private BVHNode bvhRoot;

    /**
     * Default constructor. Creates an empty collection of geometries.
     */
    public Geometries() {
        geometries = new ArrayList<>();
    }

    /**
     * Constructor to initialize the collection with an array of geometries.
     *
     * @param geometries The initial geometries to add to this collection.
     */
    public Geometries(Geometry... geometries) {
        this.geometries = new ArrayList<>();
        add(geometries);
    }

    /**
     * Adds one or more geometries to the collection.
     *
     * @param geometries The geometries to add.
     */
    public void add(Geometry... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Builds the BVH (Bounding Volume Hierarchy) for the geometries in this collection.
     * Uses the Surface Area Heuristic (SAH) method for optimal node splitting.
     */
    public void buildBVH() {
        if (!geometries.isEmpty()) {
            bvhRoot = new BVHNode(geometries, BVHNode.BVHBuildMethod.LINEAR_BVH);
        }
    }

    /**
     * Finds the intersection points of a ray with the geometries in the collection.
     * Uses the BVH for optimization if available.
     *
     * @param ray         The ray to intersect with the geometries.
     * @param maxDistance The maximum distance for valid intersections.
     * @return A list of {@link GeoPoint} objects representing the intersections, or {@code null} if none exist.
     */
    @Override
    public List<GeoPoint> findGeoIntersections(Ray ray, double maxDistance) {
        if (bvhRoot != null) {
            return bvhRoot.findIntersections(ray, maxDistance);
        }

        List<GeoPoint> intersections = null;
        for (Geometry geometry : geometries) {
            var geometryIntersections = geometry.findGeoIntersections(ray, maxDistance);
            if (geometryIntersections != null) {
                if (intersections == null) {
                    intersections = new LinkedList<>(geometryIntersections);
                } else {
                    intersections.addAll(geometryIntersections);
                }
            }
        }
        return intersections;
    }

    /**
     * Calculates the Axis-Aligned Bounding Box (AABB) for all geometries in the collection.
     */
    public void calculateAABB() {
        for (Geometry geometry : geometries) {
            geometry.calculateAABB();
        }
    }

    /**
     * Translates all geometries in the collection by the given vector.
     *
     * @param translationVector The vector by which to translate the geometries.
     */
    public void translate(Vector translationVector) {
        Geometries newGeometries = new Geometries();
        for (Geometry geometry : geometries) {
            newGeometries.add(geometry.translate(translationVector));
        }
    }

    /**
     * Rotates all geometries in the collection around a specified axis by a given angle.
     * The rotation is performed relative to the center of the entire collection.
     *
     * @param axis             The axis of rotation.
     * @param angleInRadians   The angle of rotation in radians.
     */
    public void rotate(Vector axis, double angleInRadians) {
        calculateAABB(); // Ensure the AABB is calculated to find the center
        Point center = getGeometriesCenter();

        // Translate geometries to the origin, rotate, and return them to the original position
        Vector toOrigin = Point.ZERO.subtract(center);
        Vector backToOriginalPosition = toOrigin.scale(-1);

        Geometries newGeometries = new Geometries();
        for (Geometry geometry : geometries) {
            newGeometries.add(
                    geometry.translate(toOrigin)
                            .rotate(axis, angleInRadians)
                            .translate(backToOriginalPosition)
            );
        }
    }

    /**
     * Scales all geometries in the collection by the given scaling vector.
     *
     * @param scale The scaling vector containing factors for each axis.
     */
    public void scale(Vector scale) {
        Geometries newGeometries = new Geometries();
        for (Geometry geometry : geometries) {
            newGeometries.add(geometry.scale(scale));
        }
    }

    /**
     * Calculates the center of all geometries in the collection.
     * This is determined as the centroid of the bounding boxes of all geometries.
     *
     * @return The center point of the geometries in the collection.
     */
    private Point getGeometriesCenter() {
        if (geometries.isEmpty()) {
            throw new IllegalStateException("Cannot calculate the center of an empty collection.");
        }

        double sumX = 0, sumY = 0, sumZ = 0;
        int count = 0;

        for (Geometry geometry : geometries) {
            geometry.calculateAABB();
            Point center = geometry.aabb.getCenterPoint();
            sumX += center.getX();
            sumY += center.getY();
            sumZ += center.getZ();
            count++;
        }

        return new Point(sumX / count, sumY / count, sumZ / count);
    }
}

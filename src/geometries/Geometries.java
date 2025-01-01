package geometries;

import primitives.*;
import primitives.Vector;

import java.util.*;

import static utils.Util.isZero;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 * Provides support for optimizing ray intersections using a Bounding Volume Hierarchy (BVH).
 *
 * <p>The class can perform geometric transformations (translation, rotation, scaling)
 * and calculate intersection points with rays either using the BVH structure for optimization
 * or directly with individual geometries.</p>
 *
 * @author Benny Avrahami
 */
public class Geometries implements Intersectable, Transformable {

    /**
     * List of geometries contained in this collection.
     */
    private final List<Geometry> geometries;

    /**
     * The precomputed center of the geometries in the collection.
     * Used for efficient transformations.
     */
    private Point geometriesCenter;

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
     *
     * @param bvhBuildMethod The method to use for building the BVH.
     */
    public void buildBVH(BVHNode.BVHBuildMethod bvhBuildMethod) {
        if (!geometries.isEmpty()) {
            bvhRoot = new BVHNode(geometries, bvhBuildMethod);
        }
    }

    @Override
    public Geometries translate(Vector translationVector) {
        if (bvhRoot != null) {
            bvhRoot.translate(translationVector);
        } else {
            geometries.replaceAll(geometry -> geometry.translate(translationVector));
        }
        return this;
    }

    @Override
    public Geometries rotate(Vector axis, double angleInRadians) {
        if (isZero(angleInRadians)) return this;

        Quaternion rotation = Quaternion.fromAxisAngle(axis, angleInRadians);
        return rotate(rotation);
    }

    @Override
    public Geometries rotate(Quaternion rotation) {

        calculateAABB();
        Point center = getGeometriesCenter();

        Vector toOrigin = Point.ZERO.subtract(center);
        Vector backToOriginalPosition = toOrigin.scale(-1);

        if (bvhRoot != null) {
            bvhRoot.translate(toOrigin).rotate(rotation).translate(backToOriginalPosition);
        } else {
            geometries.replaceAll(geometry -> geometry.translate(toOrigin).rotate(rotation).translate(backToOriginalPosition));
        }
        return this;
    }

    @Override
    public Geometries scale(Vector scale) {
        calculateAABB();
        Point center = getGeometriesCenter();

        Vector toOrigin = Point.ZERO.subtract(center);
        Vector backToOriginalPosition = toOrigin.scale(-1);

        if (bvhRoot != null) {
            bvhRoot.scale(scale);
        } else {
            geometries.replaceAll(geometry -> geometry.translate(toOrigin).scale(scale).translate(backToOriginalPosition));
        }
        return this;
    }

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
     * Calculates the center of all geometries in the collection.
     * This is determined as the centroid bounding boxes of all geometries.
     *
     * @return The center point of the geometries in the collection.
     */
    private Point getGeometriesCenter() {
        if (geometries.isEmpty()) {
            throw new IllegalStateException("Cannot calculate the center of an empty collection.");
        }

       // if (geometriesCenter != null) return geometriesCenter;

        geometriesCenter = new AABB(geometries).getCenter();
        return geometriesCenter;
    }


    /**
     * Calculates the shortest distance to any object in the scene from the given point.
     *
     * @param point The point to evaluate.
     * @return The minimum distance to the scene objects.
     */
    public double sceneDistance(Point point) {
        double minDistance = Double.POSITIVE_INFINITY;
        for (Geometry geometry : geometries) {
            double distance = geometry.signedDistance(point);
            minDistance = Math.min(minDistance, distance);
        }
        return minDistance;
    }

    public SignedDistance.SDFResult sceneDistanceWithMaterial(Point point) {
        double minDistance = Double.POSITIVE_INFINITY;
        Material material = null;
        for (Geometry geometry : geometries) {
            double distance = geometry.signedDistance(point);
            if (distance < minDistance){
                minDistance = distance;
                material = geometry.getMaterial();
            }
        }
        return new SignedDistance.SDFResult(minDistance, material);
    }
}

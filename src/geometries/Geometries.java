package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.*;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 * This class also provides mechanisms to optimize ray intersections using Bounding Volume Hierarchies (BVH).
 */
public class Geometries extends Intersectable {

    private static final double MAX_OBJECTS_PER_LEAF = 2;

    /**
     * The list of intersectable geometric objects in this collection.
     */
    private List<Intersectable> intersectables;

    private BVHNode bvhRoot; // Root node of the BVH
    private boolean useBVH = false; // Flag to determine if BVH should be used


    /**
     * Default constructor for an empty collection of geometries.
     */
    public Geometries() {
    }

    /**
     * Constructs a Geometries object initialized with the given intersectable objects.
     *
     * @param intersectables The intersectable objects to include in this collection.
     */
    public Geometries(Intersectable... intersectables) {
        add(intersectables);
    }

    /**
     * Adds one or more intersectable objects to the collection.
     *
     * @param intersectables The intersectable objects to add.
     */
    public void add(Intersectable... intersectables) {
        if (this.intersectables == null) {
            this.intersectables = new ArrayList<>();
        }
        Collections.addAll(this.intersectables, intersectables);
    }

    /**
     * Adds all intersectable objects from another Geometries object to this collection.
     *
     * @param otherGeometries The other Geometries object to merge into this collection.
     */
    public void add(Geometries otherGeometries) {
        if (this.intersectables == null) this.intersectables = new ArrayList<>();

        this.intersectables.addAll(otherGeometries.intersectables);
    }

    @Override
    protected void calculateAABBHelper() {
        aabb = new AABB(intersectables);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        if (bvhRoot != null)
            return bvhRoot.findIntersections(ray, maxDistance);

        if (intersectables == null || intersectables.isEmpty()) {
            return null;
        }
        List<GeoPoint> intersections = null;
        for (Intersectable intersectable : intersectables) {
            var geometryIntersections = intersectable.findGeoIntersections(ray, maxDistance);
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


    private void buildBVHWithSAH(List<Intersectable> intersectables, int numObjects) {
        if (numObjects <= MAX_OBJECTS_PER_LEAF) {
            this.intersectables = intersectables;
            calculateAABB();
            return;
        }

        int splitIndex = findOptimalSplit(intersectables, numObjects);

        Geometries leftNode = new Geometries();
        Geometries rightNode = new Geometries();

        leftNode.buildBVHWithSAH(intersectables.subList(0, splitIndex), splitIndex);
        rightNode.buildBVHWithSAH(intersectables.subList(splitIndex, numObjects), numObjects - splitIndex);

        this.intersectables = List.of(leftNode, rightNode);
        aabb = new AABB(leftNode.aabb, rightNode.aabb);
    }

    private int findOptimalSplit(List<Intersectable> intersectables, int numObjects) {
        double bestSplitCost = Double.MAX_VALUE;
        int bestSplitIndex = 0;
        int bestSortAxis = 0;

        for (int sortAxis = 0; sortAxis < 3; sortAxis++) {
            sortByAxis(intersectables, sortAxis);
            for (int splitIndex = 0; splitIndex < numObjects; splitIndex++) {
                AABB leftBoundingBox = new AABB(intersectables.subList(0, splitIndex));
                AABB rightBoundingBox = new AABB(intersectables.subList(splitIndex, numObjects));
                double splitCost = calculateSplitCost(leftBoundingBox, rightBoundingBox, splitIndex, numObjects - splitIndex);
                if (splitCost < bestSplitCost) {
                    bestSplitCost = splitCost;
                    bestSplitIndex = splitIndex;
                    bestSortAxis = sortAxis;
                }
            }
        }

        if (bestSortAxis != 2) {
            sortByAxis(intersectables, bestSortAxis);
        }
        return bestSplitIndex;
    }

    private void buildBVHWithMedianSplit(int currentDepth, int numObjects, int splitIndex, List<Intersectable> intersectables) {
        if (numObjects <= MAX_OBJECTS_PER_LEAF) {
            this.intersectables = intersectables;
            calculateAABB();
            return;
        }

        sortByAxis(intersectables, currentDepth);

        Geometries leftNode = new Geometries();
        Geometries rightNode = new Geometries();

        leftNode.buildBVHWithMedianSplit(currentDepth + 1, splitIndex, splitIndex / 2, intersectables.subList(0, splitIndex));
        rightNode.buildBVHWithMedianSplit(currentDepth + 1, numObjects - splitIndex, (numObjects - splitIndex) / 2, intersectables.subList(splitIndex, numObjects));

        this.intersectables = List.of(leftNode, rightNode);
        aabb = new AABB(leftNode.aabb, rightNode.aabb);
    }

    private void sortByAxis(List<Intersectable> intersectables, int sortAxis) {
        intersectables.sort(Comparator.comparingDouble(intersectable -> intersectable.aabb.getCenter()[sortAxis % 3]));
    }

    private double calculateSplitCost(AABB leftBBox, AABB rightBBox, int leftCount, int rightCount) {
        double leftArea = leftBBox.surfaceArea();
        double rightArea = rightBBox.surfaceArea();
        return (leftArea * leftCount) + (rightArea * rightCount);
    }

    public void buildBVH() {
        // buildBVHWithSAH(intersectables, intersectables.size());

        if (useBVH) {
            bvhRoot = new BVHNode(intersectables);
            return;
        }

        buildBVHWithMedianSplit(0, intersectables.size(), intersectables.size() / 2, intersectables);
    }







    @Override
    public Intersectable translate(Vector translationVector) {
        Geometries newGeometries = new Geometries();
        for (Intersectable intersectable : intersectables) {
            newGeometries.add(intersectable.translate(translationVector));
        }
        return newGeometries;
    }

    @Override
    public Intersectable rotate(Vector axis, double angleInRadians) {
        this.calculateAABB(); // Calculate AABB to find the center of rotation
        var aabbCenter = aabb.getCenter();
        Point center = new Point(aabbCenter[0], aabbCenter[1], aabbCenter[2]);

        // Compute the translation to move the object to the origin, rotate, and return it
        Vector toOrigin = Point.ZERO.subtract(center);
        Vector backToOriginalPosition = toOrigin.scale(-1);

        Geometries newGeometries = new Geometries();
        for (Intersectable intersectable : intersectables) {
            newGeometries.add(
                    intersectable.translate(toOrigin)
                            .rotate(axis, angleInRadians)
                            .translate(backToOriginalPosition)
            );
        }
        return newGeometries;
    }

    @Override
    public Intersectable scale(Vector scale) {
        Geometries newGeometries = new Geometries();
        for (Intersectable intersectable : intersectables) {
            newGeometries.add(intersectable.scale(scale));
        }
        return newGeometries;
    }
}

package geometries;

import primitives.Ray;
import renderer.RenderSettings;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 * This class also provides mechanisms to optimize ray intersections using Bounding Volume Hierarchies (BVH).
 */
public class Geometries extends Intersectable {

    /**
     * The cost of traversing a BVH node.
     */
    private static final double C_TRAVERSAL = 1;
    /**
     * The maximum number of primitives allowed in a leaf node of the BVH.
     */
    private static final double MAX_PRIMITIVES_PER_LEAF = 2;
    /**
     * Flag to choose the median-split method for BVH construction.
     * If {@code true}, the BVH will use the median-split method; otherwise, it will use the SAH method.
     */
    private static final Boolean MEDIAN_METHOD = true;
    /**
     * Flag to enable or disable BVH traversal.
     * If {@code true}, BVH traversal will be used; otherwise, the default intersection method will be used.
     */
    private static final Boolean BVH_METHOD = false;
    /**
     * A list to hold geometric objects that implement the Intersectable interface.
     */
    private final List<Intersectable> geometries = new LinkedList<>();

    /**
     * Default constructor to create an empty Geometries object.
     */
    public Geometries() {
    }

    /**
     * Constructor to create a Geometries object with an initial set of geometric objects.
     *
     * @param geometries Varargs of geometric objects that implement the Intersectable interface.
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds geometric objects to the collection.
     *
     * @param geometries Varargs of geometric objects that implement the Intersectable interface.
     */
    public void add(Intersectable... geometries) {
        Collections.addAll(this.geometries, geometries);
    }

    /**
     * Calculates the axis-aligned bounding box (AABB) for the set of geometries.
     * This method should be called before performing BVH construction or ray intersections.
     */
    @Override
    void calculateAABB() {
        aabb = new AABB(geometries);
    }

    /**
     * Finds all the intersections of a ray with the geometric objects in the collection.
     * Depending on the render settings, it uses either BVH traversal or a brute-force intersection check.
     *
     * @param ray         The ray to check intersections with.
     * @param maxDistance The maximum distance for the intersection.
     * @return A list of GeoPoints where the ray intersects geometries within the maximum distance.
     */
    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        return RenderSettings.isBVHEnabled() ? BVHIntersection(ray, maxDistance) : allGeometriesIntersection(ray, maxDistance);
    }

    /**
     * Finds intersections by brute-force checking all geometries.
     *
     * @param ray         The ray to check for intersections.
     * @param maxDistance The maximum distance for intersections.
     * @return A list of GeoPoints where the ray intersects geometries within the maximum distance.
     */
    private List<GeoPoint> allGeometriesIntersection(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = null;
        for (Intersectable intersectable : geometries) {
            var geometryIntersections = intersectable.findGeoIntersections(ray, maxDistance);
            if (geometryIntersections != null) {
                if (intersections == null)
                    intersections = new LinkedList<>(geometryIntersections);
                else
                    intersections.addAll(geometryIntersections);
            }
        }
        return intersections;
    }

    /**
     * BVH-based intersection, used when BVH is enabled.
     *
     * @param ray         The ray to intersect.
     * @param maxDistance The maximum distance for intersections.
     * @return A list of GeoPoints where the ray intersects geometries within the BVH.
     */
    private List<GeoPoint> BVHIntersection(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = new LinkedList<>();
        BVHIntersectionHelper(ray, maxDistance, intersections);
        return intersections;
    }

    /**
     * Recursively traverses the BVH tree to find intersections.
     *
     * @param ray           The ray to intersect.
     * @param maxDistance   The maximum distance for intersections.
     * @param intersections A list to store the found intersections.
     */
    private void BVHIntersectionHelper(Ray ray, double maxDistance, List<GeoPoint> intersections) {
        for (Intersectable intersectable : geometries) {
            if (aabb.rayIntersects(ray)) {
                if (intersectable instanceof Geometries other)
                    other.BVHIntersectionHelper(ray, maxDistance, intersections);
                else {
                    var geometryIntersections = intersectable.findGeoIntersections(ray, maxDistance);
                    if (geometryIntersections != null)
                        intersections.addAll(geometryIntersections);
                }
            }
        }
    }
//    private List<GeoPoint> BVHIntersectionHelper(Ray ray, double maxDistance) {
//        // Stack to hold the nodes (Intersectables) to be processed
//        LinkedList<Intersectable> nodesToVisit = new LinkedList<>();
//        List<GeoPoint> intersections = new LinkedList<>();
//        nodesToVisit.add(this); // Start with the root
//
//        while (!nodesToVisit.isEmpty()) {
//            Intersectable node = nodesToVisit.poll(); // Get the next node to process
//
//            // Check if the node's AABB intersects the ray
//            if (node.aabb.rayIntersects(ray)) {
//                if (node instanceof Geometries geometriesNode) {
//                    nodesToVisit.addAll(geometriesNode.geometries);
//
//                } else {
//                    // If the node is a leaf (primitive), check for intersections
//                    var geometryIntersections = node.findGeoIntersections(ray, maxDistance);
//                    if (geometryIntersections != null) {
////                        if (intersections == null)
////                            intersections = new LinkedList<>(geometryIntersections);
////                        else
//                        intersections.addAll(geometryIntersections);
//                    }
//                }
//            }
//        }
//        return intersections;
//    }

    /**
     * Builds the BVH for the collection of geometries.
     * This function organizes the geometries into a binary tree structure to optimize intersection queries.
     */
    public void buildBVH() {
        recursiveBuildBVH(0);
    }

    /**
     * Recursively builds the BVH tree by splitting the geometries based on the specified method.
     *
     * @param depth The current depth of recursion, used for alternating between split axes.
     */
    private void recursiveBuildBVH(int depth) {
        if (geometries.size() <= MAX_PRIMITIVES_PER_LEAF) {
            calculateAABB();
            return;
        }
        Geometries left = new Geometries();
        Geometries right = new Geometries();

        if (MEDIAN_METHOD) {
            medianMethod(left.geometries, right.geometries, depth);
        } else {
            SAHMethod(left.geometries, right.geometries);
        }
        //geometries = List.of(left, right);
        geometries.clear(); // Clear current geometries and add sub-geometries.
        add(left, right);
        left.recursiveBuildBVH(depth + 1);
        right.recursiveBuildBVH(depth + 1);
        aabb = new AABB(left.aabb);
        aabb.merge(right.aabb);
    }

    /**
     * Splits the list of geometries into two sublist.
     *
     * @param leftList  The list to store the left sub-geometries.
     * @param rightList The list to store the right sub-geometries.
     * @param index     The index at which to split the list.
     */
    private void split(List<Intersectable> leftList, List<Intersectable> rightList, int index) {
        leftList.addAll(geometries.subList(0, index));
        rightList.addAll(geometries.subList(index, geometries.size()));
    }

    /**
     * Sorts the geometries by their center along a given axis.
     *
     * @param axis The axis along which to sort (0 = X, 1 = Y, 2 = Z).
     */
    private void sortByAxis(int axis) {
        geometries.sort(Comparator.comparingDouble(a -> a.aabb.getCenter()[axis]));
    }

    /**
     * Splits geometries based on the median method, where the geometries are sorted by their center along a given axis.
     *
     * @param leftList  The list to store the left sub-geometries.
     * @param rightList The list to store the right sub-geometries.
     * @param depth     The current depth of the BVH tree, used to choose the split axis.
     */
    private void medianMethod(List<Intersectable> leftList, List<Intersectable> rightList, int depth) {
        sortByAxis(depth % 3);
        split(leftList, rightList, geometries.size() / 2);
    }


    /**
     * Splits geometries based on the Surface Area Heuristic (SAH) method, which minimizes the cost of the split.
     *
     * @param leftList  The list to store the left sub-geometries.
     * @param rightList The list to store the right sub-geometries.
     */
    private void SAHMethod(List<Intersectable> leftList, List<Intersectable> rightList) {
        double bestCost = Double.MAX_VALUE;
        int bestSplitIndex = 0;
        int bestSortingAxis = 0;
        for (int axis = 0; axis < 3; axis++) {
            sortByAxis(axis);
            for (int index = 0; index < geometries.size(); index++) {
                AABB leftBoundingBox = new AABB(geometries.subList(0, index));
                AABB rightBoundingBox = new AABB(geometries.subList(index, geometries.size()));
                double cost = computeSAHCost(leftBoundingBox, rightBoundingBox, index, geometries.size() - index);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestSplitIndex = index;
                    bestSortingAxis = axis;
                }
            }
        }
        if (bestSortingAxis != 2)
            sortByAxis(bestSortingAxis);
        split(leftList, rightList, bestSplitIndex);
    }

    /**
     * Computes the Surface Area Heuristic (SAH) cost for a split.
     *
     * @param leftBBox   The bounding box of the left sub-geometries.
     * @param rightBBox  The bounding box of the right sub-geometries.
     * @param leftCount  The number of geometries in the left sublist.
     * @param rightCount The number of geometries in the right sublist.
     * @return The cost of the split based on the SAH formula.
     */
    private double computeSAHCost(AABB leftBBox, AABB rightBBox, int leftCount, int rightCount) {
        AABB parentBBox = new AABB(leftBBox);
        parentBBox.merge(rightBBox);
        double parentArea = parentBBox.surfaceArea();
        double leftArea = leftBBox.surfaceArea();
        double rightArea = rightBBox.surfaceArea();
        return C_TRAVERSAL + (leftArea / parentArea) * leftCount + (rightArea / parentArea) * rightCount;
    }
}

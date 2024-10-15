package geometries;

import primitives.Ray;
import primitives.Vector;

import java.util.*;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 * This class also provides mechanisms to optimize ray intersections using Bounding Volume Hierarchies (BVH).
 */
public class Geometries extends Intersectable {


    private static final double MAX_PRIMITIVES_PER_LEAF = 1;


    private List<Intersectable> geometries;


    public Geometries() {
    }


    public Geometries(Intersectable... geometries) {
        add(geometries);
    }


    public void add(Intersectable... geometries) {
        if (this.geometries == null)
            this.geometries = new ArrayList<>();
        Collections.addAll(this.geometries, geometries);
    }

    public void add(Geometries geometries){
        if (this.geometries == null)
            this.geometries = new ArrayList<>();
        this.geometries.addAll(geometries.geometries);
    }


    @Override
    protected void calculateAABBHelper() {
        aabb = new AABB(geometries);
    }


    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
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

    @Override
    protected Intersectable duplicateObjectHelper(Vector vector) {
        Geometries duplicate = new Geometries();
        for (Intersectable intersectable : geometries)
            duplicate.add(intersectable.duplicateObject(vector));
        return duplicate;
    }


    private void buildBVHUsingSAH(List<Intersectable> geometries, int listSize) {
        if (listSize <= MAX_PRIMITIVES_PER_LEAF) {
            this.geometries = geometries;
            calculateAABB();
            return;
        }


      int bestSplitIndex = findBestSplitAndSort(geometries, listSize);

        Geometries left = new Geometries();
        Geometries right = new Geometries();

        left.buildBVHUsingSAH(geometries.subList(0, bestSplitIndex), bestSplitIndex);
        right.buildBVHUsingSAH(geometries.subList(bestSplitIndex, listSize), listSize - bestSplitIndex);
        this.geometries = List.of(left, right);
        aabb = new AABB(left.aabb, right.aabb);
    }

    int findBestSplitAndSort(List<Intersectable> geometries, int listSize){
        double bestCost = Double.MAX_VALUE;
        int bestSplitIndex = 0;
        int bestSortingAxis = 0;
        for (int axis = 0; axis < 3; axis++) {
            sortByAxis(geometries, axis);
            for (int index = 0; index < listSize; index++) {
                AABB leftBoundingBox = new AABB(geometries.subList(0, index));
                AABB rightBoundingBox = new AABB(geometries.subList(index, listSize));
                double cost = simpleCost(leftBoundingBox, rightBoundingBox, index, listSize - index);
                if (cost < bestCost) {
                    bestCost = cost;
                    bestSplitIndex = index;
                    bestSortingAxis = axis;
                }
            }
        }
        if (bestSortingAxis != 2)
            sortByAxis(geometries, bestSortingAxis);
        return bestSplitIndex;
    }


    private void buildBVHUsingMedianSplit(int depth, int listSize, int medianIndex, List<Intersectable> geometries) {
        // Base case: If the list size is small enough, treat this node as a leaf node
        if (listSize <= MAX_PRIMITIVES_PER_LEAF) {
            this.geometries = geometries;  // Capture the primitives in this node
            calculateAABB();  // Calculate the bounding box
            return;
        }

        sortByAxis(geometries, depth);

        // Initialize child nodes
        Geometries left = new Geometries();
        Geometries right = new Geometries();

        // Recursively build left and right BVH using portions of the list
        left.buildBVHUsingMedianSplit(depth + 1, medianIndex, medianIndex / 2, geometries.subList(0, medianIndex));
        right.buildBVHUsingMedianSplit(depth + 1, listSize - medianIndex, (listSize - medianIndex) / 2, geometries.subList(medianIndex, listSize));

        // Store the children nodes in this geometry
        this.geometries = List.of(left, right);

        // Merge the bounding boxes from left and right child nodes
        aabb = new AABB(left.aabb, right.aabb);
    }



    private void sortByAxis(List<Intersectable> geometries, int axis) {
        geometries.sort(Comparator.comparingDouble(a -> a.aabb.getCenter()[axis%3]));
    }

    private double simpleCost(AABB leftBBox, AABB rightBBox, int leftCount, int rightCount) {
        double leftArea = leftBBox.surfaceArea();
        double rightArea = rightBBox.surfaceArea();
        return (leftArea) * leftCount + (rightArea) * rightCount;
    }

    public void buildBVH() {
      // buildBVHUsingSAH(geometries, geometries.size());
       buildBVHUsingMedianSplit(0, geometries.size(), geometries.size() / 2, geometries);
    }

}

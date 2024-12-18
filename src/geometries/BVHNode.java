package geometries;

import primitives.Ray;

import java.util.*;

/**
 * Represents a node in a Bounding Volume Hierarchy (BVH).
 * BVH is used to speed up ray intersection tests by spatially partitioning geometry.
 */
public class BVHNode {
    private AABB aabb; // The bounding box enclosing the node
    private BVHNode leftChild; // Left child node
    private BVHNode rightChild; // Right child node
    private List<Intersectable> geometries; // List of geometries (leaf nodes)

    private static final int MAX_OBJECTS_PER_LEAF = 2;

    /**
     * Constructs a BVHNode from a list of intersectable objects.
     *
     * @param geometries The list of geometries to include in this node.
     */
    public BVHNode(List<Intersectable> geometries) {
        buildBVH(geometries, 0);
    }

    private BVHNode(List<Intersectable> geometries, int depth) {
        buildBVH(geometries, depth);
    }


    /**
     * Builds the BVH for the given geometries, using the median split strategy.
     *
     * @param geometries The list of geometries to split and include in the BVH.
     * @param depth      The current depth in the BVH tree.
     */
    private void buildBVH(List<Intersectable> geometries, int depth) {
        if (geometries.size() <= MAX_OBJECTS_PER_LEAF) {
            // This is a leaf node
            this.geometries = geometries;
            this.aabb = new AABB(geometries);
            return;
        }

        // Sort geometries by the current axis
        int axis = depth % 3; // Cycle through x, y, z axes
        geometries.sort(Comparator.comparingDouble(geom -> geom.aabb.getCenter()[axis]));

        // Find the split index
        int splitIndex = geometries.size() / 2;

        // Split geometries into left and right lists
        List<Intersectable> leftGeometries = new ArrayList<>(geometries.subList(0, splitIndex));
        List<Intersectable> rightGeometries = new ArrayList<>(geometries.subList(splitIndex, geometries.size()));

        // Recursively build child nodes
        this.leftChild = new BVHNode(leftGeometries, 1);
        this.rightChild = new BVHNode(rightGeometries, 1);

        // Compute the bounding box for this node
        this.aabb = new AABB(leftChild.aabb, rightChild.aabb);
    }

    /**
     * Finds the longest axis of the bounding box.
     *
     * @return The index of the longest axis (0 = X, 1 = Y, 2 = Z).
     */
    private int findLongestAxis() {
        double[] lengths = aabb.getLengths();
        if (lengths[0] > lengths[1] && lengths[0] > lengths[2]) return 0;
        else if (lengths[1] > lengths[2]) return 1;
        return 2;
    }

    /**
     * Finds intersections with a given ray, optimized using the BVH.
     *
     * @param ray         The ray to test for intersections.
     * @param maxDistance The maximum allowed intersection distance.
     * @return A list of intersection points.
     */
    public List<Intersectable.GeoPoint> findIntersections(Ray ray, double maxDistance) {
//        List<Intersectable.GeoPoint> intersections = new ArrayList<>();
//
//        // Check if the ray intersects the bounding box
//        if (!aabb.intersects(ray)) return null;
//
//        // If this is a leaf node, test for intersections with the geometries
//        if (objects != null) {
//            for (Intersectable obj : objects) {
//                List<Intersectable.GeoPoint> geoPoints = obj.findGeoIntersections(ray, maxDistance);
//                if (geoPoints != null) intersections.addAll(geoPoints);
//            }
//            return intersections.isEmpty() ? null : intersections;
//        }
//
//        // Otherwise, recursively test the left and right children
//        List<Intersectable.GeoPoint> leftIntersections = leftChild.findIntersections(ray, maxDistance);
//        List<Intersectable.GeoPoint> rightIntersections = rightChild.findIntersections(ray, maxDistance);
//
//        if (leftIntersections != null) intersections.addAll(leftIntersections);
//        if (rightIntersections != null) intersections.addAll(rightIntersections);
//
//        return intersections.isEmpty() ? null : intersections;


        List<Intersectable.GeoPoint> intersections = new ArrayList<>();

        findIntersections(ray, maxDistance, this, intersections);
        //findIntersectionsIterative(ray, maxDistance, this, intersections);
        return intersections;
    }


    /**
     * Recursively finds intersections of a ray with objects in a BVH tree.
     *
     * @param ray           The ray to test for intersections.
     * @param maxDistance   The maximum valid distance for intersections.
     * @param curr          The current BVH node being checked.
     * @param intersections A list to collect found intersection points.
     */
    public void findIntersections(Ray ray, double maxDistance, BVHNode curr, List<Intersectable.GeoPoint> intersections) {
        // Base case: If the current node is null or its AABB does not intersect the ray, return.
        if (curr == null || !curr.aabb.intersects(ray)) {
            return;
        }

        // If this is a leaf node (contains geometries), check for intersections with its geometries.
        if (curr.geometries != null) {
            for (Intersectable obj : curr.geometries) {
                List<Intersectable.GeoPoint> geoPoints = obj.findGeoIntersections(ray, maxDistance);
                if (geoPoints != null) {
                    intersections.addAll(geoPoints);
                }
            }
            return; // No need to recurse further if this is a leaf node.
        }

        // Recursively check left and right children.
        findIntersections(ray, maxDistance, curr.leftChild, intersections);
        findIntersections(ray, maxDistance, curr.rightChild, intersections);
    }

    public void findIntersectionsIterative(Ray ray, double maxDistance, BVHNode root, List<Intersectable.GeoPoint> intersections) {
        if (root == null) return;

        Deque<BVHNode> stack = new ArrayDeque<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            BVHNode curr = stack.pop();

            // Skip nodes that don't intersect the ray
            if (!curr.aabb.intersects(ray)) continue;

            if (curr.geometries != null) {
                // Process leaf node
                for (Intersectable geometry : curr.geometries) {
                    List<Intersectable.GeoPoint> geoPoints = geometry.findGeoIntersections(ray, maxDistance);
                    if (geoPoints != null) {
                        intersections.addAll(geoPoints);
                    }
                }
            } else {
                // Push children to the stack
                if (curr.rightChild != null) stack.push(curr.rightChild);
                if (curr.leftChild != null) stack.push(curr.leftChild);
            }
        }
    }

}

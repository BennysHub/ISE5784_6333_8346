package geometries;

import primitives.Ray;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents a node in a Bounding Volume Hierarchy (BVH).
 * BVH is used to optimize ray-object intersection tests by spatially partitioning geometry.
 */
public class BVHNode {

    /**
     * Enum defining the methods to build a BVH.
     */
    public enum BVHBuildMethod {
        SAH, // Surface Area Heuristic
        MEDIAN_SPLIT, // Median split strategy
        LINEAR_BVH // Linear BVH construction
    }

    private AABB aabb; // The bounding box enclosing the node
    private BVHNode leftChild; // Left child node
    private BVHNode rightChild; // Right child node
    private Geometry[] geometries; // List of geometries (only for leaf nodes)

    private static final int MAX_OBJECTS_PER_LEAF = 2;

    /**
     * Constructs a BVHNode from a list of geometries and a specified build method.
     *
     * @param geometries The list of geometries to include in this node.
     * @param bvhBuildMethod The method used to build the BVH (e.g., SAH, median split).
     */
    public BVHNode(List<Geometry> geometries, BVHBuildMethod bvhBuildMethod) {
        buildBVH(geometries, bvhBuildMethod);
    }

    private BVHNode() {
    }

    private void buildBVH(List<Geometry> geometries, BVHBuildMethod bvhBuildMethod) {
        switch (bvhBuildMethod) {
            case SAH -> buildUsingSAH(geometries);
            case MEDIAN_SPLIT -> buildUsingMedianSplit(geometries);
            case LINEAR_BVH -> buildUsingLinearBVH(geometries);
            default -> throw new IllegalArgumentException("Invalid BVH build method specified.");
        }
    }

    private void buildUsingSAH(List<Geometry> geometries) {
        if (geometries.size() <= MAX_OBJECTS_PER_LEAF) {
            this.geometries = geometries.toArray(Geometry[]::new);
            aabb = new AABB(geometries);
            return;
        }

        int splitIndex = findOptimalSplit(geometries, geometries.size());

        // Recursively build child nodes
        leftChild = new BVHNode();
        rightChild = new BVHNode();

        leftChild.buildUsingSAH(geometries.subList(0, splitIndex));
        rightChild.buildUsingSAH(geometries.subList(splitIndex, geometries.size()));

        aabb = new AABB(leftChild.aabb, rightChild.aabb);
    }

    private void buildUsingMedianSplit(List<Geometry> geometries) {
        buildUsingMedianSplitHelper(geometries, 0);
    }

    private void buildUsingMedianSplitHelper(List<Geometry> geometries, int depth) {
        if (geometries.size() <= MAX_OBJECTS_PER_LEAF) {
            // This is a leaf node
            this.geometries = geometries.toArray(Geometry[]::new);
            aabb = new AABB(geometries);
            return;
        }

        geometries.sort(Comparator.comparingDouble(geom -> geom.aabb.getCenter()[depth % 3]));
        int splitIndex = geometries.size() / 2;

        // Recursively build child nodes
        leftChild = new BVHNode();
        rightChild = new BVHNode();

        leftChild.buildUsingMedianSplitHelper(geometries.subList(0, splitIndex), depth + 1);
        rightChild.buildUsingMedianSplitHelper(geometries.subList(splitIndex, geometries.size()), depth + 1);

        aabb = new AABB(leftChild.aabb, rightChild.aabb);
    }

    private int findOptimalSplit(List<Geometry> geometries, int numObjects) {
        double bestSplitCost = Double.MAX_VALUE;
        int bestSplitIndex = 0;
        int bestSortAxis = 0;

        for (int sortAxis = 0; sortAxis < 3; sortAxis++) {
            sortByAxis(geometries, sortAxis);
            for (int splitIndex = 0; splitIndex < numObjects; splitIndex++) {
                AABB leftBoundingBox = new AABB(geometries.subList(0, splitIndex));
                AABB rightBoundingBox = new AABB(geometries.subList(splitIndex, numObjects));
                double splitCost = calculateSplitCost(leftBoundingBox, rightBoundingBox, splitIndex, numObjects - splitIndex);
                if (splitCost < bestSplitCost) {
                    bestSplitCost = splitCost;
                    bestSplitIndex = splitIndex;
                    bestSortAxis = sortAxis;
                }
            }
        }

        if (bestSortAxis != 2) {
            sortByAxis(geometries, bestSortAxis);
        }
        return bestSplitIndex;
    }

    private void sortByAxis(List<Geometry> geometries, int sortAxis) {
        geometries.sort(Comparator.comparingDouble(geometry -> geometry.aabb.getCenter()[sortAxis % 3]));
    }


    private double calculateSplitCost(AABB leftBBox, AABB rightBBox, int leftCount, int rightCount) {
        double leftArea = leftBBox.surfaceArea();
        double rightArea = rightBBox.surfaceArea();
        return (leftArea * leftCount) + (rightArea * rightCount);
    }

    /**
     * Finds intersections with a given ray, optimized using the BVH.
     *
     * @param ray The ray to test for intersections.
     * @param maxDistance The maximum allowed intersection distance.
     * @return A list of intersection points.
     */
    public List<GeoPoint> findIntersections(Ray ray, double maxDistance) {
        List<GeoPoint> intersections = new ArrayList<>();
        findIntersectionsRecursive(ray, maxDistance, this, intersections);
        return intersections;
    }

    private void findIntersectionsRecursive(Ray ray, double maxDistance, BVHNode node, List<GeoPoint> intersections) {
        if (!node.aabb.intersects(ray)) {
            return;
        }

        if (node.geometries != null) {
            for (Geometry geometry : node.geometries) {
                List<GeoPoint> geoPoints = geometry.findGeoIntersections(ray, maxDistance);
                if (geoPoints != null) {
                    intersections.addAll(geoPoints);
                }
            }
            return;
        }

        findIntersectionsRecursive(ray, maxDistance, node.leftChild, intersections);
        findIntersectionsRecursive(ray, maxDistance, node.rightChild, intersections);
    }



    private void buildUsingLinearBVH(List<Geometry> geometries) {
        // Step 1: Compute Morton codes in parallel
        List<MortonCodeGeometry> mortonGeometries = geometries.parallelStream()
                .map(geometry -> new MortonCodeGeometry(geometry, computeMortonCode(geometry.aabb.getCenter())))
                .sorted(Comparator.comparingLong(MortonCodeGeometry::mortonCode))
                .toList();

        // Step 2: Create leaf nodes in parallel
        List<BVHNode> leafNodes = mortonGeometries.parallelStream()
                .map(mg -> {
                    BVHNode leaf = new BVHNode();
                    leaf.geometries = new Geometry[]{mg.geometry};
                    leaf.aabb = mg.geometry.aabb;
                    return leaf;
                })
                .toList();

        // Step 3: Bottom-up construction with multi-threading
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        while (leafNodes.size() > 1) {
            List<BVHNode> nextLevelNodes = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(leafNodes.size() / 2);

            for (int i = 0; i < leafNodes.size(); i += 2) {
                int index = i; // For lambda compatibility
                List<BVHNode> finalLeafNodes = leafNodes;
                executor.submit(() -> {
                    try {
                        if (index + 1 < finalLeafNodes.size()) {
                            // Combine two nodes
                            BVHNode left = finalLeafNodes.get(index);
                            BVHNode right = finalLeafNodes.get(index + 1);
                            BVHNode parent = new BVHNode();

                            parent.leftChild = left;
                            parent.rightChild = right;
                            parent.aabb = new AABB(left.aabb, right.aabb);

                            synchronized (nextLevelNodes) {
                                nextLevelNodes.add(parent);
                            }
                        } else {
                            // If there's an odd node, promote it directly
                            synchronized (nextLevelNodes) {
                                nextLevelNodes.add(finalLeafNodes.get(index));
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for all threads to finish processing the current level
            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted during BVH construction", e);
            }

            leafNodes = nextLevelNodes;
        }

        // Shutdown the executor
        executor.shutdown();

        // Step 4: Assign the root node
        BVHNode root = leafNodes.getFirst();
        this.aabb = root.aabb;
        this.geometries = null;
        this.leftChild = root.leftChild;
        this.rightChild = root.rightChild;
    }

    private long computeMortonCode(double[] center) {
        // Maps a 3D point to a 1D Morton code for spatial locality.
        int x = (int) (center[0] * 1024); // Scale to a fixed grid
        int y = (int) (center[1] * 1024);
        int z = (int) (center[2] * 1024);

        return interleaveBits(x) | (interleaveBits(y) << 1) | (interleaveBits(z) << 2);
    }

    private long interleaveBits(int value) {
        value = (value | (value << 16)) & 0x030000FF;
        value = (value | (value << 8)) & 0x0300F00F;
        value = (value | (value << 4)) & 0x030C30C3;
        value = (value | (value << 2)) & 0x09249249;
        return value;
    }

    private record MortonCodeGeometry(Geometry geometry, long mortonCode) {}
}

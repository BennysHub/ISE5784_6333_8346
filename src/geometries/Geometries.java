package geometries;

import primitives.Ray;
import renderer.RenderSettings;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 */
public class Geometries extends Intersectable {

    /**
     * A list to hold an arbitrary number of geometric objects that implement the Intersectable interface.
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

//    @Override
//    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
//        List<GeoPoint> combinedIntersections = null;
//
//        for (Intersectable intersectable : geometries) {
//            var intersections = intersectable.findGeoIntersections(ray, maxDistance);
//            if (intersections != null) {
//                if (combinedIntersections == null)
//                    combinedIntersections = new LinkedList<>(intersections);
//                else
//                    combinedIntersections.addAll(intersections);
//            }
//        }
//
//        return combinedIntersections;
//    }


    @Override
    void calculateAABB() {
        aabb = new AABB();
        for (Intersectable intersectable : geometries)
            aabb.merge(intersectable.aabb);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        List<GeoPoint> combinedIntersections = new LinkedList<>();
        if (RenderSettings.isBVHEnabled())
            BVHIntersection(ray, maxDistance, combinedIntersections);
        else
            allGeometriesIntersection(ray, maxDistance, combinedIntersections);
        return combinedIntersections;
    }

    private void allGeometriesIntersection(Ray ray, double maxDistance, List<GeoPoint> intersection) {
        for (Intersectable geometry : geometries) {
            var geometryIntersections = geometry.findGeoIntersections(ray, maxDistance);
            if (geometryIntersections != null)
                intersection.addAll(geometryIntersections);
        }
    }

    private void BVHIntersection(Ray ray, double maxDistance, List<GeoPoint> intersection) {//support multiple child tree
        for (Intersectable intersectable : geometries) {
            if (aabb.rayIntersects(ray))
                if (intersectable instanceof Geometries other)
                    other.BVHIntersection(ray, maxDistance, intersection);
                else {
                    var geometryIntersections = intersectable.findGeoIntersections(ray, maxDistance);
                    if (geometryIntersections != null)
                        intersection.addAll(geometryIntersections);
                }
        }
    }

    public void buildBVH(int depth) {
        calculateAABB();
        if (geometries.size() <= 2)
            return;
        Geometries left = new Geometries();
        Geometries right = new Geometries();
        sortAndSplit(left, right, depth);
        left.buildBVH(depth + 1);
        right.buildBVH(depth + 1);
    }


    private void sortAndSplit(Geometries left, Geometries right, int depth) {
        geometries.sort((i1, i2) -> {
            int axis = depth % 3;
            double[] centerG1 = i1.aabb.getCenter();
            double[] centerG2 = i2.aabb.getCenter();
            return Double.compare(centerG1[axis], centerG2[axis]);
        });
        int mid = geometries.size() / 2;

        // Split the list into two halves
        left.geometries.addAll(geometries.subList(0, mid));
        right.geometries.addAll(geometries.subList(mid, geometries.size()));
        geometries.clear();
        add(left, right);
    }

    private void split() {

    }

    private void medianSplit() {

    }

    private void SAHSplit() {

    }

    private void spatialMedianSplit() {

    }

}

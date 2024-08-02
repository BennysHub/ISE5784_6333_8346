package geometries;

import primitives.Ray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 */
public class Geometries extends Intersectable {

    /**
     * A list to hold an arbitrary amount of geometric objects that implement the Intersectable interface.
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

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        List<GeoPoint> combinedIntersections = null;

        for (Intersectable intersectable : geometries) {
            var intersections = intersectable.findGeoIntersectionsHelper(ray, maxDistance);
            if (intersections != null) {
                if (combinedIntersections == null)
                    combinedIntersections = new LinkedList<>(intersections);
                else
                    combinedIntersections.addAll(intersections);
            }
        }

        return combinedIntersections;
    }
}

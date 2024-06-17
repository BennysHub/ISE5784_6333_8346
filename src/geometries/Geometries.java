package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 */
public class Geometries implements Intersectable {

    /**
     * A list to hold an arbitrary amount of geometric objects that implement the Intersectable interface.
     */
    private final List<Intersectable> geometries = null;

    /**
     * Default constructor to create an empty Geometries object.
     */
    public Geometries() {}

    /**
     * Constructor to create a Geometries object with an initial set of geometric objects.
     *
     * @param geometries Varargs of geometric objects that implement the Intersectable interface.
     */
    public Geometries(Intersectable... geometries) {
        this();
        add(geometries);
    }

    /**
     * Adds geometric objects to the collection.
     *
     * @param geometries Varargs of geometric objects that implement the Intersectable interface.
     */
    public void add(Intersectable... geometries) {
        // Implementation here
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        // Implementation here
        return List.of();
    }
}

package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a collection of geometric objects that can be intersected by a ray.
 */
public class Geometries implements Intersectable {

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
        this();
        add(geometries);
    }

    /**
     * Adds geometric objects to the collection.
     *
     * @param geometries Varargs of geometric objects that implement the Intersectable interface.
     */
    public void add(Intersectable... geometries) {
        for (Intersectable g : geometries)
            this.geometries.add(g);

    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        List<Point> intersections = new LinkedList<>();
        if (geometries == null) return null;
        for (Intersectable geometry : this.geometries) {
            List<Point> tempIntersections = geometry.findIntersections(ray);
            if (tempIntersections != null) {
                intersections.addAll(tempIntersections);
            }
        }
        return intersections.isEmpty() ? null : intersections;
    }

}

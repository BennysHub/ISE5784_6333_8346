package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;


/**
 * An interface for geometric shapes that can be intersected by a ray.
 * Implementing classes will provide the logic to find intersection points.
 */
public interface Intersectable {

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     *
     * @param ray The ray to intersect with this geometric shape.
     * @return A list of intersection points, if any, between the ray and the geometric shape.
     */
    List<Point> findIntersections(Ray ray);
}

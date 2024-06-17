package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * interface for all the geometries that intersect with the rays
 */
public interface Intersectable {
    /** *
     * calculate list of all the points on the surface of the object that intersect with the given ray
     * @param ray from the camera
     * @return list of all the points on the surface of the object that intersect with the given ray
     */
    List<Point> findIntersections(Ray ray);
}

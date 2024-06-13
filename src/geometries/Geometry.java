package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * An interface for basic geometric objects.
 * It can provide a normal vector at a given point.
 * This is a fundamental operation for geometric shapes in 3D space.
 *
 * @author Benny Avrahami
 */
public interface Geometry {

    /**
     * Calculates the normal vector to the geometry at the specified point.
     *
     * @param point The point on the geometry where the normal is to be calculated.
     * @return The normal vector at the specified point on the geometry.
     */
    Vector getNormal(Point point);
}

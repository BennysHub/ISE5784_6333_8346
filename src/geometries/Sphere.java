package geometries;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space, defined by its center point and radius.
 * Inherits the radius property from RadialGeometry.
 *
 * @author Benny Avrahami
 */
public class Sphere extends RadialGeometry {
    /**
     * The center point of the sphere.
     */
    private final Point center;

    /**
     * Constructs a Sphere with the specified radius and center.
     *
     * @param radius The radius of the sphere.
     * @param center The center point of the sphere.
     */
    public Sphere(double radius, Point center) {
        super(radius);
        this.center = center;
    }

    @Override
    public Vector getNormal(Point spherePoint) {
        // The normal to the sphere at a given point is the vector from the center to the point.
        return spherePoint.subtract(center).normalize();
    }
}

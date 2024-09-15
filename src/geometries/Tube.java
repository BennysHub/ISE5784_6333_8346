package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents a tube in 3D space, defined by its axis ray and radius.
 * Inherits the radius property from RadialGeometry.
 *
 * @author Benny Avrahami
 */
public class Tube extends RadialGeometry {
    /**
     * The axis ray of the tube, which is a line that runs through its center.
     */
    protected final Ray axis;

    /**
     * Constructs a Tube with the specified radius and axis ray.
     *
     * @param radius The radius of the tube.
     * @param axis   The axis ray of the tube.
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }

    @Override
    public Vector getNormal(Point p) {

        // The Point where the Ray of Tube Start
        Point p0 = axis.getOrigin();

        // The dir of the ray it's also normalized
        Vector dir = axis.getDirection();

        // Vector from p0 to p
        Vector pMinusP0 = p.subtract(p0);

        // projection of pMinusP0 onto dir(unit vector) we get the distance from p0 to point1 (p1)
        // p1 is the point on the ray where the vector (p1-p0) is orthogonal to vector (p-p1)
        // if projection is zero it means dir and pMinusP0 are orthogonal
        double projection = pMinusP0.dotProduct(dir);

        //p1 as mention above (create a 90-degree triangle between p, p0, p1)
        //point p1 will be equal p0 if dir and pMinusP0 are orthogonal
        Point p1 = axis.getPoint(projection);

        //normal is the vector normal at point p
        Vector normal = p.subtract(p1);

        return normal.normalize();
    }

    @Override
    void calculateAABB() {

    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        return null;
    }

}

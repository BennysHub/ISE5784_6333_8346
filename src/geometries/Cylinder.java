package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

/**
 * Represents a cylinder in 3D space, defined by its axis ray, radius, and height.
 * Inherits the radius property from RadialGeometry and extends Tube with a finite height.
 *
 * @author Benny Avrahami
 */
public class Cylinder extends Tube {
    /**
     * The height of the cylinder.
     */
    private final double height;

    /**
     * Constructs a Cylinder with the specified radius, axis ray, and height.
     *
     * @param radius The radius of the cylinder.
     * @param axis   The axis ray of the cylinder.
     * @param height The height of the cylinder.
     */
    public Cylinder(double radius, Ray axis, double height) {
        super(radius, axis);
        this.height = height;
    }

    @Override
    public Vector getNormal(Point p) {
        // The Point where the Ray of Tube Start
        Point p0 = axis.getHead();

        // The dir of the ray it's also normalized
        Vector dir = axis.getDirection();

        // If p == p0
        if (p.equals(p0))
            return dir.scale(-1);

        // Vector from p0 to p
        Vector v = p.subtract(p0);

        // projection of v onto dir(unit vector) we get the distance from p0 to Point1 (p1)
        // p1 is the point on the ray where the vector (p1-p0) is orthogonal to vector (p-p1)
        // if projection is zero it means dir and v are orthogonal
        double projection = v.dotProduct(dir);

        // If dot product between dir and v is zero meaning they orthogonal meaning the point p is in front base
        // point p is in front flat base, normal vector is the opposite direction of dir
        if (isZero(projection))
            return dir.scale(-1);

        //p1 as mention above (create a 90 degrees triangle between p, p0, p1)
        Point p1 = p0.add(dir.scale(projection));

        // If projection of vector v onto dir (unit vector) is length/size is the length of height, p is in back flat base
        // point p is in back flat base, normal vector is the dir vector.
        if (isZero(projection - height))
            return dir;

        return p.subtract(p1).normalize();
    }
}

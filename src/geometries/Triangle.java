package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.List;

/**
 * Represents a triangle in 3D space, defined by three vertices.
 * Inherits all properties and methods from the Polygon class.
 *
 * @author Benny Avrahami
 */
public class Triangle extends Polygon {

    /**
     * Constructor for Triangle that accepts three Point objects.
     *
     * @param p1 The first vertex of the triangle.
     * @param p2 The second vertex of the triangle.
     * @param p3 The third vertex of the triangle.
     */
    public Triangle(Point p1, Point p2, Point p3) {
        // Call the constructor of the superclass Polygon with exactly three points
        super(p1, p2, p3);
    }

    @Override
    public List<Point> findIntersections(Ray ray) {
        return null;
    }
}

package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Util;
import primitives.Vector;

import java.util.List;

import static primitives.Util.isZero;

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
        // Extract relevant information from the input
        Vector rayDirection = ray.getDirection(); // Direction vector of the ray
        Point rayOrigin = ray.getHead(); // Origin point of the ray
        Point vertex0 = vertices.getFirst(); // First vertex of the triangle

        // Check if the ray direction is parallel to the triangle's plane
        if (isZero(this.plane.getNormal().dotProduct(rayDirection)) || rayOrigin.equals(vertex0)) {
            // If parallel or starting from the vertex, no intersection
            return null;
        }

        // Retrieve the other two vertices of the triangle
        Point vertex1 = vertices.get(1);
        Point vertex2 = vertices.get(2);

        // Compute edge vectors
        Vector edge1 = vertex1.subtract(vertex0); // Edge from vertex0 to vertex1
        Vector edge2 = vertex2.subtract(vertex0); // Edge from vertex0 to vertex2

        // Compute the cross product of ray direction and edge2
        Vector rayCrossEdge2 = rayDirection.crossProduct(edge2);

        // Calculate the determinant (used for inverse determinant later)
        double det = rayCrossEdge2.dotProduct(edge1);
        double inv_det = 1d / det; // Inverse determinant

        // Compute vector 's' from ray origin to vertex0
        Vector s = rayOrigin.subtract(vertex0);

        // Calculate barycentric coordinate 'u'
        double u = inv_det * s.dotProduct(rayCrossEdge2);

        // Check if 'u' is within valid range (0, 1)
        if (Util.alignZero(u) <= 0 || Util.alignZero(u - 1d) >= 0) {
            // Outside valid range, no intersection
            return null;
        }

        // Compute vector 'q' (cross product of 's' and edge1)
        Vector q = s.crossProduct(edge1);

        // Calculate barycentric coordinate 'v'
        double v = inv_det * rayDirection.dotProduct(q);

        // Check if 'v' is within valid range (0, 1) and u + v < 1
        if (Util.alignZero(v) <= 0 || Util.alignZero(u + v - 1d) >= 0) {
            // Outside valid range, no intersection
            return null;
        }

        // Compute parameter 't' to find the intersection point on the line
        double t = inv_det * edge2.dotProduct(q);

        if (Util.alignZero(t) > 0) {
            // Ray intersection: Compute the actual intersection point
            return List.of(ray.getPoint(t));
        } else {
            // Line intersection but not a ray intersection
            return null;
        }
    }

}

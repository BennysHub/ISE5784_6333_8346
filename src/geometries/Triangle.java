package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
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
    protected void calculateAABBHelper() {
        Point p1 = vertices.getFirst();
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);
        double minX = Math.min(p1.getX(), Math.min(p2.getX(), p3.getX()));
        double minY = Math.min(p1.getY(), Math.min(p2.getY(), p3.getY()));
        double minZ = Math.min(p1.getZ(), Math.min(p2.getZ(), p3.getZ()));

        double maxX = Math.max(p1.getX(), Math.max(p2.getX(), p3.getX()));
        double maxY = Math.max(p1.getY(), Math.max(p2.getY(), p3.getY()));
        double maxZ = Math.max(p1.getZ(), Math.max(p2.getZ(), p3.getZ()));

        Point min = new Point(minX, minY, minZ);
        Point max = new Point(maxX, maxY, maxZ);
        aabb = new AABB(min, max);
    }


    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        // Extract relevant information from the input
        Vector rayDirection = ray.getDirection(); // Direction vector of the ray
        Point rayOrigin = ray.getOrigin(); // Origin point of the ray
        Point vertex0 = vertices.getFirst(); // First vertex of the triangle

        // Check if the ray direction is parallel to the triangle's plane
        if (isZero(this.plane.getNormal().dotProduct(rayDirection)) || rayOrigin.equals(vertex0))
            // If parallel or starting from the vertex, no intersection
            return null;

        // Retrieve the other two vertices of the triangle
        Point vertex1 = vertices.get(1);
        Point vertex2 = vertices.get(2);

        // Compute-edge vectors
        Vector edge1 = vertex1.subtract(vertex0); // Edge from vertex0 to vertex1
        Vector edge2 = vertex2.subtract(vertex0); // Edge from vertex0 to vertex2

        // Compute the cross-product of a ray direction and edge2
        Vector rayCrossEdge2 = rayDirection.crossProduct(edge2);

        // Calculate the determinant (used for inverse determinant later)
        double det = rayCrossEdge2.dotProduct(edge1);
        double invDet = 1d / det; // Inverse determinant

        // Compute vector 's' from ray origin to vertex0
        Vector s = rayOrigin.subtract(vertex0);
        // Calculate barycentric coordinate 'u'
        double u = invDet * s.dotProduct(rayCrossEdge2);

        // Check if 'u' is within valid range (0, 1)
        if (alignZero(u) <= 0 || alignZero(u - 1d) >= 0)
            // Outside valid range, no intersection
            return null;

        // Compute vector 'q' (cross product of 's' and edge1)
        Vector q = s.crossProduct(edge1);//TODO possible vector zero?
        // Calculate barycentric coordinate 'v'
        double v = invDet * rayDirection.dotProduct(q);

        // Check if 'v' is within valid range (0, 1) and u + v < 1
        if (alignZero(v) <= 0 || alignZero(u + v - 1d) >= 0)
            // Outside valid range, no intersection
            return null;

        // Compute parameter "t" to find the intersection point on the line
        double t = invDet * edge2.dotProduct(q);
        return alignZero(t) > 0 && alignZero(t - maxDistance) < 0
                // Ray intersection: Compute the actual intersection point
                ? List.of(new GeoPoint(this, ray.getPoint(t)))
                // Line intersection but not a ray intersection
                : null;
    }
}

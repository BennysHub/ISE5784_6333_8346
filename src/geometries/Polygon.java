package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.Arrays;
import java.util.List;

import static primitives.Util.alignZero;

/**
 * Polygon class represents a two-dimensional polygon in a 3D Cartesian coordinate system.
 * The polygon is defined by its vertices, which must be coplanar and ordered in a convex shape.
 *
 * <p>This class also associates the polygon with a plane in which it lies, providing fast normal vector retrieval.</p>
 *
 * @author Dan
 */
public class Polygon extends Geometry {
    /**
     * List of the polygon's vertices.
     */
    protected final Point[] polygonVertices;

    /**
     * The plane in which the polygon lies.
     */
    protected final Plane polygonPlane;

    /**
     * The number of vertices in the polygon.
     */
    private final int vertexCount;

    /**
     * Constructs a polygon from a list of vertices. The vertices must:
     * <ul>
     *     <li>Be ordered sequentially along the edge path.</li>
     *     <li>Lie in the same plane.</li>
     *     <li>Form a convex shape (no internal angles greater than 180°).</li>
     * </ul>
     *
     * @param vertices The list of vertices defining the polygon.
     * @throws IllegalArgumentException if:
     *                                  <ul>
     *                                      <li>Less than three vertices are provided.</li>
     *                                      <li>Two consecutive vertices are identical.</li>
     *                                      <li>Vertices are not coplanar.</li>
     *                                      <li>The vertex order does not form a convex polygon.</li>
     *                                  </ul>
     */
    public Polygon(Point... vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("A polygon must have at least three vertices.");
        }

        this.polygonVertices = vertices;
        this.vertexCount = vertices.length;

        // Generate the polygon's plane using the first three vertices
        this.polygonPlane = new Plane(vertices[0], vertices[1], vertices[2]);
        if (vertexCount == 3) {
            // A triangle requires no further checks
            return;
        }

        Vector polygonNormal = polygonPlane.getNormal();
        Vector previousEdge = vertices[vertices.length - 1].subtract(vertices[vertices.length - 2]);
        Vector currentEdge = vertices[0].subtract(vertices[vertices.length - 1]);

        // Determine if the polygon is convex by checking edge orientation
        boolean isConvexDirectionPositive = previousEdge.crossProduct(currentEdge).dotProduct(polygonNormal) > 0;

        for (int i = 1; i < vertices.length; i++) {
            // Ensure all vertices lie on the same plane
            if (!vertices[i].subtract(vertices[0]).isPerpendicular(polygonNormal)) {
                throw new IllegalArgumentException("All vertices of a polygon must lie in the same plane.");
            }

            // Check edge orientation for convexity
            previousEdge = currentEdge;
            currentEdge = vertices[i].subtract(vertices[i - 1]);
            if (isConvexDirectionPositive != (previousEdge.crossProduct(currentEdge).dotProduct(polygonNormal) > 0)) {
                throw new IllegalArgumentException("The polygon must be convex and the vertices must be ordered.");
            }
        }
    }

    @Override
    public Vector getNormal(Point point) {
        // The normal of the polygon is the same as the normal of its associated plane
        return polygonPlane.getNormal();
    }

    @Override
    protected Geometry translateHelper(Vector translationVector) {
        return new Polygon(Arrays.stream(polygonVertices).map(point -> point.add(translationVector)).toArray(Point[]::new));
    }

    @Override
    protected Geometry rotateHelper(Vector axis, double angleInRadians) {
        Quaternion rotation = Quaternion.fromAxisAngle(axis, angleInRadians);
        return new Polygon(Arrays.stream(polygonVertices).map(point ->  rotation.rotate(point.toVector())).toArray(Point[]::new));
    }

    @Override
    protected Geometry scaleHelper(Vector scale) {
        return new Polygon(Arrays.stream(polygonVertices).map(point -> point.scale(scale)).toArray(Point[]::new));
    }

    @Override
    protected void calculateAABBHelper() {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;

        for (Point vertex : polygonVertices) {
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            minZ = Math.min(minZ, vertex.getZ());

            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
            maxZ = Math.max(maxZ, vertex.getZ());
        }

        // Create the AABB using the computed minimum and maximum points
        Point min = new Point(minX, minY, minZ);
        Point max = new Point(maxX, maxY, maxZ);
        aabb = new AABB(min, max);
    }


    /**
     * Finds the intersections of a ray with the polygon.
     * <p>This implementation works as follows:</p>
     * <ol>
     *     <li>Check if the ray intersects the polygon's plane.</li>
     *     <li>If it does, perform a point-in-polygon test to ensure the intersection
     *     lies within the polygon's boundaries.</li>
     * </ol>
     *
     * @param ray         The ray to test for intersections.
     * @param maxDistance The maximum distance for valid intersections.
     * @return A list of intersection points (if any), or {@code null} if none exist.
     */
    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        // Step 1: Check for intersection with the plane
        List<GeoPoint> planeIntersections = polygonPlane.findGeoIntersections(ray, maxDistance);
        if (planeIntersections == null) {
            return null; // No intersection with the plane
        }

        // Step 2: Perform a point-in-polygon test
        Point intersectionPoint = planeIntersections.getFirst().point();
        Vector normal = polygonPlane.getNormal();

        // Loop through the edges of the polygon and test if the point is "inside"
        for (int i = 0; i < vertexCount; i++) {
            Point v0 = polygonVertices[i];
            Point v1 = polygonVertices[(i + 1) % vertexCount];

            Vector edge = v1.subtract(v0);
            Vector toIntersection = intersectionPoint.subtract(v0);

            // Cross product to determine if the point lies "inside" the edge
            double sign = alignZero(edge.crossProduct(toIntersection).dotProduct(normal));
            if (sign < 0) {
                return null; // The point lies outside the polygon
            }
        }

        // If all edges pass, the point lies inside the polygon
        return List.of(new GeoPoint(this, intersectionPoint));
    }

}

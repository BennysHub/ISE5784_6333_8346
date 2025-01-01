package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents a triangle in 3D space, defined by three vertices.
 * Inherits properties and methods from the {@link Polygon} class,
 * providing additional implementations for geometry-specific operations
 * like translation, rotation, and scaling.
 *
 * <p>The triangle is a fundamental geometry and is often used
 * in rendering pipelines, collision detection, and 3D modeling.</p>
 *
 * @author Benny Avrahami
 */
public class Triangle extends Polygon {

    /**
     * Constructs a Triangle with the specified three vertices.
     *
     * @param vertex1 The first vertex of the triangle.
     * @param vertex2 The second vertex of the triangle.
     * @param vertex3 The third vertex of the triangle.
     */
    public Triangle(Point vertex1, Point vertex2, Point vertex3) {
        super(vertex1, vertex2, vertex3);
    }

    //edges of triangle use for findGeoIntersection


    private final Point p1 = polygonVertices[0];
    private final Point p2 = polygonVertices[1];
    private final Point p3 = polygonVertices[2];

    private final Vector edge1 = p2.subtract(p1);
    private final Vector edge2 = p3.subtract(p1);
    private final Vector edge3 = p3.subtract(p2);

    /**
     * Calculates the Axis-Aligned Bounding Box (AABB) for the triangle.
     * The AABB is a box that tightly bounds the triangle.
     */
    @Override
    protected void calculateAABBHelper() {
        Point v0 = polygonVertices[0];
        Point v1 = polygonVertices[1];
        Point v2 = polygonVertices[2];

        // Compute minimum and maximum bounds along each axis
        double minX = Math.min(v0.getX(), Math.min(v1.getX(), v2.getX()));
        double minY = Math.min(v0.getY(), Math.min(v1.getY(), v2.getY()));
        double minZ = Math.min(v0.getZ(), Math.min(v1.getZ(), v2.getZ()));

        double maxX = Math.max(v0.getX(), Math.max(v1.getX(), v2.getX()));
        double maxY = Math.max(v0.getY(), Math.max(v1.getY(), v2.getY()));
        double maxZ = Math.max(v0.getZ(), Math.max(v1.getZ(), v2.getZ()));

        // Create the AABB using min and max points
        Point min = new Point(minX, minY, minZ);
        Point max = new Point(maxX, maxY, maxZ);
        aabb = new AABB(min, max);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Vector direction = ray.getDirection();
        Point origin = ray.getOrigin();

        Vector directionCrossEdge02 = direction.crossProduct(edge2);
        double det = edge1.dotProduct(directionCrossEdge02);

        if (det == 0) return null; // Ray is parallel to the triangle
        double invDet = 1d / det;

        // Compute barycentric coordinates
        Vector originToV0 = origin.subtract(polygonVertices[0]);
        double u = invDet * originToV0.dotProduct(directionCrossEdge02);
        if (u <= 0 || u - 1d >= 0) return null;

        Vector originToV0CrossEdge01 = originToV0.crossProduct(edge1);
        double v = invDet * direction.dotProduct(originToV0CrossEdge01);
        if (v <= 0 || u + v - 1d >= 0) return null;

        // Compute t parameter
        double t = invDet * edge2.dotProduct(originToV0CrossEdge01);

        // Check if t is within the valid range
        return t > 0 && t - maxDistance < 0
                ? List.of(new GeoPoint(this, ray.getPoint(t)))
                : null;
    }

    @Override
    protected Triangle translateHelper(Vector translationVector) {
        return new Triangle(
                polygonVertices[0].add(translationVector),
                polygonVertices[1].add(translationVector),
                polygonVertices[2].add(translationVector)
        );
    }

    @Override
    protected Triangle rotateHelper(Quaternion rotation) {
        return new Triangle(
                rotation.rotate(polygonVertices[0]),
                rotation.rotate(polygonVertices[1]),
                rotation.rotate(polygonVertices[2])
        );
    }

    @Override
    protected Triangle scaleHelper(Vector scaleVector) {
        return new Triangle(
                polygonVertices[0].scale(scaleVector),
                polygonVertices[1].scale(scaleVector),
                polygonVertices[2].scale(scaleVector)
        );
    }


    @Override
    public double signedDistance(Point point) {

        // Compute normal of the triangle's plane
        Vector normal = polygonPlane.getNormal();

        // Signed distance to the triangle's plane
       // double signedPlaneDistance = normal.dotProduct(point.subtract(p1));
        double signedPlaneDistance = polygonPlane.signedDistance(point);

        // Project the point onto the triangle's plane
        Point projectedPoint = point.subtract(normal.scale(signedPlaneDistance));

        // Barycentric coordinates to determine if the point is inside the triangle
        Vector v0 = edge1;
        Vector v1 = edge2;
        Vector v2 = projectedPoint.subtract(p1);

        double d00 = v0.dotProduct(v0);
        double d01 = v0.dotProduct(v1);
        double d11 = v1.dotProduct(v1);
        double d20 = v2.dotProduct(v0);
        double d21 = v2.dotProduct(v1);

        double denom = d00 * d11 - d01 * d01;
        double a = (d11 * d20 - d01 * d21) / denom;
        double b = (d00 * d21 - d01 * d20) / denom;
        double c = 1.0 - a - b;

        // If the point is inside the triangle
        if (a >= 0 && b >= 0 && c >= 0) {
            return Math.abs(signedPlaneDistance); // Closest to the surface
        }

        // If outside, calculate distances to edges and vertices
        double dist0 = distanceToEdge(point, p1, edge1);
        double dist1 = distanceToEdge(point, p1, edge2);
        double dist2 = distanceToEdge(point, p2, edge3);

        return Math.min(Math.min(dist0, dist1), dist2);
    }

    /**
     * Helper method to calculate the closest distance to an edge.
     *
     * @param point     The point to check.
     * @param edgeStart The starting point of the edge.
     * @param edge      The edge vector.
     * @return The closest distance to the edge.
     */
    private double distanceToEdge(Point point, Point edgeStart, Vector edge) {
        Vector toPoint = point.subtract(edgeStart);
        double t = Math.max(0, Math.min(1, toPoint.dotProduct(edge) / edge.lengthSquared()));
        Point closestPoint = edgeStart.add(edge.scale(t));
        return closestPoint.distance(point);
    }


    public double signedDistance1(Point point) {
        // Calculate edges


        // Calculate normal
        Vector normal = edge2.crossProduct(edge3).normalize();

        // Calculate signed distance to the plane
        Vector pointVec = point.subtract(p1);
        double d = pointVec.dotProduct(normal);

        // Project point onto triangle plane
        Point projectedPoint = point.subtract(normal.scale(d));

        // Check if projected point is inside the triangle using barycentric coordinates
        if (isInsideTriangle(projectedPoint)) {
            return d;
        } else {
            // Compute distance to the closest edge or vertex
            return Math.sqrt(closestEdgeDistanceSquared(point));
        }
    }

    private boolean isInsideTriangle(Point point) {
        Vector v0 = p3.subtract(p1);
        Vector v1 = p2.subtract(p1);
        Vector v2 = point.subtract(p1);

        double dot00 = v0.dotProduct(v0);
        double dot01 = v0.dotProduct(v1);
        double dot02 = v0.dotProduct(v2);
        double dot11 = v1.dotProduct(v1);
        double dot12 = v2.dotProduct(v2);

        double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
        double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

        // Point is inside the triangle if u, v are >= 0 and u + v <= 1
        return (u >= 0) && (v >= 0) && (u + v <= 1);
    }

    private double closestEdgeDistanceSquared(Point point) {
        // Calculate squared distance to each edge and return the minimum squared distance
        double distance1 = distanceToSegmentSquared(point, p1, p2);
        double distance2 = distanceToSegmentSquared(point, p2, p3);
        double distance3 = distanceToSegmentSquared(point, p3, p1);
        return Math.min(distance1, Math.min(distance2, distance3));
    }

    private double distanceToSegmentSquared(Point p, Point v, Point w) {
        Vector pv = p.subtract(v);
        Vector wv = w.subtract(v);

        double t = Math.max(0, Math.min(1, pv.dotProduct(wv) / wv.dotProduct(wv)));
        Point projection = v.add(wv.scale(t));

        Vector projectionVec = p.subtract(projection);
        return projectionVec.lengthSquared();
    }


}

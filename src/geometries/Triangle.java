package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

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

    /**
     * Finds the intersections of a given ray with this triangle.
     *
     * @param ray         The ray to intersect with the triangle.
     * @param maxDistance The maximum distance for valid intersections.
     * @return A list of intersection points (if any), or {@code null} if no intersections exist.
     */
    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Vector direction = ray.getDirection();
        Point origin = ray.getOrigin();

        Point v0 = polygonVertices[0];
        Point v1 = polygonVertices[1];
        Point v2 = polygonVertices[2];

        // Check if the ray is parallel to the triangle's plane
        if (isZero(polygonPlane.getNormal().dotProduct(direction)) || origin.equals(v0)) {
            return null;
        }

        // Compute edges and determinant
        Vector edge01 = v1.subtract(v0);
        Vector edge02 = v2.subtract(v0);
        Vector directionCrossEdge02 = direction.crossProduct(edge02);
        double det = edge01.dotProduct(directionCrossEdge02);

        if (isZero(det)) return null; // Ray is parallel to the triangle
        double invDet = 1d / det;

        // Compute barycentric coordinates
        Vector originToV0 = origin.subtract(v0);
        double u = invDet * originToV0.dotProduct(directionCrossEdge02);
        if (alignZero(u) <= 0 || alignZero(u - 1d) >= 0) return null;

        Vector originToV0CrossEdge01 = originToV0.crossProduct(edge01);
        double v = invDet * direction.dotProduct(originToV0CrossEdge01);
        if (alignZero(v) <= 0 || alignZero(u + v - 1d) >= 0) return null;

        // Compute t parameter
        double t = invDet * edge02.dotProduct(originToV0CrossEdge01);

        // Check if t is within the valid range
        return alignZero(t) > 0 && alignZero(t - maxDistance) < 0
                ? List.of(new GeoPoint(this, ray.getPoint(t)))
                : null;
    }


    /**
     * Translates the triangle by a given translation vector.
     *
     * @param translationVector The vector by which to translate the triangle.
     * @return A new {@code Triangle} instance representing the translated triangle.
     */
    @Override
    protected Geometry translateHelper(Vector translationVector) {
        return new Triangle(
                polygonVertices[0].add(translationVector),
                polygonVertices[1].add(translationVector),
                polygonVertices[2].add(translationVector)
        );
    }

    /**
     * Rotates the triangle around a specified axis by a given angle.
     *
     * @param axis             The axis of rotation.
     * @param angleInRadians   The rotation angle in radians.
     * @return A new {@code Triangle} instance representing the rotated triangle.
     */
    @Override
    protected Geometry rotateHelper(Vector axis, double angleInRadians) {
        Quaternion rotation = Quaternion.fromAxisAngle(axis, angleInRadians);

        return new Triangle(
                rotation.rotate(polygonVertices[0].toVector()),
                rotation.rotate(polygonVertices[1].toVector()),
                rotation.rotate(polygonVertices[2].toVector())
        );
    }

    /**
     * Scales the triangle by a given scale vector.
     *
     * @param scale The scale vector containing scaling factors for each axis.
     * @return A new {@code Triangle} instance representing the scaled triangle.
     */
    @Override
    protected Geometry scaleHelper(Vector scale) {
        return new Triangle(
                polygonVertices[0].scale(scale),
                polygonVertices[1].scale(scale),
                polygonVertices[2].scale(scale)
        );
    }
}

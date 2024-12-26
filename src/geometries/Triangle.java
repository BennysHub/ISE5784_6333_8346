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
        edge1 = polygonVertices[1].subtract(polygonVertices[0]);
        edge2 = polygonVertices[2].subtract(polygonVertices[0]);
    }

    //edges of triangle use for findGeoIntersection
    private final Vector edge1;
    private final Vector edge2;


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


        // Check if the ray is parallel to the triangle's plane
        if (origin.equals(polygonVertices[0])) {// TODO: vector zero case
            return null;
        }


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


}

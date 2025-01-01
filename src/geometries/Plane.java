package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;
import static utils.Util.isZero;

/**
 * Represents a plane in 3D space, defined by a point on the plane and a normal vector.
 * The plane is assumed to be infinite.
 *
 * <p>Common uses include defining infinite surfaces and aiding in intersection calculations.</p>
 *
 * @author Benny Avrahami
 */
public class Plane extends Geometry {
    /**
     * A reference point on the plane.
     */
    private final Point referencePoint;

    /**
     * The normalized vector perpendicular to the plane.
     */
    private final Vector normalVector;

    /**
     * Constructs a Plane through three non-collinear points in space.
     *
     * <p>The order of points determines the direction of the normal vector.</p>
     *
     * @param p1 The first point on the plane.
     * @param p2 The second point on the plane.
     * @param p3 The third point on the plane.
     * @throws IllegalArgumentException If the points are collinear or not distinct.
     */
    public Plane(Point p1, Point p2, Point p3) {
        Vector edge1 = p2.subtract(p1);// TODO: vector zero case
        Vector edge2 = p3.subtract(p1);// TODO: vector zero case

        // Cross-product of edges to compute the normal vector
        Vector normalCandidate = edge1.crossProduct(edge2);

        if (normalCandidate.equals(Vector.ZERO))
            throw new IllegalArgumentException("Constructing a plane with 3 points, The points must not be collinear.");



        this.referencePoint = p1;
        this.normalVector = normalCandidate.normalize();
    }

    /**
     * Constructs a Plane with a point on the plane and a normal vector.
     *
     * @param referencePoint A point on the plane.
     * @param normalVector   The normal vector of the plane. It will be normalized automatically.
     */
    public Plane(Point referencePoint, Vector normalVector) {
        this.referencePoint = referencePoint;
        this.normalVector = normalVector.normalize();
    }

    /**
     * Gets the normalized normal vector of the plane.
     *
     * @return The normalized normal vector of the plane.
     */
    public Vector getNormal() {
        return normalVector;
    }


    @Override
    public Vector getNormal(Point point) {
        // The normal is the same everywhere on an infinite plane.
        return getNormal();
    }

    @Override
    protected Plane rotateHelper(Quaternion rotation ) {
        return new Plane(rotation.rotate(referencePoint),  rotation.rotate(normalVector));
    }

    @Override
    protected Plane scaleHelper(Vector scaleVector) {
        return this;
    }

    @Override
    protected Plane translateHelper(Vector translationVector) {
        return new Plane(referencePoint.add(translationVector), normalVector);
    }

    @Override
    public double signedDistance(Point point) {
        return normalVector.dotProduct(point.subtract(referencePoint));
    }



    @Override
    protected void calculateAABBHelper() {
        aabb = new AABB(); // Infinite planes have no bounding box
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Point rayOrigin = ray.getOrigin();
        Vector rayDirection = ray.getDirection();

        // Check if the ray is parallel to the plane
        double normalDotDirection = normalVector.dotProduct(rayDirection);
        if (normalDotDirection == 0) {
            return null; // Ray is parallel to the plane
        }

        // Compute the t parameter for the intersection point
        double t = normalVector.dotProduct(referencePoint.subtract(rayOrigin)) / normalDotDirection;// TODO: vector zero case

        // Check if the intersection point lies beyond the ray's origin or within maxDistance
        return t > 0 && t - maxDistance < 0
                ? List.of(new GeoPoint(this, ray.getPoint(t)))
                : null;
    }
}

package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.Arrays;
import java.util.Collection;

/**
 * Represents an Oriented Bounding Box (OOB), which is a rectangular cuboid in 3D space
 * that can have an arbitrary orientation.
 *
 * <p>Unlike an Axis-Aligned Bounding Box (AABB), an OOB is defined by its center, dimensions, and orientation.
 * It provides methods for transformation, merging, and intersection testing.</p>
 *
 * @author Benny Avrahami
 */
public class OOB implements Transformable {

    /**
     * The center of the OOB.
     */
    private Point center;

    /**
     * The dimensions of the OOB along its local axes.
     */
    private Vector halfDimensions;

    /**
     * The orientation of the OOB represented as three orthogonal unit vectors.
     * These vectors define the local axes of the OOB.
     */
    private Vector[] axes;

    /**
     * Default constructor to initialize an empty OOB with extreme values.
     */
    public OOB() {
        this.center = Point.ZERO;
        this.halfDimensions = new Vector(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.axes = new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z};
    }

    /**
     * Constructs an OOB with the specified center, dimensions, and orientation.
     *
     * @param center         The center of the OOB.
     * @param halfDimensions Half the dimensions of the OOB along its local axes.
     * @param axes           The local axes defining the orientation of the OOB.
     */
    public OOB(Point center, Vector halfDimensions, Vector[] axes) {
        this.center = center;
        this.halfDimensions = halfDimensions;
        this.axes = axes;
    }

    /**
     * Constructs an OOB from a collection of geometries by computing a tight-fitting box.
     *
     * @param geometries The geometries to enclose in the OOB.
     */
    public OOB(Collection<Geometry> geometries) {
        AABB aabb = new AABB(geometries); // Compute AABB first
        this.center = aabb.getCenter();
        this.halfDimensions = new Vector(
                (aabb.getMax().getX() - aabb.getMin().getX()) / 2,
                (aabb.getMax().getY() - aabb.getMin().getY()) / 2,
                (aabb.getMax().getZ() - aabb.getMin().getZ()) / 2
        );
        this.axes = new Vector[]{Vector.UNIT_X, Vector.UNIT_Y, Vector.UNIT_Z};
    }

    /**
     * Expands the OOB to include a given point.
     *
     * @param point The point to include.
     */
    public void expand(Point point) {
        // Transform the point into the OOB's local space
        Vector localPoint = point.subtract(center);
        double[] localCoords = new double[3];
        for (int i = 0; i < 3; i++) {
            localCoords[i] = localPoint.dotProduct(axes[i]);
        }

        // Update the half-dimensions to encompass the point
        for (int i = 0; i < 3; i++) {
            halfDimensions = halfDimensions.setCoordinate(i,
                    Math.max(halfDimensions.getCoordinate(i), Math.abs(localCoords[i]))
            );
        }
    }

    /**
     * Checks if a ray intersects with this OOB.
     *
     * @param ray The ray to check for intersection.
     * @return {@code true} if the ray intersects the OOB, {@code false} otherwise.
     */
    public boolean intersects(Ray ray) {
        Vector rayDir = ray.getDirection();
        Vector rayToBox = center.subtract(ray.getOrigin());

        double tMin = Double.NEGATIVE_INFINITY, tMax = Double.POSITIVE_INFINITY;

        for (int i = 0; i < 3; i++) {
            double axisProjection = rayDir.dotProduct(axes[i]);
            double boxProjection = rayToBox.dotProduct(axes[i]);

            if (Math.abs(axisProjection) > 1e-6) {
                double t1 = (boxProjection - halfDimensions.getCoordinate(i)) / axisProjection;
                double t2 = (boxProjection + halfDimensions.getCoordinate(i)) / axisProjection;

                if (t1 > t2) {
                    double temp = t1;
                    t1 = t2;
                    t2 = temp;
                }

                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);

                if (tMin > tMax) return false;
            } else if (Math.abs(boxProjection) > halfDimensions.getCoordinate(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public OOB translate(Vector translationVector) {
        return new OOB(center.add(translationVector), halfDimensions, axes);
    }

    @Override
    public OOB rotate(Vector axis, double angleInRadians) {
        Quaternion rotation = Quaternion.fromAxisAngle(axis, angleInRadians);
        return rotate(rotation);
    }

    @Override
    public OOB rotate(Quaternion rotation) {
        Vector[] rotatedAxes = new Vector[3];
        for (int i = 0; i < 3; i++) {
            rotatedAxes[i] = rotation.rotate(axes[i]);
        }
        return new OOB(center, halfDimensions, rotatedAxes);
    }

    @Override
    public OOB scale(Vector scale) {
        return new OOB(center, halfDimensions.elementWiseMultiply(scale), axes);
    }

    /**
     * Returns the surface area of the OOB.
     *
     * @return The surface area.
     */
    public double surfaceArea() {
        return 2 * (
                halfDimensions.getX() * halfDimensions.getY() +
                        halfDimensions.getY() * halfDimensions.getZ() +
                        halfDimensions.getZ() * halfDimensions.getX()
        );
    }

    @Override
    public String toString() {
        return "OOB [center=" + center + ", halfDimensions=" + halfDimensions + ", axes=" + Arrays.toString(axes) + "]";
    }

    public Point getCenterPoint() {
        return center;
    }

    public Object[] getAxes() {
        return axes;
    }

    public Vector getHalfDimensions() {
        return halfDimensions;
    }
}

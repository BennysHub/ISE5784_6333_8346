package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.Collection;
import java.util.List;

/**
 * Represents an Axis-Aligned Bounding Box (AABB) used for bounding volumes in geometric computations.
 * <p>
 * An AABB is a rectangular cuboid aligned with the coordinate axes.
 * It is commonly used in
 * 3D graphics and physics engines to optimize intersection tests and spatial partitioning.
 * </p>
 *
 * <p>The AABB can be dynamically expanded or merged with other AABBs and supports intersection tests
 * with both rays and other AABBs.</p>
 *
 * @author Benny Avrahami
 */
public class AABB  {

    /**
     * The minimum corner of the AABB, representing the smallest x, y, and z coordinates.
     */
    private Point min;

    /**
     * The maximum corner of the AABB, representing the largest x, y, and z coordinates.
     */
    private Point max;

    /**
     * The center of the AABB, calculated lazily and stored for optimization.
     */
    private double[] center;

    /**
     * Indicates whether the center value is valid and up to date.
     */
    private boolean isCenterValid = false;

    /**
     * Default constructor to initialize an empty AABB with extreme values.
     * <p>
     * The {@code min} point is set to positive infinity, and the {@code max} point is set to negative infinity.
     * This allows later operations (e.g., {@link #expand(Point)}) to update the bounds correctly.
     * </p>
     */
    public AABB() {
        this.min = Point.POSITIVE_INFINITY;
        this.max = Point.NEGATIVE_INFINITY;
    }

    /**
     * Constructor to initialize the AABB with specific minimum and maximum points.
     *
     * @param min The minimum point of the AABB.
     * @param max The maximum point of the AABB.
     */
    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
        this.center = getCenter(); // Precompute the center
        this.isCenterValid = true;
    }

    /**
     * Copy constructor to initialize a new AABB as a copy of another AABB, merged with an additional AABB.
     *
     * @param other1 The first AABB to copy.
     * @param other2 The second AABB to merge with the first.
     */
    public AABB(AABB other1, AABB other2) {
        this.min = other1.min;
        this.max = other1.max;
        this.merge(other2);
    }


    public AABB(Collection<Geometry> geometries) {
        this(); // Initialize to an empty AABB
        for (Geometry geometry : geometries) {
            geometry.calculateAABB();
            this.merge(geometry.aabb);
        }
    }


    /**
     * Expands the AABB to include a given point.
     *
     * @param point The point to include in the AABB.
     */
    public void expand(Point point) {
        min = new Point(
                Math.min(min.getX(), point.getX()),
                Math.min(min.getY(), point.getY()),
                Math.min(min.getZ(), point.getZ())
        );
        max = new Point(
                Math.max(max.getX(), point.getX()),
                Math.max(max.getY(), point.getY()),
                Math.max(max.getZ(), point.getZ())
        );
    }

    /**
     * Merges this AABB with another AABB by expanding its bounds to encompass the other AABB.
     *
     * @param other The AABB to merge with this one.
     */
    public void merge(AABB other) {
        this.expand(other.min);
        this.expand(other.max);
    }

    /**
     * Calculates the surface area of the AABB.
     *
     * @return The surface area of the AABB.
     */
    public double surfaceArea() {
        double dx = max.getX() - min.getX();
        double dy = max.getY() - min.getY();
        double dz = max.getZ() - min.getZ();
        return 2 * (dx * dy + dy * dz + dz * dx);
    }

    /**
     * Checks if this AABB intersects with another AABB.
     *
     * @param other The other AABB to check for intersection.
     * @return {@code true} if the AABBs intersect, {@code false} otherwise.
     */
    public boolean intersects(AABB other) {
        return (this.max.getX() >= other.min.getX() && this.min.getX() <= other.max.getX()) &&
                (this.max.getY() >= other.min.getY() && this.min.getY() <= other.max.getY()) &&
                (this.max.getZ() >= other.min.getZ() && this.min.getZ() <= other.max.getZ());
    }

    /**
     * Returns the center of the bounding box.
     * <p>
     * The center is computed lazily and cached for optimization.
     * If the AABB is updated
     * (e.g., by {@link #expand(Point)}), the center must be recomputed.
     * </p>
     *
     * @return An array containing the x, y, and z coordinates of the AABB center.
     */
    public double[] getCenter() {
        if (isCenterValid) {
            return center;
        }
        double centerX = (min.getX() + max.getX()) / 2;
        double centerY = (min.getY() + max.getY()) / 2;
        double centerZ = (min.getZ() + max.getZ()) / 2;
        this.center = new double[]{centerX, centerY, centerZ};
        this.isCenterValid = true;
        return center;
    }

    public Point getCenterPoint() {
        double[] center = getCenter();
        return new Point(center[0], center[1], center[2]);
    }


    /**
     * Gets the minimum point of the AABB.
     *
     * @return The minimum point of the AABB.
     */
    public Point getMin() {
        return min;
    }

    /**
     * Gets the maximum point of the AABB.
     *
     * @return The maximum point of the AABB.
     */
    public Point getMax() {
        return max;
    }

    /**
     * Checks if a ray intersects with this AABB.
     *
     * @param ray The ray to check for intersection with the AABB.
     * @return {@code true} if the ray intersects the AABB, {@code false} otherwise.
     */
    public boolean intersects(Ray ray) {
        Point rayOrigin = ray.getOrigin();
        Point rayDir = ray.getDirection();

        double tMin = (min.getX() - rayOrigin.getX()) / rayDir.getX();
        double tMax = (max.getX() - rayOrigin.getX()) / rayDir.getX();
        if (tMin > tMax) {
            double temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        double tYMin = (min.getY() - rayOrigin.getY()) / rayDir.getY();
        double tYMax = (max.getY() - rayOrigin.getY()) / rayDir.getY();
        if (tYMin > tYMax) {
            double temp = tYMin;
            tYMin = tYMax;
            tYMax = temp;
        }

        if ((tMin > tYMax) || (tYMin > tMax))
            return false;

        if (tYMin > tMin)
            tMin = tYMin;
        if (tYMax < tMax)
            tMax = tYMax;

        double tZMin = (min.getZ() - rayOrigin.getZ()) / rayDir.getZ();
        double tZMax = (max.getZ() - rayOrigin.getZ()) / rayDir.getZ();
        if (tZMin > tZMax) {
            double temp = tZMin;
            tZMin = tZMax;
            tZMax = temp;
        }
        return !((tMin > tZMax) || (tZMin > tMax));
    }

    /**
     * Returns a string representation of the AABB for debugging.
     *
     * @return A string representing the AABB.
     */
    @Override
    public String toString() {
        return "AABB [min=" + min + ", max=" + max + "]";
    }

    public double[] getLengths() {
        var lengthOfAxis = max.subtract(min);
        return new double[]{lengthOfAxis.getX(), lengthOfAxis.getY(), lengthOfAxis.getZ()};
    }


}

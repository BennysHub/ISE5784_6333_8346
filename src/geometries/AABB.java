package geometries;

import primitives.Point;
import primitives.Ray;

import java.util.Collection;

/**
 * Represents an Axis-Aligned Bounding Box (AABB) used for bounding volumes in geometric computations.
 */
public class AABB {
    private Point min;
    private Point max;
    /**
     * the first Geometry center is only updated in the AABB(Point min, Point max) constructor after marge or
     */
    private double[] center;
    private boolean isCenterValid = false;

    /**
     * Default constructor to initialize an empty AABB with maximum possible values.
     */
    public AABB() {
        this.min = new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.max = new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
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
        center = getCenter();
        isCenterValid = true;
    }

    /**
     * Copy constructor to initialize a new AABB as a copy of another AABB.
     *
     * @param other1 The AABB to copy.
     */
    public AABB(AABB other1, AABB other2) {
        this.min = other1.min;
        this.max = other1.max;
        this.merge(other2);
    }



    /**
     * Constructor to initialize an AABB that encompasses all intersectables in the given collection.
     *
     * @param intersectables A collection of intersectable objects to encompass in the AABB.
     */
    public AABB(Collection<Intersectable> intersectables) {
        this();
        for (Intersectable intersectable : intersectables) {
            intersectable.calculateAABB();
            this.merge(intersectable.aabb);
        }
    }

    /**
     * Expands the AABB to include a given point.
     *
     * @param point The point to include in the AABB.
     */
    public void expand(Point point) {
        min = new Point(Math.min(min.getX(), point.getX()), Math.min(min.getY(), point.getY()), Math.min(min.getZ(), point.getZ()));
        max = new Point(Math.max(max.getX(), point.getX()), Math.max(max.getY(), point.getY()), Math.max(max.getZ(), point.getZ()));
        //center = getCenter();
    }

    /**
     * Merges this AABB with another AABB.
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
    @SuppressWarnings("unused")
    public boolean intersects(AABB other) {
        return (this.max.getX() >= other.min.getX() && this.min.getX() <= other.max.getX()) &&
                (this.max.getY() >= other.min.getY() && this.min.getY() <= other.max.getY()) &&
                (this.max.getZ() >= other.min.getZ() && this.min.getZ() <= other.max.getZ());
    }

    /**
     * Returns the center of the bounding box.
     *
     * @return An array containing the x, y, and z coordinates of the AABB center.
     */
    public double[] getCenter() {
        if (isCenterValid)
            return center;//can improve if after update center again/
        double centerX = (min.getX() + max.getX()) / 2;
        double centerY = (min.getY() + max.getY()) / 2;
        double centerZ = (min.getZ() + max.getZ()) / 2;
        return new double[]{centerX, centerY, centerZ};
    }

    /**
     * Gets the minimum point of the AABB.
     *
     * @return The minimum point of the AABB.
     */
    @SuppressWarnings("unused")
    public Point getMin() {
        return min;
    }

    /**
     * Gets the maximum point of the AABB.
     *
     * @return The maximum point of the AABB.
     */
    @SuppressWarnings("unused")
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
}

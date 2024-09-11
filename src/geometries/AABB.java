package geometries;

import primitives.Point;
import primitives.Ray;


public class AABB {
    private Point min;
    private Point max;

    // Constructor to initialize an empty bounding box
    public AABB() {
        this.min = new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        this.max = new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    }

    // Constructor to initialize the bounding box with specific min and max points
    public AABB(Point min, Point max) {
        this.min = min;
        this.max = max;
    }

    // AABB for Sphere
    public static AABB fromSphere(Point center, double radius) {
        Point min = new Point(center.getX() - radius, center.getY() - radius, center.getZ() - radius);
        Point max = new Point(center.getX() + radius, center.getY() + radius, center.getZ() + radius);
        return new AABB(min, max);
    }

    // AABB for Triangle
    public static AABB fromTriangle(Point p1, Point p2, Point p3) {
        double minX = Math.min(p1.getX(), Math.min(p2.getX(), p3.getX()));
        double minY = Math.min(p1.getY(), Math.min(p2.getY(), p3.getY()));
        double minZ = Math.min(p1.getZ(), Math.min(p2.getZ(), p3.getZ()));

        double maxX = Math.max(p1.getX(), Math.max(p2.getX(), p3.getX()));
        double maxY = Math.max(p1.getY(), Math.max(p2.getY(), p3.getY()));
        double maxZ = Math.max(p1.getZ(), Math.max(p2.getZ(), p3.getZ()));

        Point min = new Point(minX, minY, minZ);
        Point max = new Point(maxX, maxY, maxZ);
        return new AABB(min, max);
    }

    // Expands the AABB to include a given point
    public void expand(Point point) {
        min = new Point(Math.min(min.getX(), point.getX()), Math.min(min.getY(), point.getY()), Math.min(min.getZ(), point.getZ()));
        max = new Point(Math.max(max.getX(), point.getX()), Math.max(max.getY(), point.getY()), Math.max(max.getZ(), point.getZ()));
    }

    // Computes the bounding box for an array of points
    public void computeBoundingBox(Point[] points) {
        for (Point point : points) {
            expand(point);
        }
    }

    // Merges this AABB with another AABB (used for internal BVH nodes)
    public void merge(AABB other) {
        this.expand(other.min);
        this.expand(other.max);
    }

    // Calculates the surface area of the AABB (used in SAH)
    public double surfaceArea() {
        double dx = max.getX() - min.getX();
        double dy = max.getY() - min.getY();
        double dz = max.getZ() - min.getZ();
        return 2 * (dx * dy + dy * dz + dz * dx);
    }

    // Checks if this AABB intersects with another AABB
    public boolean intersects(AABB other) {
        return (this.max.getX() >= other.min.getX() && this.min.getX() <= other.max.getX()) &&
                (this.max.getY() >= other.min.getY() && this.min.getY() <= other.max.getY()) &&
                (this.max.getZ() >= other.min.getZ() && this.min.getZ() <= other.max.getZ());
    }

    // Returns the center of the bounding box (useful for BVH splitting)
    public Point getCenter() {
        return new Point(
                (min.getX() + max.getX()) / 2,
                (min.getY() + max.getY()) / 2,
                (min.getZ() + max.getZ()) / 2
        );
    }

    // Getters for the min and max points
    public Point getMin() {
        return min;
    }

    public Point getMax() {
        return max;
    }

    // Checks if a ray intersects with this AABB
    public boolean rayIntersects(Ray ray) {
        Point rayOrigin = ray.getOrigin();
        Point rayDir = ray.getDirection();

        double tmin = (min.getX() - rayOrigin.getX()) / rayDir.getX();
        double tmax = (max.getX() - rayOrigin.getX()) / rayDir.getX();
        if (tmin > tmax) {
            double temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        double tymin = (min.getY() - rayOrigin.getY()) / rayDir.getY();
        double tymax = (max.getY() - rayOrigin.getY()) / rayDir.getY();
        if (tymin > tymax) {
            double temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax))
            return false;

        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;

        double tzmin = (min.getZ() - rayOrigin.getZ()) / rayDir.getZ();
        double tzmax = (max.getZ() - rayOrigin.getZ()) / rayDir.getZ();
        if (tzmin > tzmax) {
            double temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        return !((tmin > tzmax) || (tzmin > tmax));
    }

    // String representation of the AABB for debugging
    @Override
    public String toString() {
        return "AABB [min=" + min + ", max=" + max + "]";
    }
}

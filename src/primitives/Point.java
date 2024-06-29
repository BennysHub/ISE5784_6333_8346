package primitives;

/**
 * Represents a point in 3D space with three coordinates.
 *
 * @author Benny Avrahami
 */
public class Point {
    /**
     * The x, y, and z coordinates of the point, encapsulated in a Double3 object.
     */
    final protected Double3 xyz;

    /**
     * A constant representing the origin point (0,0,0).
     */
    public static final Point ZERO = new Point(0, 0, 0);

    /**
     * Constructs a new Point with the specified coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @param z The z-coordinate of the point.
     */
    public Point(double x, double y, double z) {
        xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a new Point using a Double3 object.
     *
     * @param double3 The Double3 object representing the coordinates of the point.
     */
    public Point(Double3 double3) {
        xyz = double3;
    }

    /**
     * Adds the coordinates of the given vector to this point and returns the resulting point.
     *
     * @param vector The vector to add to this point.
     * @return A new Point resulting from the addition of the current point and the vector.
     */
    public Point add(Vector vector) {
        return new Point(xyz.add(vector.xyz));
    }

    /**
     * Subtracts the coordinates of the given point from this point and returns the resulting vector.
     *
     * @param point The point to subtract from this point.
     * @return A new Vector resulting from the subtraction of the given point from this point.
     */
    public Vector subtract(Point point) {
        return new Vector(xyz.subtract(point.xyz));
    }

    /**
     * Calculates the distance between this point and another point.
     *
     * @param point The point to calculate the distance to.
     * @return The distance between this point and the given point.
     */
    public double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    /**
     * Calculates the squared distance between this point and another point.
     *
     * @param point The point to calculate the squared distance to.
     * @return The squared distance between this point and the given point.
     */
    public double distanceSquared(Point point) {
        double dx = xyz.d1 - point.xyz.d1;
        double dy = xyz.d2 - point.xyz.d2;
        double dz = xyz.d3 - point.xyz.d3;
        return dx * dx + dy * dy + dz * dz;//simple 3d distance squared
    }

    @Override
    public String toString() {
        return String.valueOf(xyz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Point other
                && xyz.equals(other.xyz);
    }
}

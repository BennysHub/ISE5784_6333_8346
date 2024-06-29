package primitives;

/**
 * Represents a vector in 3D space, extending the Point class with additional vector operations.
 * Vectors can be added, scaled, and normalized, and their dot and cross products can be computed.
 *
 * @author Benny Avrahami
 */
public class Vector extends Point {

    /**
     * Constructs a new Vector with the specified coordinates.
     * Throws IllegalArgumentException if the vector is a zero vector.
     *
     * @param c1 The x-coordinate of the vector.
     * @param c2 The y-coordinate of the vector.
     * @param c3 The z-coordinate of the vector.
     */
    public Vector(double c1, double c2, double c3) {
        super(c1, c2, c3);
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Vector Zero is not allowed.");
    }

    /**
     * Constructs a new Vector using a Double3 object.
     * Throws IllegalArgumentException if the vector is a zero vector.
     *
     * @param double3 The Double3 object representing the coordinates of the vector.
     */
    public Vector(Double3 double3) {
        super(double3);
        if (xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Vector Zero is not allowed.");
    }

    /**
     * Adds the coordinates of the given vector to this vector and returns the resulting vector.
     *
     * @param vector The vector to add to this vector.
     * @return A new Vector resulting from the addition of this vector and the given vector.
     */
    public Vector add(Vector vector) {
        return new Vector(xyz.add(vector.xyz));
    }

    /**
     * Scales this vector by the given scalar and returns the resulting vector.
     *
     * @param doubleNum The scalar to scale the vector by.
     * @return A new Vector resulting from the scaling of this vector by the given scalar.
     */
    public Vector scale(double doubleNum) {
        return new Vector(xyz.scale(doubleNum));
    }

    /**
     * Computes the dot product of this vector with the given vector.
     *
     * @param vector The vector to compute the dot product with.
     * @return The dot product of this vector and the given vector.
     */
    public double dotProduct(Vector vector) {
        return xyz.d1 * vector.xyz.d1 + xyz.d2 * vector.xyz.d2 + xyz.d3 * vector.xyz.d3;
    }

    /**
     * Computes the cross product of this vector with the given vector.
     *
     * @param vector The vector to compute the cross product with.
     * @return A new Vector resulting from the cross product of this vector and the given vector.
     */
    public Vector crossProduct(Vector vector) {
        return new Vector(
                xyz.d2 * vector.xyz.d3 - xyz.d3 * vector.xyz.d2,
                xyz.d3 * vector.xyz.d1 - xyz.d1 * vector.xyz.d3,
                xyz.d1 * vector.xyz.d2 - xyz.d2 * vector.xyz.d1
        );
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return The squared length of the vector.
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of the vector.
     *
     * @return The length of the vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }


    /**
     * Normalizes this vector and returns the resulting unit vector.
     *
     * @return A new Vector that is the normalized version of this vector.
     */
    public Vector normalize() {
        return new Vector(xyz.reduce(length()));
    }

    @Override
    public String toString() {
        return String.format("Vector --> %s", xyz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Vector other
                && super.equals(other);
    }
}

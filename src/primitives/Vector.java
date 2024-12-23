package primitives;

import static primitives.Util.isZero;

/**
 * Represents a vector in 3D space, extending the {@code Point} class with additional vector-specific operations.
 * Vectors are immutable and can be used for various geometric computations, such as addition, scaling,
 * normalization, and calculating dot- and cross-products.
 *
 * <p>Zero vectors are not allowed for construction to ensure mathematical correctness.</p>
 *
 * @author Benny Avrahami
 */
public class Vector extends Point {

    /**
     * The unit vector in the positive X direction.
     * This constant is normalized upon initialization.
     */
    public static final Vector UNIT_X = new Vector(new Double3(1, 0, 0)).normalize();

    /**
     * The unit vector in the positive Y direction.
     * This constant is normalized upon initialization.
     */
    public static final Vector UNIT_Y = new Vector(new Double3(0, 1, 0)).normalize();

    /**
     * The unit vector in the positive Z direction.
     * This constant is normalized upon initialization.
     */
    public static final Vector UNIT_Z = new Vector(new Double3(0, 0, 1)).normalize();

    /**
     * Indicates whether this vector is normalized.
     */
    private final boolean isNormalized;

    /**
     * Constructs a new {@code Vector} with the specified coordinates.
     * Throws an {@code IllegalArgumentException} if the vector is a zero vector.
     *
     * @param c1 the X-coordinate of the vector.
     * @param c2 the Y-coordinate of the vector.
     * @param c3 the Z-coordinate of the vector.
     * @throws IllegalArgumentException if the vector is a zero vector.
     */
    public Vector(double c1, double c2, double c3) {
        super(c1, c2, c3);
        if (xyz.equals(Double3.ZERO)) {// TODO: vector zero case
            throw new IllegalArgumentException("Vector Zero is not allowed.");
        }
        this.isNormalized = false;
    }

    /**
     * Constructs a new {@code Vector} using a {@code Double3} object.
     * Throws an {@code IllegalArgumentException} if the vector is a zero vector.
     *
     * @param double3 the {@code Double3} object representing the vector's coordinates.
     * @throws IllegalArgumentException if the vector is a zero vector.
     */
    public Vector(Double3 double3) {
        super(double3);
        if (xyz.equals(Double3.ZERO)) {
            throw new IllegalArgumentException("Vector Zero is not allowed.");
        }
        this.isNormalized = false;
    }

    private Vector(Double3 double3, boolean isNormalized) {
        super(double3);
        this.isNormalized = isNormalized;
    }

    /**
     * Returns a new {@code Vector} representing the addition of this vector and another.
     *
     * @param vector the vector to add.
     * @return a new {@code Vector} resulting from the addition.
     */
    public Vector add(Vector vector) {
        return new Vector(xyz.add(vector.xyz));
    }

    /**
     * Returns a new {@code Vector} representing the scaling of this vector by a scalar.
     *
     * @param scalar the scalar value to scale the vector by.
     * @return a new {@code Vector} scaled by the given scalar.
     */
    public Vector scale(double scalar) {
        return new Vector(xyz.scale(scalar));
    }

    /**
     * Computes a perpendicular unit vector to this vector.
     *
     * @return a normalized vector perpendicular to this vector.
     */
    public Vector perpendicular() {
        return (this.getY() != this.getX())
                ? new Vector(this.getY(), -this.getX(), 0).normalize()
                : new Vector(this.getZ(), 0, -this.getX()).normalize();
    }

    /**
     * Computes the dot product of this vector and another.
     *
     * @param vector the vector to compute the dot product with.
     * @return the dot product.
     */
    public double dotProduct(Vector vector) {
        return xyz.d1 * vector.xyz.d1 + xyz.d2 * vector.xyz.d2 + xyz.d3 * vector.xyz.d3;
    }

    /**
     * Computes the cross-product of this vector and another.
     *
     * @param vector the vector to compute the cross-product with.
     * @return a new {@code Vector} resulting from the cross-product.
     */
    public Vector crossProduct(Vector vector) {
        return new Vector(
                xyz.d2 * vector.xyz.d3 - xyz.d3 * vector.xyz.d2,
                xyz.d3 * vector.xyz.d1 - xyz.d1 * vector.xyz.d3,
                xyz.d1 * vector.xyz.d2 - xyz.d2 * vector.xyz.d1
        );
    }

    /**
     * Checks if this vector is parallel to another vector.
     *
     * @param vector the vector to compare with.
     * @return {@code true} if the vectors are parallel; {@code false} otherwise.
     */
    public boolean isParallel(Vector vector) {
        return isZero(xyz.d2 * vector.xyz.d3 - xyz.d3 * vector.xyz.d2)
                && isZero(xyz.d3 * vector.xyz.d1 - xyz.d1 * vector.xyz.d3)
                && isZero(xyz.d1 * vector.xyz.d2 - xyz.d2 * vector.xyz.d1);
    }

    /**
     * Checks if this vector is perpendicular to another vector.
     *
     * @param other the vector to compare with.
     * @return {@code true} if the vectors are perpendicular; {@code false} otherwise.
     */
    public boolean isPerpendicular(Vector other) {
        return isZero(this.dotProduct(other));
    }

    /**
     * Projects this vector onto another vector.
     *
     * @param other the vector to project onto.
     * @return the projection of this vector onto the given vector.
     */
    public Vector project(Vector other) {
        double dotProduct = this.dotProduct(other);
        double otherLengthSquared = other.lengthSquared();
        return other.scale(dotProduct / otherLengthSquared);
    }

    /**
     * Rejects this vector from another vector.
     *
     * <p>The rejection of a vector is the component of the vector that is orthogonal to the other vector.
     * It can be calculated as the difference between the vector and its projection onto the other vector.</p>
     *
     * @param other the vector to reject from.
     * @return the rejection of this vector from the given vector.
     */
    public Vector reject(Vector other) {
        return other.isPerpendicular(this) ? this : this.subtract(this.project(other));// TODO: vector zero case
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return the squared length of the vector.
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Calculates the length of the vector.
     *
     * @return the length of the vector.
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Normalizes this vector and returns the resulting unit vector.
     *
     * @return a new {@code Vector} that is the normalized version of this vector.
     */
    public Vector normalize() {
        return isNormalized ? this : new Vector(xyz.reduce(length()), true);
    }

    @Override
    public String toString() {
        return String.format("Vector --> %s", xyz);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Vector other && super.equals(other);
    }
}

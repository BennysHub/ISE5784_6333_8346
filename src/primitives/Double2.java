package primitives;

import static primitives.Util.isZero;

/**
 * A 2D vector or point in the XY plane, represented by two double values: x and y.
 * Provides operations to manipulate the vector or point, such as scaling and comparison.
 * <p>
 * The {@link Double2} class represents a simple 2D entity, typically used in 2D vector math,
 * geometrical computations, and graphics where 2D coordinates or directions are required.
 *
 * @author Benny Avrahami
 */
public record Double2(double x, double y) {

    /**
     * A constant representing the zero vectors (0, 0).
     */
    public static final Double2 ZERO = new Double2(0, 0);

    /**
     * Scales the vector by the given scalar.
     * Multiplies both the x and y components of the vector by the specified number.
     *
     * @param number the scalar factor to scale the vector
     * @return a new {@link Double2} object representing the scaled vector
     */
    public Double2 scale(double number) {
        return new Double2(x * number, y * number);
    }

    /**
     * Converts the vector to a string representation in the format (x, y).
     *
     * @return a string representing the vector
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Checks if this vector is equal to another object.
     * Two vectors are considered equal if their x and y components are equal within a tolerance
     * (using {@link Util#isZero}).
     *
     * @param obj the object to compare to
     * @return {@code true} if the object is an instance of {@link Double2} and both x and y components are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Double2(double x1, double y1)
                && isZero(x - x1) // Tolerance check for floating point equality
                && isZero(y - y1); // Tolerance check for floating point equality
    }
}

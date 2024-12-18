package primitives;

import static primitives.Util.isZero;

/**
 * This class represents a 3D vector or point, consisting of three double values: d1, d2, and d3.
 * It provides operations to manipulate 3D vectors or points such as addition, subtraction, scaling, and comparison.
 * <p>
 * The {@link Double3} class can be used in 3D geometric computations, vector math, and in scenarios where
 * three-dimensional data (e.g., RGB color values) need to be represented and manipulated.
 *
 * @author Dan Zilberstein
 */
public class Double3 {
    /**
     * Zero triad (0,0,0)
     */
    public static final Double3 ZERO = new Double3(0, 0, 0);

    /**
     * One's triad (1,1,1)
     */
    public static final Double3 ONE = new Double3(1, 1, 1);

    /**
     * First number (X component).
     */
    final double d1;

    /**
     * Second number (Y component).
     */
    final double d2;

    /**
     * Third number (Z component).
     */
    final double d3;

    /**
     * Constructor to initialize a Double3 object with its three number values.
     *
     * @param d1 the first number value
     * @param d2 the second number value
     * @param d3 the third number value
     */
    public Double3(double d1, double d2, double d3) {
        this.d1 = d1;
        this.d2 = d2;
        this.d3 = d3;
    }

    /**
     * Constructor to initialize a Double3 object where all three values are the same.
     *
     * @param value the number value for all three components (d1, d2, d3)
     */
    public Double3(double value) {
        this.d1 = value;
        this.d2 = value;
        this.d3 = value;
    }

    /**
     * Checks if this Double3 object is equal to another object.
     * Two Double3 objects are considered equal if their components are equal within a tolerance,
     * using {@link Util#isZero} for floating-point comparisons.
     *
     * @param obj the object to compare with
     * @return true if the object is an instance of Double3 and all components are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Double3 other)
                && isZero(d1 - other.d1)
                && isZero(d2 - other.d2)
                && isZero(d3 - other.d3);
    }

    /**
     * Returns a hash code for the Double3 object based on its components.
     * The hash code is calculated by summing the components and rounding the result.
     *
     * @return a hash code for this object
     */
    @Override
    public int hashCode() {
        return (int) Math.round(d1 + d2 + d3);
    }

    /**
     * Returns a string representation of the Double3 object in the format "(d1, d2, d3)".
     *
     * @return a string representation of the Double3 object
     */
    @Override
    public String toString() {
        return "(" + d1 + "," + d2 + "," + d3 + ")";
    }

    /**
     * Adds two Double3 objects component-wise and returns a new Double3 object with the result.
     *
     * @param rhs the right-hand side operand for addition
     * @return a new Double3 object representing the result of the addition
     */
    public Double3 add(Double3 rhs) {
        return new Double3(d1 + rhs.d1, d2 + rhs.d2, d3 + rhs.d3);
    }

    /**
     * Subtracts one Double3 object from another component-wise and returns a new Double3 object with the result.
     *
     * @param rhs the right-hand side operand for subtraction
     * @return a new Double3 object representing the result of the subtraction
     */
    public Double3 subtract(Double3 rhs) {
        return new Double3(d1 - rhs.d1, d2 - rhs.d2, d3 - rhs.d3);
    }

    /**
     * Scales (multiplies)
     * the components of this Double3 object by a scalar value and returns a new Double3 object with the result.
     *
     * @param rhs the scalar value to multiply with
     * @return a new Double3 object representing the scaled vector
     */
    public Double3 scale(double rhs) {
        return new Double3(d1 * rhs, d2 * rhs, d3 * rhs);
    }

    /**
     * Reduces (divides)
     * the components of this Double3 object by a scalar value and returns a new Double3 object with the result.
     *
     * @param rhs the scalar value to divide by
     * @return a new Double3 object representing the reduced vector
     */
    public Double3 reduce(double rhs) {
        return new Double3(d1 / rhs, d2 / rhs, d3 / rhs);
    }

    /**
     * Multiplies two Double3 objects component-wise and returns a new Double3 object with the result.
     *
     * @param rhs the right-hand side operand for multiplication
     * @return a new Double3 object representing the result of the multiplication
     */
    public Double3 product(Double3 rhs) {
        return new Double3(d1 * rhs.d1, d2 * rhs.d2, d3 * rhs.d3);
    }

    /**
     * Checks if all components of this Double3 object are less than a specified value.
     *
     * @param k the value to compare the components to
     * @return true if all components are less than the specified value, false otherwise
     */
    public boolean lowerThan(double k) {
        return d1 < k && d2 < k && d3 < k;
    }

    /**
     * Checks if all components of this Double3 object are less than the corresponding components in another Double3 object.
     *
     * @param other the other Double3 object to compare with
     * @return true if all components of this object are less than those in the other object, false otherwise
     */
    public boolean lowerThan(Double3 other) {
        return d1 < other.d1 && d2 < other.d2 && d3 < other.d3;
    }
}

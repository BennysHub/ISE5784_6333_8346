package utils;

/**
 * The {@code Util} class provides utility methods for mathematical operations and accuracy control.
 * It includes methods for determining if a number is nearly zero, aligning values close to zero,
 * checking the sign of numbers, and generating random numbers within a specified range.
 *
 * <p>This class is designed for internal use and cannot be instantiated.</p>
 *
 * @author Dan
 */
public final class Util {
    /**
     * A constant for controlling the level of numerical precision.
     * The value represents the binary exponent threshold below which a number is considered "almost zero."
     * It is approximately equivalent to ~1/1,000,000,000,000 in decimal (12 digits of accuracy).
     */
    private static final int ACCURACY = -40;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Util() {
    }

    /**
     * Extracts the exponent of a {@code double} number based on its IEEE 754 bit-level representation.
     *
     * <p>The {@code double} format consists of:</p>
     * <ul>
     *   <li>One sign bit</li>
     *   <li>Eleven exponent bits (biased by 1023)</li>
     *   <li>Fifty-two mantissa bits (implicitly normalized with a leading 1)</li>
     * </ul>
     *
     * @param num the {@code double} value whose exponent is to be extracted.
     * @return the unbiased exponent of the number.
     */
    private static int getExp(double num) {
        long bits = Double.doubleToRawLongBits(num); // Get raw bits
        bits >>= 52; // Shift right to isolate the exponent
        bits &= 0x7FFL; // Mask to remove the sign bit and isolate the exponent
        return (int) bits - 1023; // Subtract the bias (1023) to get the actual exponent
    }

    /**
     * Checks whether a given number is effectively zero within the defined accuracy threshold.
     *
     * @param number the number to check.
     * @return {@code true} if the number is zero or close to zero; {@code false} otherwise.
     */
    public static boolean isZero(double number) {
        return getExp(number) < ACCURACY;
    }

    /**
     * Aligns a number to zero if it is close enough to zero within the defined accuracy threshold.
     *
     * @param number the number to align.
     * @return {@code 0.0} if the number is close to zero; otherwise, the original number.
     */
    public static double alignZero(double number) {
        return isZero(number) ? 0.0 : number;
    }

    /**
     * Checks whether two numbers have the same sign.
     *
     * @param n1 the first number.
     * @param n2 the second number.
     * @return {@code true} if both numbers have the same sign; {@code false} otherwise.
     */
    public static boolean compareSign(double n1, double n2) {
        return n1 * n2 > 0; // Same sign if the product is positive
    }

    /**
     * Generates a random {@code double} value within the specified range.
     *
     * @param min the inclusive lower bound of the range.
     * @param max the exclusive upper bound of the range.
     * @return a random {@code double} value in the range {@code [min, max)}.
     */
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }
}

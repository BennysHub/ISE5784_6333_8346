package primitives;

/**
 * Util class is used for some internal utilities, e.g. controlling accuracy
 *
 * @author Dan
 */
public final class Util {
    /**
     * It is binary, equivalent to ~1/1,000,000,000,000 in decimal (12 digits)
     */
    private static final int ACCURACY = -40;

    /**
     * Don't let anyone instantiate this class.
     */
    private Util() {
    }

    /**
     * Extracts the exponent of a {@code double} number based on its bit-level representation in memory.
     * The {@code double} data format in memory is as follows:
     * seee eeee eeee (1.)mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
     * where 's' is the sign bit, 'e' represents the 11 bits of the exponent, and 'm' represents the 52 bits of the normalized mantissa (53 bits including the leading 1).
     * The actual number is calculated as m * 2^e, where 1 &lt;= m &lt; 2.
     * Note: The exponent is stored "normalized" (i.e., it is always positive by adding a bias of 1023).
     *
     * @param num the original {@code double} number
     * @return the exponent value of the number
     */
    private static int getExp(double num) {
        // Convert the stored number to a set of bits
        long bits = Double.doubleToRawLongBits(num);
        // Shift all 52 bits to the right to isolate the exponent and remove the mantissa
        bits >>= 52;
        // Zero the sign bit with a mask to get only the exponent
        bits &= 0x7FFL;
        // "De-normalize" the exponent by subtracting the bias (1023)
        return (int) (bits) - 1023;
    }


    /**
     * Checks whether the number is [almost] zero
     *
     * @param number the number to check
     * @return true if the number is zero or almost zero, false otherwise
     */
    public static boolean isZero(double number) {
        return getExp(number) < ACCURACY;
    }

    /**
     * Aligns the number to zero if it is almost zero
     *
     * @param number the number to align
     * @return 0.0 if the number is very close to zero, the number itself
     * otherwise
     */
    public static double alignZero(double number) {
        return isZero(number) ? 0.0 : number;
    }

    /**
     * Check whether two numbers have the same sign
     *
     * @param n1 1st number
     * @param n2 2nd number
     * @return true if the numbers have the same sign
     */
    public static boolean compareSign(double n1, double n2) {
        return (n1 < 0 && n2 < 0) || (n1 > 0 && n2 > 0);
    }

    /**
     * Provide a real random number in range between min and max
     *
     * @param min value (included)
     * @param max value (excluded)
     * @return the random value
     */
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

}

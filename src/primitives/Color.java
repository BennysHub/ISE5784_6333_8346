package primitives;

/**
 * Wrapper class for {@link java.awt.Color}.
 * The constructors support RGB values,
 * allowing for non-negative components, including values greater than 255, which is
 * typically used for light manipulation.
 * <p>
 * RGB values are maintained without an upper limit of 255, enabling the manipulation
 * of light colors in a more flexible way.
 * <p>
 * Additional operations are included for common color manipulation tasks.
 *
 * @author Dan Zilberstein
 */
public class Color {

    /**
     * Black color (0, 0, 0)
     */
    public static final Color BLACK = new Color();

    /**
     * The internal fields maintain RGB components as {@link Double3} objects, where
     * each component is a double value.
     */
    private final Double3 rgb;

    /**
     * Default constructor - generates the black color (0, 0, 0).
     * This constructor is private and is used for static constants like {@link #BLACK}.
     */
    private Color() {
        rgb = Double3.ZERO;
    }

    /**
     * Constructor that generates a color from RGB components.
     * RGB components may be non-negative, and values can exceed 255 for light manipulation.
     *
     * @param r Red component (non-negative)
     * @param g Green component (non-negative)
     * @param b Blue component (non-negative)
     * @throws IllegalArgumentException if any of the components is negative
     */
    public Color(double r, double g, double b) {
        if (r < 0 || g < 0 || b < 0) {
            throw new IllegalArgumentException("Negative color component is illegal");
        }
        rgb = new Double3(r, g, b);
    }

    /**
     * Private constructor to generate a color from an existing {@link Double3} object.
     *
     * @param rgb {@link Double3} object representing the RGB components
     * @throws IllegalArgumentException if any of the components in {@link Double3} is negative
     */
    private Color(Double3 rgb) {
        if (rgb.d1 < 0 || rgb.d2 < 0 || rgb.d3 < 0) {
            throw new IllegalArgumentException("Negative color component is illegal");
        }
        this.rgb = rgb;
    }

    /**
     * Constructor that generates a color based on a {@link java.awt.Color} object.
     * The RGB components are extracted from the java.awt.Color and stored as a {@link Double3}.
     *
     * @param other {@link java.awt.Color} object to convert
     */
    public Color(java.awt.Color other) {
        rgb = new Double3(other.getRed(), other.getGreen(), other.getBlue());
    }

    /**
     * Computes the average color from a series of colors.
     *
     * @param colors one or more {@link Color} objects to average
     * @return a new {@link Color} object representing the average of the input colors
     */
    public static Color average(Color... colors) {
        Color averageColor = Color.BLACK;
        averageColor = averageColor.add(colors);
        return averageColor.reduce(colors.length);
    }

    /**
     * Computes the variance in RGB components between the input colors.
     * The variance is calculated by averaging the squared differences in each component (R, G, B).
     *
     * @param colors one or more {@link Color} objects to compute variance
     * @return a double value representing the average variance across RGB components
     */
    public static double variance(Color... colors) {
        int n = colors.length;
        double meanR = 0, meanG = 0, meanB = 0;
        for (Color color : colors) {
            meanR += color.rgb.d1;
            meanG += color.rgb.d2;
            meanB += color.rgb.d3;
        }
        meanR /= n;
        meanG /= n;
        meanB /= n;

        double varianceR = 0, varianceG = 0, varianceB = 0;
        for (Color color : colors) {
            varianceR += Math.pow(color.rgb.d1 - meanR, 2);
            varianceG += Math.pow(color.rgb.d2 - meanG, 2);
            varianceB += Math.pow(color.rgb.d3 - meanB, 2);
        }
        varianceR /= n;
        varianceG /= n;
        varianceB /= n;

        return (varianceR + varianceG + varianceB) / 3; // Average variance of R, G, B
    }

    /**
     * Converts the {@link Color} object to a {@link java.awt.Color} object.
     * Any component larger than 255 is clamped to 255.
     *
     * @return a new {@link java.awt.Color} object corresponding to this color
     */
    public java.awt.Color getColor() {
        int ir = (int) rgb.d1;
        int ig = (int) rgb.d2;
        int ib = (int) rgb.d3;
        return new java.awt.Color(Math.min(ir, 255), Math.min(ig, 255), Math.min(ib, 255));
    }

    /**
     * Adds this color to one or more other colors.
     *
     * @param colors one or more {@link Color} objects to add to this color
     * @return a new {@link Color} object which is the result of the addition
     */
    public Color add(Color... colors) {
        double rr = rgb.d1;
        double rg = rgb.d2;
        double rb = rgb.d3;
        for (Color c : colors) {
            rr += c.rgb.d1;
            rg += c.rgb.d2;
            rb += c.rgb.d3;
        }
        return new Color(rr, rg, rb);
    }

    /**
     * Scales the color by a scalar triad, multiplying each RGB component by the corresponding value.
     *
     * @param k a {@link Double3} scale factor for each RGB component
     * @return a new {@link Color} object which is the result of scaling
     * @throws IllegalArgumentException if any component in k is negative
     */
    public Color scale(Double3 k) {
        if (k.d1 < 0.0 || k.d2 < 0.0 || k.d3 < 0.0) {
            throw new IllegalArgumentException("Can't scale a color by a negative number");
        }
        return new Color(rgb.product(k));
    }

    /**
     * Scales the color by a single scalar, multiplying each RGB component by this value.
     *
     * @param k the scalar to scale the color by
     * @return a new {@link Color} object which is the result of scaling
     * @throws IllegalArgumentException if k is negative
     */
    public Color scale(double k) {
        if (k < 0.0) {
            throw new IllegalArgumentException("Can't scale a color by a negative number");
        }
        return new Color(rgb.scale(k));
    }

    public Double3 toDouble3(){
        return rgb;
    }

    /**
     * Scales the color by a factor of 1/k, reducing each RGB component by this factor.
     *
     * @param k the reduction factor
     * @return a new {@link Color} object which is the result of the reduction
     */
    public Color reduce(double k) {
        return new Color(rgb.reduce(k));
    }

    @Override
    public String toString() {
        return "rgb:" + rgb;
    }
}

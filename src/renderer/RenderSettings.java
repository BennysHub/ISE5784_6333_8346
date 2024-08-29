package renderer;

/**
 * The RenderSettings class contains configuration settings for rendering.
 * These settings control various rendering features such as shadows, anti-aliasing,
 * depth of field, and more.
 */
public class RenderSettings {
    /**
     * The maximum recursion level for color calculations.
     */
    protected static final int MAX_CALC_COLOR_LEVEL = 100;
    /**
     * The minimum value for the reflection/refraction coefficient in color calculations.
     */
    protected static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * The number of sample rays used for shadow calculations.
     */
    protected static final int SHADOW_RAYS_SAMPLE_COUNT = 9;//169;
    /**
     * Indicates whether soft shadows are enabled.
     */
    protected static boolean softShadowsEnabled = false;
    /**
     * Indicates whether antialiasing is enabled.
     */
    protected static boolean antiAliasingEnabled = false;
    /**
     * Indicates whether depth of field is enabled.
     */
    protected static boolean depthOfFieldEnabled = false;
    /**
     * Indicates whether glossy surfaces are enabled.
     */
    protected static boolean glossySurfacesEnabled = false;
    /**
     * Indicates whether diffused glass effect is enabled.
     */
    protected static boolean DiffusedGlassEnabled = false;
}


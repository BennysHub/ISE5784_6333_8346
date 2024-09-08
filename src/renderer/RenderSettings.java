package renderer;

/**
 * The RenderSettings class contains configuration settings for rendering.
 * These settings control various rendering features such as shadows, antialiasing,
 * depth of field, and more.
 */
public class RenderSettings {
    /**
     * The maximum recursion level for color calculations.
     */
    static final int MAX_CALC_COLOR_LEVEL = 100;
    /**
     * The minimum value for the reflection/refraction coefficient in color calculations.
     */
    static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * The number of sample rays used for shadow calculations.
     */
    static final int SHADOW_RAYS_SAMPLE_COUNT = 169;
    /**
     * Indicates whether soft shadows are enabled.
     */
    static boolean softShadowsEnabled = false;
    /**
     * Indicates whether antialiasing is enabled.
     */
    static boolean antiAliasingEnabled = false;
    /**
     * Indicates whether depth of field is enabled.
     */
    static boolean depthOfFieldEnabled = false;
    /**
     * Indicates whether glossy surfaces are enabled.
     */
    static boolean glossySurfacesEnabled = false;
    /**
     * Indicates whether diffused glass effect is enabled.
     */
    static boolean DiffusedGlassEnabled = false;
}


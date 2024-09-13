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
    static final int MAX_CALC_COLOR_LEVEL = 10;
    /**
     * The minimum value for the reflection/refraction coefficient in color calculations.
     */
    static final double MIN_CALC_COLOR_K = 0.001;
    /**
     * The number of sample rays used for shadow calculations.
     */
    static final int SHADOW_RAYS_SAMPLE_COUNT = 169;
    /**
     * The number of threads to use. Set to 0 for no multithreading
     */
    static int threadsCount = 16;
    /**
     * Indicates whether BVH is enabled.
     */
    static boolean BVHIsEnabled = false;
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


    public static boolean isBVHEnabled(){
        return BVHIsEnabled;
    }
}


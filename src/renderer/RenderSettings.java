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
    static public final int MAX_CALC_COLOR_LEVEL = 5;
    /**
     * The minimum value for the reflection/refraction coefficient in color calculations.
     */
    static public final double MIN_CALC_COLOR_K = 0.001;
    public static final int SHADOW_RAYS_SAMPLE_COUNT = 49 ;


    /**
     * The number of threads to use. Set to 0 for no multithreading
     */
    static int threadsCount = 0;
    /**
     * Indicates whether BVH is enabled.
     */
    static boolean BVHIsEnabled = false;
    static boolean SAHIsEnabled = false;
    static boolean CBRIsEnabled = false;
    /**
     * Indicates whether soft shadows are enabled.
     */
    static boolean softShadowsEnabled = false;

    public static boolean isSoftShadowsEnabled(){
        return softShadowsEnabled;
    }

    public static int getShadowRaysSampleCount(){
        return SHADOW_RAYS_SAMPLE_COUNT;
    }
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

    /**
     * Indicates whether BVH is enabled.
     *
     * @return true if BVH is enabled.
     */
    public static boolean isBVHEnabled() {
        return BVHIsEnabled;
    }
    public static boolean isSAHIsEnabled(){return BVHIsEnabled; }
}


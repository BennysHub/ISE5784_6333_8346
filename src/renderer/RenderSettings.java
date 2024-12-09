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

    /**
     * The number of threads to use
     */
    static int threadsCount = 4;
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

    static QualityLevel softShadowQuality =  QualityLevel.LOW;

    static boolean multiThreadingEnabled = false;

    static boolean parallelStreamsEnabled = false;

    /**
     * Indicates whether antialiasing is enabled.
     */
    static boolean antiAliasingEnabled = false;

    static QualityLevel antiAliasingQuality =  QualityLevel.LOW;
    /**
     * Indicates whether depth of field is enabled.
     */
    static boolean depthOfFieldEnabled = false;

    static QualityLevel depthOfFieldQuality =  QualityLevel.LOW;
    /**
     * Indicates whether glossy surfaces are enabled.
     */
    static boolean glossySurfacesEnabled = false;
    /**
     * Indicates whether diffused glass effect is enabled.
     */
    static boolean DiffusedGlassEnabled = false;


}


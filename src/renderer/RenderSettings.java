package renderer;

public class RenderSettings {
    protected static boolean softShadowsEnabled = false;
    protected boolean antiAliasingEnabled = false;
    protected static boolean depthOfFieldEnabled = false;
    protected static boolean glossySurfacesEnabled = false;
    protected static boolean DiffusedGlassEnabled = false;

    /**
     * The maximum recursion level for color calculations.
     */
    protected static final int MAX_CALC_COLOR_LEVEL = 100;

    /**
     * The minimum value for the reflection/refraction coefficient in color calculations.
     */
    protected static final double MIN_CALC_COLOR_K = 0.001;

    protected static final int SHADOW_RAYS_SAMPLE_COUNT = 169;

}

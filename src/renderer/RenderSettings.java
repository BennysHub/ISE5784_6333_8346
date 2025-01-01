package renderer;

import geometries.BVHNode;
import primitives.Double3;
import primitives.Material;

/**
 * The {@code RenderSettings} class contains configuration settings for rendering operations.
 * These settings control various features like shadows, antialiasing, depth of field,
 * multithreading, and optimizations such as BVH (Bounding Volume Hierarchy).
 *
 * <p>This class acts as a central point for managing global rendering options,
 * allowing customization of performance, quality, and visual effects.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li><b>Recursion Levels:</b> Controls maximum recursion depth for reflections and refractions.</li>
 *   <li><b>BVH and SAH:</b> Enables or disables spatial acceleration structures for faster ray intersections.</li>
 *   <li><b>Shadows:</b> Supports soft shadows with adjustable quality levels.</li>
 *   <li><b>Antialiasing:</b> Improves image quality by reducing jagged edges using super sampling techniques.</li>
 *   <li><b>Depth of Field:</b> Simulates camera lens blur effects, controlled by aperture size and focal length.</li>
 *   <li><b>Multithreading:</b> Enables parallel rendering for improved performance on multicore systems.</li>
 *   <li><b>Special Effects:</b> Includes glossy surfaces and diffused glass for enhanced realism.</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * RenderSettings.BVHIsEnabled = true;
 * RenderSettings.softShadowsEnabled = true;
 * RenderSettings.softShadowQuality = QualityLevel.HIGH;
 * }</pre>
 *
 * <h2>Field Details</h2>
 * <ul>
 *   <li>{@code MAX_CALC_COLOR_LEVEL}: The maximum recursion depth for calculating reflections and refractions.</li>
 *   <li>{@code MIN_CALC_COLOR_K}: The minimum contribution factor for recursive color calculations.</li>
 *   <li>{@code threadsCount}: Number of threads used for rendering. Default is 4.</li>
 *   <li>{@code BVHIsEnabled}: Enables the Bounding Volume Hierarchy optimization.</li>
 *   <li>{@code SAHIsEnabled}: Enables Surface Area Heuristic-based BVH splitting.</li>
 *   <li>{@code CBRIsEnabled}: Enables Cost-Benefit Ratio optimizations.</li>
 *   <li>{@code softShadowsEnabled}: Enables soft shadow rendering.</li>
 *   <li>{@code softShadowQuality}: Controls the quality level for soft shadows.</li>
 *   <li>{@code multiThreadingEnabled}: Toggles multi-threaded rendering.</li>
 *   <li>{@code parallelStreamsEnabled}: Toggles parallel streams for rendering.</li>
 *   <li>{@code antiAliasingEnabled}: Enables antialiasing for smoother edges.</li>
 *   <li>{@code antiAliasingQuality}: Controls the quality level for antialiasing.</li>
 *   <li>{@code depthOfFieldEnabled}: Enables depth-of-field effects.</li>
 *   <li>{@code depthOfFieldQuality}: Controls the quality level for depth-of-field effects.</li>
 *   <li>{@code glossySurfacesEnabled}: Toggles glossy surface effects.</li>
 *   <li>{@code DiffusedGlassEnabled}: Toggles diffused glass effects.</li>
 * </ul>
 *
 * <h2>Design Considerations</h2>
 * <ul>
 *   <li>All fields are static, making the class globally accessible and easy to configure.</li>
 *   <li>Default values provide a balance between performance and quality.</li>
 *   <li>Combines performance optimizations (e.g., BVH, multithreading) with visual effects for flexible usage.</li>
 * </ul>
 *
 * @author Benny Avrahami
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

    static BVHNode.BVHBuildMethod bvhBuildMethod = BVHNode.BVHBuildMethod.MEDIAN_SPLIT;

    static boolean CBRIsEnabled = false;
    /**
     * Indicates whether soft shadows are enabled.
     */
    static boolean softShadowsEnabled = false;

    static QualityLevel softShadowQuality = QualityLevel.LOW;

    static boolean multiThreadingEnabled = false;

    static boolean parallelStreamsEnabled = true;

    /**
     * Indicates whether antialiasing is enabled.
     */
    static boolean antiAliasingEnabled = false;

    static QualityLevel antiAliasingQuality = QualityLevel.LOW;
    /**
     * Indicates whether depth of field is enabled.
     */
    static boolean depthOfFieldEnabled = false;

    static QualityLevel depthOfFieldQuality = QualityLevel.MEDIUM;
    /**
     * Indicates whether glossy surfaces are enabled.
     */
    static boolean glossySurfacesEnabled = false;
    /**
     * Indicates whether diffused glass effect is enabled.
     */
    static boolean DiffusedGlassEnabled = false;

    static RayTracerBase.RayTracerMethod RAY_TRACER_METHOD = RayTracerBase.RayTracerMethod.BasicRayTracer;

    static Material RAY_MARCHING_GLOBAL_MATERIAL = Material.DEFAULT;

    static boolean RAY_MARCHING_SIMPLE_SHADING = false;


}


package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * The RayTracerBase class is an abstract base class for ray tracing in a scene.
 *
 * @author TzviYisrael and Benny
 */
public abstract class RayTracerBase {
    /**
     * The scene that contains all the elements for the render
     */
    protected final Scene scene;

    /**
     * Constructs a RayTracerBase with the specified scene.
     *
     * @param scene the scene to be used for ray tracing
     */
    public RayTracerBase(Scene scene) {
        this.scene = scene;
    }

    /**
     * Traces a ray and returns the color at the closest intersection point.
     * If there are no intersections, it returns the background color.
     *
     * @param ray the ray to be traced
     * @return the color at the closest intersection point or the background color if no intersections are found
     */
    public abstract Color traceRay(Ray ray);

}


package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * The RayTracerBase class is an abstract base class for ray tracing in a scene.
 *
 * @author TzviYisrael and Benny
 */
public abstract class RayTracerBase {
    /**
     * The scene that contain all the elements for the render
     */
    protected Scene scene;

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
    public Color traceRay(Ray ray) {
        var list = scene.geometries.findIntersections(ray);
        return (list == null) ? scene.background : calcColor(ray.findClosestPoint(list));
    }

    /**
     * Calculates the color at the given point.
     * This method currently returns the background color of the scene.
     *
     * @param point the point at which the color is to be calculated
     * @return the color at the specified point
     */
    private Color calcColor(Point point) {
        return scene.ambientLight.getIntensity();
    }
}


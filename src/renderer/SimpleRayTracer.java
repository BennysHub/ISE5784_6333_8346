package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * The SimpleRayTracer class extends the RayTracerBase and provides a simple implementation for ray tracing in a scene.
 *
 * @author TzviYisrael and Benny
 */
public class SimpleRayTracer extends RayTracerBase {

    /**
     * Constructs a SimpleRayTracer with the specified scene.
     *
     * @param scene the scene to be used for ray tracing
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var closestPoint = ray.findClosestPoint(scene.geometries.findIntersections(ray));
        return (closestPoint == null) ? scene.background : calcColor(closestPoint);
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


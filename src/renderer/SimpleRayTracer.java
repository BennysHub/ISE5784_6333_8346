package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;
import geometries.Intersectable.GeoPoint;

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
        var closestGeoPoint = ray.findClosestGeoPoint(scene.geometries.findGeoIntersections(ray));
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint);
    }

    /**
     * Calculates the color at the given point.
     * This method currently returns the background color of the scene.
     *
     * @param geoPoint the point at which the color is to be calculated
     * @return the color at the specified point
     */
    private Color calcColor(GeoPoint geoPoint) {//TODO: SEE IF WE NEED TO ADD AMBIENT LIGHT ALSO or somthing elsa like "point color"

        return(geoPoint.geometry.getEmission().add(scene.ambientLight.getIntensity()));
    }
}


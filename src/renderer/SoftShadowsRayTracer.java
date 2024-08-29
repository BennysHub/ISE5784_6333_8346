package renderer;

import geometries.Intersectable.GeoPoint;
import lighting.LightSource;
import lighting.PointLight;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.super_sampling.Blackboard;
import scene.Scene;

import java.util.LinkedList;
import java.util.List;

/**
 * The SoftShadowsRayTracer class extends SimpleRayTracer to provide
 * functionality for rendering soft shadows.
 * It constructs multiple shadow rays
 * to simulate the effect of soft shadows.
 */
public class SoftShadowsRayTracer extends SimpleRayTracer {

    /**
     * Constructs a SoftShadowsRayTracer with the given scene.
     *
     * @param scene the scene to be rendered
     */
    public SoftShadowsRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Constructs shadow rays for soft shadow rendering.
     *
     * @param light the light source
     * @param p     the point to construct shadow rays from
     * @param n     the normal vector at the point
     * @return a list of shadow rays
     */
    private List<Ray> constructShadowRays(PointLight light, Point p, Vector n) {
        List<Point> areaPoints = Blackboard.getPointsOnCircle(light.getL(p), light.getPosition(), light.getSize(), RenderSettings.SHADOW_RAYS_SAMPLE_COUNT);
        List<Ray> shadowRays = new LinkedList<>();
        for (Point areaP : areaPoints) {
            shadowRays.add(new Ray(p, areaP.subtract(p), n));
        }
        return shadowRays;
    }

    /**
     * Calculates the transparency factor for a given GeoPoint and light source.
     *
     * @param gp    the GeoPoint
     * @param light the light source
     * @param l     the light vector
     * @param n     the normal vector at the GeoPoint
     * @return the transparency factor as a Double3
     */
    @Override
    protected Double3 transparency(GeoPoint gp, LightSource light, Vector l, Vector n) {
        Double3 ktr = Double3.ZERO;
        if (light instanceof PointLight pL) {
            List<Ray> shadowRays = constructShadowRays(pL, gp.point, n);
            for (Ray shadowRay : shadowRays)
                ktr = ktr.add(cumulativeTransparencyIntersection(scene.geometries.findGeoIntersections(shadowRay, light.getDistance(gp.point))));

            return ktr.reduce(shadowRays.size()); //TODO size == 0 ?? ==> NaN ok.
        }
        return super.transparency(gp, light, l, n);
    }
}

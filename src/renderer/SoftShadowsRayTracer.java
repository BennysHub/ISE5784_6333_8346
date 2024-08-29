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

public class SoftShadowsRayTracer extends SimpleRayTracer {

    public SoftShadowsRayTracer(Scene scene) {super(scene);}

    private List<Ray> constructShadowRays(PointLight light, Point p, Vector n) {
        List<Point> areaPoints = Blackboard.getPointsOnCircle(light.getL(p), light.getPosition(), light.getSize(), RenderSettings.SHADOW_RAYS_SAMPLE_COUNT);
        List<Ray> shadowRays = new LinkedList<>();
        for (Point areaP : areaPoints) {
            shadowRays.add(new Ray(p, areaP.subtract(p), n));
        }
        return shadowRays;
    }

    @Override
    protected Double3 transparency(GeoPoint gp, LightSource light, Vector l, Vector n) {
        Double3 ktr = Double3.ZERO;
        if (light instanceof PointLight pL) {
            List<Ray> shadowRays = constructShadowRays(pL, gp.point, n);
            for (Ray shadowRay: shadowRays)
                ktr = ktr.add(cumulativeTransparencyIntersection(scene.geometries.findGeoIntersections(shadowRay, light.getDistance(gp.point))));

            return ktr.reduce(shadowRays.size());//size == 0 ?? ==> NaN ok.
        }
        return super.transparency(gp, light, l, n);
    }
}

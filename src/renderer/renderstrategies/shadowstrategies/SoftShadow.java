package renderer.renderstrategies.shadowstrategies;

import geometries.Intersectable;
import lighting.LightSource;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import renderer.RenderSettings;
import scene.Scene;

import java.util.List;

public class SoftShadow extends ShadowStrategy {


    public SoftShadow(Scene scene) {
        super(scene);
    }

    @Override
    public Double3 transparency(Intersectable.GeoPoint gp, LightSource light, Vector n) {
        Double3 ktr = Double3.ZERO;
        List<Ray> shadowRays = light.getRaysBeam(gp.point, n, RenderSettings.SHADOW_RAYS_SAMPLE_COUNT);
        for (Ray shadowRay : shadowRays)
            ktr = ktr.add(totalTransparency(scene.geometries.findGeoIntersections(shadowRay, shadowRay.getOrigin().distance(gp.point))));
        return ktr.reduce(shadowRays.size());
    }
}

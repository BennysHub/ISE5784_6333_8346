package renderer.renderstrategies.shadowstrategies;

import geometries.Intersectable;
import lighting.LightSource;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

public class HardShadow extends ShadowStrategy {

    public HardShadow(Scene scene) {
        super(scene);
    }

    @Override
    public Double3 transparency(Intersectable.GeoPoint gp, LightSource light, Vector n) {//we didn't pass l
        Vector lightDirection = light.getL(gp.point).scale(-1);
        Ray lightRay = new Ray(gp.point, lightDirection, n);
        return totalTransparency(scene.geometries.findGeoIntersections(lightRay, light.getDistance(gp.point)));
    }

}

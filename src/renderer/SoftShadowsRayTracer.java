package renderer;

import geometries.Intersectable;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;
import static primitives.Util.compareSign;

public class SoftShadowsRayTracer extends SimpleRayTracer {
    /**
     * Constructs a SimpleRayTracer with the specified scene.
     *
     * @param scene the scene to be used for ray tracing
     */
    public SoftShadowsRayTracer(Scene scene) {
        super(scene);
    }

    /**
     * Calculates the local effects of lighting at the given point.
     *
     * @param gp the point at which the local effects are to be calculated
     * @param v  the direction vector of the ray that intersects with the gp
     * @param n  the normal to the surface at the gp
     * @param nv result of dot-product n and v
     * @param k  the attenuation factor
     * @return the color at the specified point including local lighting effects
     */
    @Override
    protected Color calcLocalEffects(Intersectable.GeoPoint gp, Vector v, Vector n, double nv, Double3 k) {//in soft shadow we can limit to be done only in first level and then use hard shadow
        Material material = gp.geometry.getMaterial();
        Color color = Color.BLACK;
        for (LightSource lightSource : scene.lights) {

            List<Ray> lightRays = lightSource.getRaysBeam(gp.point, RenderSettings.SHADOW_RAYS_SAMPLE_COUNT);
            int lightRaysSize = lightRays.size();

            for (Ray lightRay : lightRays) {
                Vector l = lightRay.getDirection();
                double nl = alignZero(n.dotProduct(l));

                if (compareSign(nl, nv)) {
                    Double3 ktr = transparency(gp, lightSource, lightRay);
                    if (!ktr.product(k).lowerThan(RenderSettings.MIN_CALC_COLOR_K)) {
                        Color iL = lightSource.getIntensity(gp.point).scale(ktr);
                        color = color.add(
                                iL.scale(calcDiffusive(material, nl).add(calcSpecular(material, n, l, nl, v))));
                    }
                }
                color = color.reduce(lightRaysSize);
            }

        }
        return color.add(gp.geometry.getEmission());
    }



}

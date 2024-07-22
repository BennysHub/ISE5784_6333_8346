package renderer;

import lighting.LightSource;
import primitives.*;
import scene.Scene;
import geometries.Intersectable.GeoPoint;

import static java.lang.Math.*;
import static primitives.Util.alignZero;

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
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint, ray.getDirection());
    }

    /**
     * Calculates the color at the given point.
     * This method currently returns the background color of the scene.
     *
     * @param gp the point at which the color is to be calculated
     * @return the color at the specified point
     */
    private Color calcColor(GeoPoint gp, Vector v) {
        Vector n = gp.geometry.getNormal(gp.point);
        return scene.ambientLight.getIntensity()
                .add(calcLocalEffects(gp, v));

//        for (LightSource ls : scene.lights) {
//            Vector r = ls.getL(gp.point).subtract(
//                    n.scale(ls.getL(gp.point).dotProduct(n))).scale(2);
//            combinedColor.add(
//                    ls.getIntensity(gp.point)
//                            .scale((gp.geometry.getMaterial().kD
//                                    .scale(abs(ls.getL(gp.point).dotProduct(n))))
//                                    .add(gp.geometry.getMaterial().kS
//                                            .scale(pow(max(0, -v.dotProduct(r)), gp.geometry.getMaterial().shininess))))
//            );
//        }
//        return combinedColor;
    }

    private Color calcLocalEffects(GeoPoint gp, Vector v) {
        Vector n = gp.geometry.getNormal(gp.point);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0) return Color.BLACK;
        Material material = gp.geometry.getMaterial();
        Color color = gp.geometry.getEmission();
        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(gp.point);
            double nl = alignZero(n.dotProduct(l));
            if (nl * nv > 0) { // sign(nl) == sing(nv)
                Color iL = lightSource.getIntensity(gp.point);
                color = color.add(
                        iL.scale(calcDiffusive(material, nl)
                                .add(calcSpecular(material, n, l, nl, v))));
            }
        }
        return color;
    }

    private Double3 calcDiffusive(Material material, double nl) {
        return material.kD.scale(abs(nl));
    }

    private Double3 calcSpecular(Material material, Vector n, Vector l, double nl, Vector v) {
        return material.kS.scale(pow(max(0, -v.dotProduct(l.subtract(n.scale(2 * nl)))), material.shininess));
    }
}


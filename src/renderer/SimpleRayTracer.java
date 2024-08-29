package renderer;

import geometries.Intersectable.GeoPoint;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.Collection;

import static primitives.Util.alignZero;

/**
 * The SimpleRayTracer class extends the RayTracerBase and provides a simple implementation for ray tracing in a scene.
 *
 * @author TzviYisrael and Benny
 */
public class SimpleRayTracer extends RayTracerBase {

    private static final Double3 INITIAL_K = Double3.ONE;

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
        var closestGeoPoint = findClosestIntersection(ray);
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint, ray);
    }

    /**
     * Calculates the color at the given point.
     *
     * @param gp  the point at which the color is to be calculated
     * @param ray the ray that intersects with the gp
     * @return the color at the specified point
     */
    private Color calcColor(GeoPoint gp, Ray ray) {
        return calcColor(gp, ray, RenderSettings.MAX_CALC_COLOR_LEVEL, INITIAL_K)
                .add(scene.ambientLight.getIntensity());
    }

    /**
     * Calculates the color at the given point with recursion for global effects.
     *
     * @param gp    the point at which the color is to be calculated
     * @param ray   the ray that intersects with the gp
     * @param level the current recursion level
     * @param k     the attenuation factor
     * @return the color at the specified point including global lighting effects
     */
    private Color calcColor(GeoPoint gp, Ray ray, int level, Double3 k) {
        Vector v = ray.getDirection();
        Vector n = gp.geometry.getNormal(gp.point);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0) return Color.BLACK;

        Color color = calcLocalEffects(gp, v, n, nv, k);
        return 1 == level ? color : color.add(calcGlobalEffects(gp, v, n, nv, level, k));
    }

    /**
     * Calculates the local effects of lighting at the given point.
     *
     * @param gp the point at which the local effects are to be calculated
     * @param v  the direction vector of the ray that intersects with the gp
     * @param n  the normal to the surface at the gp
     * @param nv result of dot-product of n and v
     * @param k  the attenuation factor
     * @return the color at the specified point including local lighting effects
     */
    private Color calcLocalEffects(GeoPoint gp, Vector v, Vector n, double nv, Double3 k) {
        Material material = gp.geometry.getMaterial();
        Color color = gp.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(gp.point);
            double nl = alignZero(n.dotProduct(l));
            if ((nl * nv > 0)) { // sign(nl) == sign(nv)
                Double3 ktr = transparency(gp, lightSource, l, n);
                if (!ktr.product(k).lowerThan(RenderSettings.MIN_CALC_COLOR_K)) {
                    Color iL = lightSource.getIntensity(gp.point).scale(ktr);
                    color = color.add(
                            iL.scale(calcDiffusive(material, nl)
                                    .add(calcSpecular(material, n, l, nl, v))));
                }
            }
        }
        return color;
    }

    /**
     * Calculates the global effects of lighting at the given point.
     *
     * @param gp    the point at which the global effects are to be calculated
     * @param v     the direction vector of the ray that intersects with the gp
     * @param n     the normal to the surface at the gp
     * @param nv    result of dot-product of n and v
     * @param level the current recursion level
     * @param k     the attenuation factor
     * @return the color at the specified point including global lighting effects
     */
    private Color calcGlobalEffects(GeoPoint gp, Vector v, Vector n, double nv, int level, Double3 k) {
        Material material = gp.geometry.getMaterial();
        return calcGlobalEffect(constructRefractedRay(gp, v, n), material.kT, level, k)
                .add(calcGlobalEffect(constructReflectedRay(gp, v, n, nv), material.kR, level, k));
    }

    /**
     * Calculates the global effect of a ray with a given attenuation factor.
     *
     * @param ray   incoming ray
     * @param kx    the reflection/refraction coefficient
     * @param level the current recursion level
     * @param k     the attenuation factor
     * @return the color due to the global effect of the ray
     */
    private Color calcGlobalEffect(Ray ray, Double3 kx, int level, Double3 k) {
        Double3 kkx = kx.product(k);
        if (kkx.lowerThan(RenderSettings.MIN_CALC_COLOR_K)) return Color.BLACK;
        GeoPoint gp = findClosestIntersection(ray);
        return (gp == null ? scene.background : calcColor(gp, ray, level - 1, kkx)).scale(kx);
    }

    /**
     * Constructs a refracted ray at the given point.
     *
     * @param gp the point at which the ray is refracted
     * @param v  the direction vector of the ray that intersects with the gp
     * @param n  the normal to the surface at the gp
     * @return the refracted ray
     */
    private Ray constructRefractedRay(GeoPoint gp, Vector v, Vector n) {
        return new Ray(gp.point, v, n);
    }

    /**
     * Constructs a reflected ray at the given point.
     *
     * @param gp the point at which the ray is reflected
     * @param v  the direction vector of the ray that intersects with the gp
     * @param n  the normal to the surface at the gp
     * @param nv result of dot-product of n and v
     * @return the reflected ray
     */
    private Ray constructReflectedRay(GeoPoint gp, Vector v, Vector n, double nv) {
        return new Ray(gp.point, v.subtract(n.scale(2 * nv)), n);
    }

    /**
     * Finds the closest intersection point of the given ray with the scene geometries.
     *
     * @param ray the ray to find intersections for
     * @return the closest intersection point, or null if no intersections are found
     */
    private GeoPoint findClosestIntersection(Ray ray) {
        return ray.findClosestGeoPoint(scene.geometries.findGeoIntersections(ray));
    }

    /**
     * Calculates the diffusive component of the lighting.
     *
     * @param material the material of the geometry
     * @param nl       the dot product of the normal and light direction vectors
     * @return the diffusive component of the lighting
     */
    private Double3 calcDiffusive(Material material, double nl) {
        return material.kD.scale(Math.abs(nl));
    }

    /**
     * Calculates the specular component of the lighting.
     *
     * @param material the material of the geometry
     * @param n        the normal vector at the point
     * @param l        the vector from the light to the point
     * @param nl       the dot product of the normal and light vector to point
     * @param v        the ray of intersection, direction vector
     * @return the specular component of the lighting
     */
    private Double3 calcSpecular(Material material, Vector n, Vector l, double nl, Vector v) {
        Vector r = l.subtract(n.scale(2 * nl)); // Reflection vector
        double minusVR = -alignZero(v.dotProduct(r));
        return minusVR <= 0 ? Double3.ZERO :
                material.kS.scale(Math.pow(minusVR, material.shininess));
    }

    /**
     * Calculates the transparency of a point with respect to a light source.
     *
     * @param gp    the geometric point
     * @param light the light source
     * @param l     the vector from the light source to the point
     * @param n     the normal vector at the point
     * @return the transparency factor at the point
     */
    protected Double3 transparency(GeoPoint gp, LightSource light, Vector l, Vector n) {
        Vector lightDirection = l.scale(-1); // from point to lightSource
        Ray lightRay = new Ray(gp.point, lightDirection, n);
        return cumulativeTransparencyIntersection(scene.geometries.findGeoIntersections(lightRay, light.getDistance(gp.point)));
    }

    /**
     * Calculates the cumulative transparency factor for a collection of intersections.
     *
     * @param intersections the collection of GeoPoint intersections
     * @return the cumulative transparency factor as a Double3
     */
    protected Double3 cumulativeTransparencyIntersection(Collection<GeoPoint> intersections) {
        Double3 ktr = Double3.ONE;
        if (intersections == null) return ktr;
        for (GeoPoint p : intersections)
            ktr = ktr.product(p.geometry.getMaterial().kT);
        return ktr;
    }

}

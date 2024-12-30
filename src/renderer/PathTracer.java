package renderer;

import geometries.Intersectable;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;
import java.util.Random;

import static utils.Util.alignZero;
import static utils.Util.compareSign;

/**
 * Path tracer implementation for realistic light simulation.
 * Combines direct light sampling and recursive path tracing for indirect lighting.
 *
 * @author Benny Avrahami
 */
public class PathTracer extends RayTracerBase {

    private static final Random random = new Random();
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a PathTracer with the specified scene.
     *
     * @param scene the scene to be used for path tracing
     */
    public PathTracer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        var closestGeoPoint = findClosestIntersection(ray);
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint, ray, RenderSettings.MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * Calculates the color at the given point with recursive global illumination.
     *
     * @param gp    the geometry point
     * @param ray   the ray that intersects with the geometry point
     * @param level the current recursion level
     * @param k     the attenuation factor
     * @return the calculated color
     */
    private Color calcColor(Intersectable.GeoPoint gp, Ray ray, int level, Double3 k) {
        if (level == 0) return Color.BLACK;

        Material material = gp.geometry().getMaterial();
        Vector v = ray.getDirection();
        Vector n = gp.geometry().getNormal(gp.point());
        double nv = alignZero(n.dotProduct(v));

        if (nv == 0) return Color.BLACK;

        // Compute direct lighting
        Color directLight = calcDirectLighting(gp, n, v, nv);

        // Compute indirect lighting (recursive)
        Color indirectLight = calcGlobalEffects(gp, v, n, nv, level, k);

        return directLight.add(indirectLight).add(material.getEmission());
    }

    /**
     * Calculates the direct lighting contribution using explicit light sampling.
     *
     * @param gp the geometry point
     * @param n  the normal at the geometry point
     * @param nv the dot product of the normal and incoming ray direction
     * @return the direct lighting contribution
     */
    private Color calcDirectLighting(Intersectable.GeoPoint gp, Vector n, Vector v, double nv) {
        Point intersection = gp.point();
        Material material = gp.geometry().getMaterial();
        Color totalColor = Color.BLACK;

        for (LightSource lightSource : scene.lights) {
            Point[] lightSample = lightSource.getSamplePoints();
            Color lightColor = Color.BLACK;

            for (Point lightPoint : lightSample) {
                Vector l = lightSource.computeDirection(intersection, lightPoint);
                double nl = alignZero(n.dotProduct(l));

                if (compareSign(nl, nv)) {
                    Ray lightRay = new Ray(intersection, l.scale(-1), n);
                    Double3 ktr = transparency(intersection, lightPoint, lightRay);

                    if (!ktr.product(INITIAL_K).lowerThan(RenderSettings.MIN_CALC_COLOR_K)) {
                        Color iL = lightSource.computeIntensity(intersection, lightPoint).scale(ktr);
                        lightColor = lightColor.add(iL.scale(calcDiffusive(material, nl).add(calcSpecular(material, n, l, nl, v))));
                    }
                }
            }
            totalColor = totalColor.add(lightColor.reduce(lightSample.length));
        }
        return totalColor;
    }

    /**
     * Calculates the global illumination contribution (reflections and refractions).
     *
     * @param gp    the geometry point
     * @param v     the incoming ray direction
     * @param n     the normal at the geometry point
     * @param nv    the dot product of the normal and incoming ray direction
     * @param level the recursion depth
     * @param k     the attenuation factor
     * @return the global illumination contribution
     */
    private Color calcGlobalEffects(Intersectable.GeoPoint gp, Vector v, Vector n, double nv, int level, Double3 k) {
        Material material = gp.geometry().getMaterial();
        return calcGlobalEffect(constructRefractedRay(gp, v, n), material.kT, level, k)
                .add(calcGlobalEffect(constructReflectedRay(gp, v, n, nv), material.kR, level, k));
    }

    /**
     * Calculates the global effect of a ray (reflection or refraction).
     *
     * @param ray   the ray
     * @param kx    the attenuation factor for the effect
     * @param level the recursion depth
     * @param k     the cumulative attenuation factor
     * @return the color due to the global effect
     */
    private Color calcGlobalEffect(Ray ray, Double3 kx, int level, Double3 k) {
        Double3 kkx = kx.product(k);
        if (kkx.lowerThan(RenderSettings.MIN_CALC_COLOR_K)) return Color.BLACK;

        Intersectable.GeoPoint gp = findClosestIntersection(ray);
        return (gp == null ? scene.background : calcColor(gp, ray, level - 1, kkx)).scale(kx);
    }

    /**
     * Computes the transparency along a ray from the intersection point to a light source.
     *
     * @param intersection the intersection point
     * @param lightPoint   the light point
     * @param lightRay     the ray toward the light source
     * @return the transparency factor
     */
    private Double3 transparency(Point intersection, Point lightPoint, Ray lightRay) {
        List<Intersectable.GeoPoint> intersections = scene.geometries.findGeoIntersections(lightRay, intersection.distance(lightPoint));
        Double3 ktr = Double3.ONE;
        if (intersections == null) return ktr;

        for (Intersectable.GeoPoint p : intersections) {
            ktr = ktr.product(p.geometry().getMaterial().kT);
            if (ktr.lowerThan(RenderSettings.MIN_CALC_COLOR_K))
                return ktr;
        }
        return ktr;
    }

    /**
     * Constructs a refracted ray at a geometry point.
     */
    private Ray constructRefractedRay(Intersectable.GeoPoint gp, Vector v, Vector n) {
        return new Ray(gp.point(), v, n);
    }

    /**
     * Constructs a reflected ray at a geometry point.
     */
    private Ray constructReflectedRay(Intersectable.GeoPoint gp, Vector v, Vector n, double nv) {
        return new Ray(gp.point(), v.subtract(n.scale(2 * nv)), n);
    }


    /**
     * Calculates the diffusive component of the lighting.
     *
     * @param material the material of the geometry
     * @param nl       the dot product of the normal and light direction vectors
     * @return the diffusive component of the lighting
     */
    protected Double3 calcDiffusive(Material material, double nl) {
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
    protected Double3 calcSpecular(Material material, Vector n, Vector l, double nl, Vector v) {
        Vector r = l.subtract(n.scale(2 * nl)); // Reflection vector
        double minusVR = -alignZero(v.dotProduct(r));
        return minusVR <= 0 ? Double3.ZERO :
                material.kS.scale(Math.pow(minusVR, material.shininess));
    }


    /**
     * Finds the closest intersection point of the given ray with the scene geometries.
     *
     * @param ray the ray to find intersections for
     * @return the closest intersection point, or null if no intersections are found
     */
    protected Intersectable.GeoPoint findClosestIntersection(Ray ray) {
        return ray.findClosestGeoPoint(scene.geometries.findGeoIntersections(ray));
    }

}

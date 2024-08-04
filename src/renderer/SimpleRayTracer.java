package renderer;

import geometries.Geometry;
import geometries.Intersectable.GeoPoint;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import java.util.List;

import static primitives.Util.alignZero;

/**
 * The SimpleRayTracer class extends the RayTracerBase and provides a simple implementation for ray tracing in a scene.
 *
 * @author TzviYisrael and Benny
 */
public class SimpleRayTracer extends RayTracerBase {

    public static final int MAX_CALC_COLOR_LEVEL = 10;
    public static final double MIN_CALC_COLOR_K = 0.001;
    private static final Double3 INITIAL_K = Double3.ONE;

    /**
     * Constructs a SimpleRayTracer with the specified scene.
     *
     * @param scene the scene to be used for ray tracing
     */
    public SimpleRayTracer(Scene scene) {
        super(scene);
    }

    private static final double DELTA = 0.1;

    @Override
    public Color traceRay(Ray ray) {
        var closestGeoPoint = findClosestIntersection(ray);
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint, ray);
    }

    private Color calcColor(GeoPoint gp, Ray ray, int level, Double3 k) {
        Color color = scene.ambientLight.getIntensity()
                .add(calcLocalEffects(gp, ray.getDirection()));
        return 1 == level ? color : color.add(calcGlobalEffects(gp, ray, level, k));
    }

    /**
     * Calculates the color at the given point.
     * This method currently returns the background color of the scene.
     *
     * @param gp  the point at which the color is to be calculated
     * @param ray the ray that intersect with the gp
     * @return the color at the specified point
     */
    private Color calcColor(GeoPoint gp, Ray ray) {
        return calcColor(gp, ray, MAX_CALC_COLOR_LEVEL, INITIAL_K);
    }

    /**
     * Calculates the local effects of lighting at the given point.
     *
     * @param gp the point at which the local effects are to be calculated
     * @param v  the ray's direction vector intersecting with the gp
     * @return the color at the specified point including local lighting effects
     */
    private Color calcLocalEffects(GeoPoint gp, Vector v) {
        Vector n = gp.geometry.getNormal(gp.point);
        double nv = alignZero(n.dotProduct(v));
        if (nv == 0) return Color.BLACK;

        Material material = gp.geometry.getMaterial();
        Color color = gp.geometry.getEmission();

        for (LightSource lightSource : scene.lights) {
            Vector l = lightSource.getL(gp.point);
            double nl = alignZero(n.dotProduct(l));
            if ((nl * nv > 0) && unshaded(gp, lightSource, l, n, nl)) { // sign(nl) == sing(nv)
                Color iL = lightSource.getIntensity(gp.point);
                color = color.add(
                        iL.scale(calcDiffusive(material, nl)
                                .add(calcSpecular(material, n, l, nl, v))));
            }
        }
        return color;
    }

    private Color calcGlobalEffects(GeoPoint gp, Ray ray, int level, Double3 k) {
        Material material = gp.geometry.getMaterial();
        return calcGlobalEffect(constructRefractedRay(gp, ray), material.kT, level, k)
                .add(calcGlobalEffect(constructReflectedRay(gp, ray), material.kR, level, k));
    }

    private Color calcGlobalEffect(Ray ray, Double3 kx, int level, Double3 k) {
        Double3 kkx = kx.product(k);
        if (kkx.lowerThan(MIN_CALC_COLOR_K)) return Color.BLACK;
        GeoPoint gp = findClosestIntersection(ray);
        return (gp == null ? scene.background : calcColor(gp, ray, level - 1, kkx))
                .scale(kx);
    }

    private Ray constructRefractedRay(GeoPoint gp, Ray ray) {
        return new Ray(gp.point, ray.getDirection());
    }

    private Ray constructReflectedRay(GeoPoint gp, Ray ray) {
        var n = gp.geometry.getNormal(gp.point);
        var v = ray.getDirection();
        return new Ray(gp.point, v.subtract(n.scale(2 * v.dotProduct(n))));
    }

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
        double vr = Math.max(0, -v.dotProduct(r));
        return material.kS.scale(Math.pow(vr, material.shininess));
    }


    private boolean unshaded(GeoPoint gp, LightSource light, Vector l, Vector n, double nl) {

        Vector lightDirection = l.scale(-1); // from point to light source
        Vector epsVector = n.scale(nl < 0 ? DELTA : -DELTA);
        Point point = gp.point.add(epsVector);
        Ray ray = new Ray(point, lightDirection);
        List<GeoPoint> intersections = scene.geometries.findGeoIntersections(ray, light.getDistance(gp.point));
        if (intersections == null) return true;

        for (GeoPoint p : intersections) {
            if (p.geometry.getMaterial().kT.equals(Double3.ONE)) return false;
        }
        return true;
    }
}


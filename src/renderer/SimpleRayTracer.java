package renderer;

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
        var closestGeoPoint = ray.findClosestGeoPoint(scene.geometries.findGeoIntersections(ray));
        return (closestGeoPoint == null) ? scene.background : calcColor(closestGeoPoint, ray.getDirection());
    }

    /**
     * Calculates the color at the given point.
     * This method currently returns the background color of the scene.
     *
     * @param gp the point at which the color is to be calculated
     * @param v  the ray's direction vector intersect with the gp
     * @return the color at the specified point
     */
    private Color calcColor(GeoPoint gp, Vector v) {
        return scene.ambientLight.getIntensity()
                .add(calcLocalEffects(gp, v));
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
        List<Point> intersections = scene.geometries.findIntersections(ray, light.getDistance(gp.point));
        return intersections == null;
    }
}


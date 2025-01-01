package renderer;

import geometries.Plane;
import lighting.LightSource;
import primitives.*;
import scene.Scene;

import static utils.Util.compareSign;

/**
 * A ray marching renderer that calculates intersections with distance fields
 * and renders implicit surfaces using the ray marching technique.
 *
 * @author Benny Avrahami
 */
public class RayMarchingRenderer extends RayTracerBase {
    private static final double MIN_DISTANCE = 0.01; // Minimum distance to stop marching
    private static final double EPSILON = MIN_DISTANCE/10;
    private static final int MAX_STEPS = 500;          // Maximum number of marching steps
    private static final double MAX_MARCH_DISTANCE = 100000; // Maximum marching distance

    //  Vector normal = new Plane(new Point(-70, -40, 0), new Point(-40, -70, 0), new Point(-68, -68, -4)).getNormal();


    private final Material material = RenderSettings.RAY_MARCHING_GLOBAL_MATERIAL;

    /**
     * Constructs a RayMarchingRenderer for a given scene.
     *
     * @param scene The scene to render using ray marching.
     */
    public RayMarchingRenderer(Scene scene) {
        super(scene);
    }

    @Override
    public Color traceRay(Ray ray) {
        double travelDistance = 0.0;
        Point currentPoint = ray.getOrigin();

        for (int step = 0; step < MAX_STEPS; step++) {
            double distance = sceneDistance(currentPoint);

            if (distance < MIN_DISTANCE) {
                return shade(ray, currentPoint, step);
            }

            travelDistance += distance;
            if (travelDistance > MAX_MARCH_DISTANCE) break;

            currentPoint = ray.getPoint(travelDistance);
        }

        return scene.background; // Return background if no intersection
    }

    /**
     * Calculates the shortest distance to any object in the scene from the given point.
     *
     * @param point The point to evaluate.
     * @return The minimum distance to the scene objects.
     */
    private double sceneDistance(Point point) {
        return scene.geometries.sceneDistance(point);
    }

    /**
     * Shades a point on the surface using lighting and material properties.
     *
     * @param ray          The ray that hit the surface.
     * @param intersection The intersection point.
     * @param steps        The number of steps taken to reach the surface.
     * @return The shaded color at the intersection point.
     */
    private Color shade(Ray ray, Point intersection, int steps) {

        Vector normal = calculateNormal(intersection);
        Vector viewDirection = ray.getDirection();
        if (!RenderSettings.RAY_MARCHING_SIMPLE_SHADING) {
//            double curvature = curvature(intersection);
//            Color baseColor = new Color(160, 160, 160);
//            // Darken based on curvature
//            return baseColor.scale(1.0 - curvature);
             return new Color(200,200,200).scale(1.0 - (steps / (double) MAX_STEPS)).scale(Math.abs(normal.dotProduct(viewDirection)));

        }





        double nv = normal.dotProduct(viewDirection);

        if (nv == 0) return Color.BLACK;
        return applyLighting(intersection, normal, ray, nv)
                .add(scene.ambientLight.getIntensity());
    }

    /**
     * Calculates the normal vector at a given point on the surface using gradient estimation.
     *
     * @param point The point on the surface.
     * @return The normal vector.
     */
    private Vector calculateNormal(Point point) {
        double distance = sceneDistance(point);
        double dx = sceneDistance(point.add(Vector.UNIT_X.scale(EPSILON))) - distance;
        double dy = sceneDistance(point.add(Vector.UNIT_Y.scale(EPSILON))) - distance;
        double dz = sceneDistance(point.add(Vector.UNIT_Z.scale(EPSILON))) - distance;
        return new Vector(dx, dy, dz).normalize();
    }

    double curvature(Point p) {
        double d = sceneDistance(p);
        double ddx = sceneDistance(p.add(Vector.UNIT_X.scale(EPSILON))) - d;
        double ddy = sceneDistance(p.add(Vector.UNIT_Y.scale(EPSILON))) - d;
        double ddz = sceneDistance(p.add(Vector.UNIT_Z.scale(EPSILON))) - d;
        return Math.sqrt(ddx * ddx + ddy * ddy + ddz * ddz);
    }


    /**
     * Applies lighting effects to a point on the surface.
     *
     * @param point      The point to shade.
     * @param normal     The normal vector at the point.
     * @param primaryRay The primary ray hitting the surface.
     * @param nv         Dot product of a normal and view direction.
     * @return The shaded color at the point.
     */
    private Color applyLighting(Point point, Vector normal, Ray primaryRay, double nv) {
        Color totalColor = Color.BLACK;

        for (LightSource lightSource : scene.lights) {
            Point[] lightSamples = lightSource.getSamplePoints();
            Color sampleColor = Color.BLACK;

            for (Point lightPoint : lightSamples) {
                Vector lightDirection = lightSource.computeDirection(point, lightPoint);
                double nl = normal.dotProduct(lightDirection);

                if (compareSign(nl, nv)) {
                    Ray shadowRay = new Ray(point, lightSource.computeDirection(point, lightPoint).scale(-1), normal);
                    double shadowFactor = calculateShadow(shadowRay, Math.min( point.distance(lightPoint), MAX_MARCH_DISTANCE));
                    //double shadowFactor = calculateAmbientOcclusion(shadowRay.getOrigin(), shadowRay.getDirection(), 20, point.distance(lightPoint));

                    if (shadowFactor > 0) {
                        Double3 diffuse = calculateDiffuse(nl);
                        Double3 specular = calculateSpecular(normal, lightDirection, nl, primaryRay.getDirection());
                        Color lightContribution = lightSource.computeIntensity(point, lightPoint);

                        sampleColor = sampleColor.add(
                                lightContribution.scale((specular).add(diffuse)).scale(shadowFactor));
                    }
                }
            }

            totalColor = totalColor.add(sampleColor.reduce(lightSamples.length));
        }

        return totalColor;
    }

    /**
     * Calculates the shadow factor for a given shadow ray.
     *
     * @param shadowRay       The ray cast toward the light source.
     * @param distanceToLight Distance to the light source.
     * @return A shadow factor between 0 (fully blocked) and 1 (fully lit).
     */
    protected double calculateShadow(Ray shadowRay, double distanceToLight) {
        double travelDistance = 0.0;
        Point currentPoint = shadowRay.getOrigin();

        for (int step = 0; step < MAX_STEPS; step++) {
            double distance = sceneDistance(currentPoint);

            if (distance < MIN_DISTANCE) {
                return 0.0; // Fully blocked
            }

            travelDistance += distance;
            if (travelDistance > distanceToLight) return 1.0; // Fully lit

            currentPoint = shadowRay.getPoint(travelDistance);
        }

        return 0; // Consider fully blocked if max steps are reached
    }

    /**
     * Calculates the diffuse reflection based on the material and light angle.
     *
     * @param nl Dot product of a normal and light direction.
     * @return Diffuse reflection coefficient.
     */
    protected Double3 calculateDiffuse(double nl) {
        return material.kD.scale(Math.abs(nl));
    }

    /**
     * Calculates the specular reflection based on the material, normal, and light direction.
     *
     * @param normal         Normal vector at the surface.
     * @param lightDirection Direction of incoming light.
     * @param nl             Dot product of a normal and light direction.
     * @param viewDirection  Direction of view.
     * @return Specular reflection coefficient.
     */
    protected Double3 calculateSpecular(Vector normal, Vector lightDirection, double nl, Vector viewDirection) {
        Vector reflection = lightDirection.subtract(normal.scale(2 * nl));
        double minusVR = -viewDirection.dotProduct(reflection);


        return minusVR <= 0 ? Double3.ZERO :
                material.kS.scale(Math.pow(minusVR, 30));
    }


    /**
     * Calculates ambient occlusion at a point based on the surrounding geometry.
     *
     * @param point        The point on the surface.
     * @param normal       The normal vector at the point.
     * @param steps        The number of samples for ambient occlusion.
     * @param sampleRadius The radius for AO sampling.
     * @return The ambient occlusion factor (0.0 to 1.0).
     */
    private double calculateAmbientOcclusion(Point point, Vector normal, int steps, double sampleRadius) {
        double occlusion = 0.0;
        double stepSize = sampleRadius / steps;

        for (int i = 1; i <= steps; i++) {
            Point samplePoint = point.add(normal.scale(stepSize * i));
            double distance = sceneDistance(samplePoint);

            var a = Math.max(0, stepSize - distance);

            occlusion += a;
        }

        // Normalize AO to range [0, 1]
        return 1.0 - Math.min(1.0, occlusion * 10 / sampleRadius);
    }

}

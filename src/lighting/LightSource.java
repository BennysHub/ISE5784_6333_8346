package lighting;

import primitives.*;

import java.util.List;

/**
 * Interface representing a light source in a 3D scene.
 * Provides methods to get the intensity and direction of the light at a given point.
 */
public interface LightSource {

    /**
     * Gets the intensity of the light at a specific point.
     *
     * @param p the point at which the light intensity is to be calculated
     * @return the color representing the light intensity at the given point
     */
    Color getIntensity(Point p);

    /**
     * Gets the vector from the light source to a specific point.
     *
     * @param p the point at which the vector is to be calculated
     * @return the vector from the light source to the given point
     */
    Vector getL(Point p);

    /**
     * Generates a beam of rays from the given point and normal.
     *
     * @param p         The target point.
     * @param n         The normal vector at the target point.
     * @param numOfRays The number of rays to generate.
     * @return A list of rays forming a beam.
     */
    List<Ray> getRaysBeam(Point p, Vector n, int numOfRays);


    /**
     * Calculate the distance between the light source and a point
     *
     * @param point the point witch we refer to
     * @return the distance between the position and the given point
     */
    double getDistance(Point point);

    List<Point> findExtreme(Vector vector);
}

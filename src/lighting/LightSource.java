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
     * Generates a beam of rays from the given point and normal.
     *
     * @param p         The target point.
     * @param numOfRays The number of rays to generate.
     * @return A list of rays forming a beam.
     */
    List<Ray> getRaysBeam(Point p, int numOfRays);

    List<Point>getLightSample(Point p, int samplesCount);

    Vector getL(Point to, Point lightPoint);

}

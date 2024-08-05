package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

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
     * Calculate the distance between the light source and a point
     *
     * @param point the point witch we refer to
     * @return the distance between the position and the given point
     */
    double getDistance(Point point);
}

package lighting;

import primitives.*;

import java.util.List;

/**
 * Interface representing a light source in a 3D scene.
 * Provides methods to get the intensity and direction of the light at a given point.
 */
public interface LightSource {

    Color getIntensity(Point p, Point lightPoint);

    Point[] getLightSample(Point p, int samplesCount);

    Vector getL(Point to, Point lightPoint);

}

package lighting;

import primitives.*;
import renderer.QualityLevel;

/**
 * Interface representing a light source in a 3D scene.
 * Provides methods to get the intensity and direction of the light at a given point.
 */
public interface LightSource {

    void setLightSample(QualityLevel sampleCount);

    Color getIntensity(Point p, Point lightPoint);

    Point[] getLightSample(Point p);

    Vector getL(Point to, Point lightPoint);

}

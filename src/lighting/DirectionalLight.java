package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class DirectionalLight extends Light implements LightSource {
    private final Vector direction;

    public DirectionalLight(Color intensity, Vector direction) {
        super(intensity);
        this.direction = direction;
    }

    public Color getIntensity(Point p) {
        return intensity;
    }

    public Vector getL(Point p) {
        return direction.normalize();
    }

}

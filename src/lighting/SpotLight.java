package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static java.lang.Math.max;

public class SpotLight extends PointLight {
    private final Vector direction;

    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction;
    }

    @Override
    public PointLight setKc(double kc) {
        super.setKc(kc);
        return (SpotLight) this;
    }

    @Override
    public PointLight setKl(double kl) {
        super.setKl(kl);
        return (SpotLight) this;
    }

    @Override
    public PointLight setKq(double kq) {
        super.setKq(kq);
        return (SpotLight) this;
    }

    @Override
    public Color getIntensity(Point p) {
        return super.getIntensity(p).scale(max(0, direction.dotProduct(getL(p))));
    }
}

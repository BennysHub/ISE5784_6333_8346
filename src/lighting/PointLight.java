package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {
    protected final Point position;
    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;

    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    public PointLight setKc(double kc) {
        this.kC = kc;
        return this;
    }

    public PointLight setKl(double kl) {
        this.kL = kl;
        return this;
    }

    public PointLight setKq(double kq) {
        this.kQ = kq;
        return this;
    }

    public Color getIntensity(Point p) {
        final double d = p.distance(position);
        return intensity.reduce((int) (kC + (kL * d) + (kQ * d * d)));
    }

    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }
}

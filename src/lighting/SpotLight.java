package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

import static java.lang.Math.max;

/**
 * Class representing a spotlight in a 3D scene.
 * A spotlight is a point light with a specific direction and intensity that decreases with distance and angle.
 */
public class SpotLight extends PointLight {
    private final Vector direction;

    private int beamFocus = 1;

    /**
     * Constructs a spotlight with the specified intensity, position, and direction.
     *
     * @param intensity the color representing the light intensity
     * @param position  the position of the light source
     * @param direction the direction of the light
     */
    public SpotLight(Color intensity, Point position, Vector direction) {
        super(intensity, position);
        this.direction = direction.normalize();
    }


//    @Override
//    public PointLight setKc(double kc) {
//        super.setKc(kc);
//        return (SpotLight) this;
//    }
//
//    @Override
//    public PointLight setKl(double kl) {
//        super.setKl(kl);
//        return (SpotLight) this;
//    }
//
//    @Override
//    public PointLight setKq(double kq) {
//        super.setKq(kq);
//        return (SpotLight) this;
//    }

    /**
     * Sets the beamFocus for the spotLight direction
     *
     * @param beamFocus the strength of the beam will be more focus
     * @return the current PointLight instance for chaining
     */
    public PointLight setNarrowBeam(int beamFocus){
        this.beamFocus = beamFocus;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        double DirectionStrength =  direction.dotProduct(getL(p));
        return super.getIntensity(p).scale(DirectionStrength <= 0 ? 0 : Math.pow(DirectionStrength, beamFocus));
    }
}

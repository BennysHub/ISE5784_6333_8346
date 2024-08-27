package lighting;

import primitives.Color;
import primitives.Double3;
import primitives.Point;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.sqrt;
import static primitives.Util.isZero;
import static primitives.Util.random;

/**
 * Class representing a point light source in a 3D scene.
 * A point light has a position and its intensity decreases with distance.
 */
public class PointLight extends Light implements LightSource {
    /**
     * The position of the light source.
     */
    protected final Point position;

    private double size = 0d;

    private double kC = 1;
    private double kL = 0;
    private double kQ = 0;

    /**
     * Constructs a point light with the specified intensity and position.
     *
     * @param intensity the color representing the light intensity
     * @param position  the position of the light source
     */
    public PointLight(Color intensity, Point position) {
        super(intensity);
        this.position = position;
    }

    /**
     * Sets the constant attenuation factor.
     *
     * @param kc the constant attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKc(double kc) {
        this.kC = kc;
        return this;
    }

    /**
     * Sets the size/radius of the light.
     *
     * @param size the size/radius of the light
     * @return the current PointLight instance for chaining
     */
    public PointLight setSize(double size) {
        this.size = size;
        return this;
    }

    /**
     * Sets the linear attenuation factor.
     *
     * @param kl the linear attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKl(double kl) {
        this.kL = kl;
        return this;
    }

    /**
     * Sets the quadratic attenuation factor.
     *
     * @param kq the quadratic attenuation factor
     * @return the current PointLight instance for chaining
     */
    public PointLight setKq(double kq) {
        this.kQ = kq;
        return this;
    }

    @Override
    public Color getIntensity(Point p) {
        final double d = p.distance(position);
        return intensity.reduce(kC + (kL * d) + (kQ * d * d));
    }

    @Override
    public Vector getL(Point p) {
        return p.subtract(position).normalize();
    }

    @Override
    public double getDistance(Point point) {
        return point.distance(position);
    }

    //return multiple vector from the circle defined in sphere by center = position, plane containing = normal witch is getL(p) and a center point, and radius = size
    public List<Vector> multipleVectorsFromLights(Point p, int numOfVectors) {
        Vector normal = getL(p);
        if (isZero(size) || numOfVectors == 0)
            return List.of(normal);

        //make sure Vector(0,0,1) is not parallel to normal
        Vector orthogonalToNormal;
        try {
            orthogonalToNormal = new Vector(0, 0, 1).crossProduct(normal).normalize();
        } catch (IllegalArgumentException ignore) {
            orthogonalToNormal = new Vector(0, 1, 0).crossProduct(normal).normalize();
        }
        Vector orthogonalToBoth = orthogonalToNormal.crossProduct(normal);//already normalized

        List<Vector> VectorsFromDifferentParts = new LinkedList<>();
        //VectorsFromDifferentParts.add(normal);

//        jittered grid
        int dotsPerAxis = (int) sqrt(numOfVectors);
        double gridDistance = size * 2 / dotsPerAxis;

        for (int i = 0; i < dotsPerAxis; i++) {
            for (int j = 0; j < dotsPerAxis; j++) {
                double x = i * gridDistance;
                double y = j * gridDistance;

//                x += random(-gridDistance, gridDistance);
//                y += random(-gridDistance, gridDistance);
                double distance = sqrt(x * x + y * y);

//                System.out.print("(" + x + "," + y + ") ");

                if (distance <= size)
                    VectorsFromDifferentParts.add(p.subtract(
                            position.add(orthogonalToNormal.scale(x))
                                    .add(orthogonalToBoth.scale(y))));
            }
//            System.out.println();
        }

        //return only the edges can be adjusted by size = radius,
        for (int i = 0; i < numOfVectors; i++) {
            double theta = 2 * Math.PI * i / numOfVectors;
            Point pointOnTheCircle = position;//.add(orthogonalToNormal.scale(size * Math.cos(theta)).add(orthogonalToBoth.scale(size * Math.sin(theta))));
            double radius = size;//(size/10,size);
            if (!isZero(radius * Math.cos(theta)))
                pointOnTheCircle = pointOnTheCircle.add(orthogonalToNormal.scale(radius * Math.cos(theta)));
            if (!isZero(radius * Math.sin(theta)))
                pointOnTheCircle = pointOnTheCircle.add(orthogonalToBoth.scale(radius * Math.sin(theta)));

            VectorsFromDifferentParts.add(p.subtract(pointOnTheCircle).normalize());//normalized?
        }
        return VectorsFromDifferentParts;
    }
}

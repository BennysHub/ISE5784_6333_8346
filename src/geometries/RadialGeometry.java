package geometries;

/**
 * An abstract class for geometric objects that have a radial dimension.
 * This class serves as a base for all geometries that are defined by a radius.
 *
 * @author Benny Avrahami
 */
public abstract class RadialGeometry implements Geometry {
    /**
     * The radius of the geometry.
     */
    protected final double radius;
    /**
     * The radius of the geometry.
     */
    protected final double radiusSquared;

    /**
     * Constructs a RadialGeometry object with the specified radius.
     *
     * @param radius The radius of the geometry.
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

}

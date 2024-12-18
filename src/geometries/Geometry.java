package geometries;

import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * An abstract base class for geometric objects in 3D space.
 * <p>
 * This class provides the foundation for all geometric shapes, offering methods for
 * retrieving and manipulating the material properties, as well as calculating the normal
 * vector at a given point on the geometry.
 * It also defines abstract methods for geometric
 * transformations such as translation, scaling, and rotation.
 * </p>
 *
 * <p>Subclasses are expected to implement these abstract methods to define their specific behavior.</p>
 *
 * @author Benny Avrahami
 */
public abstract class Geometry extends Intersectable {

    /**
     * The material properties of the geometry.
     * Defines properties like reflectivity, transparency, and shininess.
     */
    private Material material = new Material();

    /**
     * Retrieves the material properties of the geometry.
     *
     * @return the material properties of the geometry.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties of the geometry.
     *
     * @param material the new material properties to apply to the geometry.
     * @return the current {@code Geometry} object, enabling method chaining.
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at the specified point.
     * <p>
     * The normal vector is perpendicular to the surface of the geometry at the given point
     * and is used in shading calculations, collision detection, and other geometric operations.
     * </p>
     *
     * @param point the point on the geometry where the normal vector is to be calculated.
     * @return the normal vector at the specified point on the geometry.
     */
    public abstract Vector getNormal(Point point);


    @Override
    public final Geometry translate(Vector translationVector){
        return translateHelper(translationVector).setMaterial(material);
    }

    @Override
    public final Geometry rotate(Vector axis, double angleInRadians){
        return rotateHelper(axis, angleInRadians).setMaterial(material);
    }

    @Override
    public final  Geometry scale(Vector scale){
        return scaleHelper(scale).setMaterial(material);
    }

    @Override
    public Geometry scale(double scale) {
        return scale(new Vector(scale, scale, scale));
    }

    @Override
    public final Geometry translateX(double dx) {
        return translate(new Vector(dx, 0, 0));
    }

    @Override
    public final Geometry translateY(double dy) {
        return translate(new Vector(0, dy, 0));
    }

    @Override
    public final Geometry translateZ(double dz) {
        return translate(new Vector(0, 0, dz));
    }


    protected abstract Geometry translateHelper(Vector translationVector);

    protected abstract Geometry rotateHelper(Vector axis, double angleInRadians);

    protected abstract Geometry scaleHelper(Vector scale);
}

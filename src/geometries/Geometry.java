package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * An abstract class for basic geometric objects.
 * It can provide a normal vector at a given point.
 * This is a fundamental operation for geometric shapes in 3D space.
 *
 * <p>This class serves as a base class for specific geometric shapes.</p>
 *
 * @author Benny Avrahami
 */
public abstract class Geometry extends Intersectable {

    /**
     * The emission color of the geometry, default is black.
     */
    protected Color emission = Color.BLACK;

    private Material material = new Material();

    /**
     * Gets the emission color of the geometry.
     *
     * @return the emission color of the geometry
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Gets the material properties of the geometry.
     *
     * @return the material properties of the geometry
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the material properties of the geometry.
     *
     * @param material the new material properties
     * @return the current Geometry object, for method chaining
     */
    public Geometry setMaterial(Material material) {
        this.material = material;
        return this;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission the new emission color
     * @return the current Geometry object, for method chaining
     */
    public Geometry setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Calculates the normal vector to the geometry at the specified point.
     *
     * @param point the point on the geometry where the normal is to be calculated
     * @return the normal vector at the specified point on the geometry
     */
    public abstract Vector getNormal(Point point);
}

package geometries;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * An interface for basic geometric objects.
 * It can provide a normal vector at a given point.
 * This is a fundamental operation for geometric shapes in 3D space.
 *
 * @author Benny Avrahami
 */
public abstract class Geometry extends Intersectable {

    /**
     * The emission color of the geometry, default is black
     */
    protected Color emission = Color.BLACK;

    /**
     * Gets the emission color of the geometry.
     *
     * @return the emission color of the geometry
     */
    public Color getEmission() {
        return emission;
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
     * @param point The point on the geometry where the normal is to be calculated.
     * @return The normal vector at the specified point on the geometry.
     */
    public abstract Vector getNormal(Point point);


}

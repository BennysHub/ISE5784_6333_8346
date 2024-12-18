package geometries;

import primitives.Vector;

/**
 * The {@code Transformable} interface defines transformation operations for geometric objects
 * in 3D space, such as translation, rotation, and scaling.
 *
 * <p>Implementing this interface allows geometric objects to be manipulated
 * in a scene by applying transformations relative to their local coordinate system.</p>
 *
 * <p>The interface also includes convenience methods for common transformations,
 * such as uniform scaling and axis-aligned translations.</p>
 *
 * @author
 * Benny Avrahami
 */
public interface Transformable {

    /**
     * Translates the geometry by a specified vector.
     *
     * @param translationVector The vector defining the translation direction and distance.
     * @return A new {@link Geometry} instance representing the translated geometry.
     */
    Geometry translate(Vector translationVector);

    /**
     * Rotates the geometry around a specified axis by a given angle.
     *
     * @param axis            The axis of rotation.
     * @param angleInRadians  The rotation angle in radians.
     * @return A new {@link Geometry} instance representing the rotated geometry.
     */
    Geometry rotate(Vector axis, double angleInRadians);

    /**
     * Scales the geometry by a specified vector.
     *
     * <p>The scaling is applied independently along each axis, as defined by the components of the vector.</p>
     *
     * @param scale The vector defining the scaling factors along the X, Y, and Z axes.
     * @return A new {@link Geometry} instance representing the scaled geometry.
     */
    Geometry scale(Vector scale);

    /**
     * Uniformly scales the geometry by a single factor along all axes.
     *
     * @param scale The uniform scaling factor.
     * @return A new {@link Geometry} instance representing the uniformly scaled geometry.
     */
    default Geometry scale(double scale) {
        return scale(new Vector(scale, scale, scale));
    }

    /**
     * Translates the geometry along the X-axis by a specified distance.
     *
     * @param dx The distance to translate along the X-axis.
     * @return A new {@link Geometry} instance representing the translated geometry.
     */
    default Geometry translateX(double dx) {
        return translate(new Vector(dx, 0, 0));
    }

    /**
     * Translates the geometry along the Y-axis by a specified distance.
     *
     * @param dy The distance to translate along the Y-axis.
     * @return A new {@link Geometry} instance representing the translated geometry.
     */
    default Geometry translateY(double dy) {
        return translate(new Vector(0, dy, 0));
    }

    /**
     * Translates the geometry along the Z-axis by a specified distance.
     *
     * @param dz The distance to translate along the Z-axis.
     * @return A new {@link Geometry} instance representing the translated geometry.
     */
    default Geometry translateZ(double dz) {
        return translate(new Vector(0, 0, dz));
    }
}

package geometries;

import primitives.Quaternion;
import primitives.Vector;

/**
 * The {@code Transformable} interface defines geometric transformations for 3D objects.
 * Implementing this interface enables applying operations such as translation, rotation, and scaling
 * to geometric shapes while ensuring immutability by returning new transformed instances.
 *
 * <p>In addition to primary transformation methods, the interface provides convenience methods
 * for uniform scaling and axis-aligned translations.</p>
 *
 * <p>Example Usage:</p>
 * <pre>{@code
 * Transformable geometry = new Sphere(1, new Point(0, 0, 0));
 * Transformable translated = geometry.translate(new Vector(1, 0, 0));
 * Transformable rotated = translated.rotate(new Vector(0, 1, 0), Math.PI / 2);
 * }</pre>
 *
 * @author Benny Avrahami
 */
public interface Transformable {

    /**
     * Translates the object by a specified vector.
     *
     * @param translationVector The vector defining the translation direction and magnitude.
     * @return A new instance of the transformed object.
     */
    Transformable translate(Vector translationVector);

    /**
     * Rotates the object around a specified axis by a given angle.
     *
     * @param axis           The vector representing the axis of rotation.
     * @param angleInRadians The angle of rotation, in radians.
     * @return A new instance of the rotated object.
     */
    default Transformable rotate(Vector axis, double angleInRadians) {
        return rotate(Quaternion.fromAxisAngle(axis, angleInRadians));
    }

    /**
     * Rotates the geometry using a quaternion rotation.
     *
     * @param rotation The quaternion representing the rotation.
     * @return A new {@link Transformable} instance representing the rotated geometry.
     */
    Transformable rotate(Quaternion rotation);

    /**
     * Scales the object by specified factors along each axis.
     *
     * <p>The scaling factors are provided as a {@link Vector}, where each component defines
     * the scaling magnitude for its corresponding axis (X, Y, Z).</p>
     *
     * @param scale The vector defining the scaling factors along each axis.
     * @return A new instance of the scaled object.
     */
    Transformable scale(Vector scale);

    /**
     * Uniformly scales the object by a single factor along all axes.
     *
     * @param scaleFactor The uniform scaling factor to apply.
     * @return A new instance of the uniformly scaled object.
     */
    default Transformable scale(double scaleFactor) {
        return scale(new Vector(scaleFactor, scaleFactor, scaleFactor));
    }

    /**
     * Translates the object along the X-axis by a specified distance.
     *
     * @param distanceX The distance to translate along the X-axis.
     * @return A new instance of the translated object.
     */
    default Transformable translateX(double distanceX) {
        return translate(new Vector(distanceX, 0, 0));
    }

    /**
     * Translates the object along the Y-axis by a specified distance.
     *
     * @param distanceY The distance to translate along the Y-axis.
     * @return A new instance of the translated object.
     */
    default Transformable translateY(double distanceY) {
        return translate(new Vector(0, distanceY, 0));
    }

    /**
     * Translates the object along the Z-axis by a specified distance.
     *
     * @param distanceZ The distance to translate along the Z-axis.
     * @return A new instance of the translated object.
     */
    default Transformable translateZ(double distanceZ) {
        return translate(new Vector(0, 0, distanceZ));
    }
}

package primitives;

/**
 * A class representing a Quaternion, primarily used for rotations in 3D space.
 * Quaternions avoid gimbal lock and provide smooth interpolation for rotations.
 */
public class Quaternion {
    private final double w; // Scalar part
    private final double x; // X component of the vector part
    private final double y; // Y component of the vector part
    private final double z; // Z component of the vector part

    /**
     * Constructs a quaternion with the given components.
     *
     * @param w The scalar part.
     * @param x The x-component of the vector part.
     * @param y The y-component of the vector part.
     * @param z The z-component of the vector part.
     */
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a quaternion representing a rotation about a given axis by an angle.
     *
     * @param axis  The axis of rotation (must be normalized).
     * @param angle The angle of rotation in radians.
     * @return A quaternion representing the rotation.
     */
    public static Quaternion fromAxisAngle(Vector axis, double angle) {
        Vector normalizedAxis = axis.normalize();
        double sinHalfAngle = Math.sin(angle / 2);
        return new Quaternion(
                Math.cos(angle / 2),
                normalizedAxis.getX() * sinHalfAngle,
                normalizedAxis.getY() * sinHalfAngle,
                normalizedAxis.getZ() * sinHalfAngle
        );
    }

    /**
     * Multiplies this quaternion by another quaternion.
     *
     * @param other The quaternion to multiply with.
     * @return The resulting quaternion.
     */
    public Quaternion multiply(Quaternion other) {
        double newW = w * other.w - x * other.x - y * other.y - z * other.z;
        double newX = w * other.x + x * other.w + y * other.z - z * other.y;
        double newY = w * other.y - x * other.z + y * other.w + z * other.x;
        double newZ = w * other.z + x * other.y - y * other.x + z * other.w;
        return new Quaternion(newW, newX, newY, newZ);
    }

    /**
     * Rotates a vector using this quaternion.
     *
     * @param vector The vector to rotate.
     * @return The rotated vector.
     */
    public Vector rotate(Point vector) {
        Quaternion vectorQuaternion = new Quaternion(0, vector.getX(), vector.getY(), vector.getZ());
        Quaternion result = this.multiply(vectorQuaternion).multiply(this.conjugate());
        return new Vector(result.x, result.y, result.z);
    }

    /**
     * Returns the conjugate of this quaternion.
     *
     * @return The conjugate quaternion.
     */
    public Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }

    /**
     * Normalizes the quaternion to make it a unit quaternion.
     *
     * @return The normalized quaternion.
     */
    public Quaternion normalize() {
        double magnitude = Math.sqrt(w * w + x * x + y * y + z * z);
        return new Quaternion(w / magnitude, x / magnitude, y / magnitude, z / magnitude);
    }

    /**
     * Converts this quaternion to a 3x3 rotation matrix.
     *
     * @return A 3x3 rotation matrix representing the same rotation as this quaternion.
     */
    public Matrix toRotationMatrix() {
        double xx = x * x, yy = y * y, zz = z * z;
        double xy = x * y, xz = x * z, yz = y * z;
        double wx = w * x, wy = w * y, wz = w * z;

        double[][] rotationData = {
                {1 - 2 * (yy + zz), 2 * (xy - wz), 2 * (xz + wy)},
                {2 * (xy + wz), 1 - 2 * (xx + zz), 2 * (yz - wx)},
                {2 * (xz - wy), 2 * (yz + wx), 1 - 2 * (xx + yy)}
        };

        return new Matrix(rotationData);
    }


    @Override
    public String toString() {
        return String.format("Quaternion{w=%.3f, x=%.3f, y=%.3f, z=%.3f}", w, x, y, z);
    }

    // Getters for components
    public double getW() { return w; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}

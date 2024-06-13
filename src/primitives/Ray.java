package primitives;

/**
 * Represents a ray in 3D space, defined by a starting point (head) and a direction vector.
 * The direction vector is normalized to ensure it is a unit vector.
 *
 * @author Benny Avrahami
 */
public class Ray {
    /**
     * The starting point of the ray.
     */
    final private Point head;

    /**
     * The direction vector of the ray, normalized to be a unit vector.
     */
    final private Vector direction;

    /**
     * Constructs a new Ray with the specified starting point and direction.
     * The direction vector is normalized upon construction.
     *
     * @param point  The starting point of the ray.
     * @param vector The direction vector of the ray.
     */
    public Ray(Point point, Vector vector) {
        head = new Point(point.xyz);
        direction = new Vector(vector.xyz).normalize();
    }

    /**
     * Gets the head point of the ray.
     *
     * @return The head point of the ray.
     */
    public Point getHead() {
        return head;
    }

    /**
     * Gets the direction vector of the ray.
     *
     * @return The direction vector of the ray.
     */
    public Vector getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return head + direction.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other
                && head.equals(other.head)
                && direction.equals(other.direction);
    }
}


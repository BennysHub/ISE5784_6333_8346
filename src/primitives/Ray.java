package primitives;

import java.util.List;

import static primitives.Util.isZero;

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
        head = point;
        direction = vector.normalize();
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

    /**
     * The method calculates a point on the line of the ray, at the distance t
     *
     * @param t the distance from the head
     * @return new point on the line of the ray, at the distance t
     */
    public Point getPoint(double t) {
        return isZero(t) ? head : head.add(direction.scale(t));
    }

    /**
     * Finds the point in the given list that is closest to the ray's head.
     *
     * @param pointList the list of points to search through
     * @return the closest point to the 'head' point, or null if the list is null or empty
     */
    public Point findClosestPoint(List<Point> pointList) {
        if (pointList == null || pointList.isEmpty()) return null;

        Point closest = null;
        double shortestDistance = Double.MAX_VALUE;
        for (Point p : pointList) {
            double distance = p.distanceSquared(head);
            if (distance < shortestDistance) {
                closest = p;
                shortestDistance = distance;
            }
        }
        return closest;
    }


    @Override
    public String toString() {
        return String.format("Ray ➞ %s in direction %s", head, direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other
                && head.equals(other.head)
                && direction.equals(other.direction);
    }
}


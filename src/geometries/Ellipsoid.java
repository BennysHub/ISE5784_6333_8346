package geometries;

import primitives.Point;
import primitives.Quaternion;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * Represents an ellipsoid in 3D space, defined by its center and radii along orthogonal axes.
 * <p>
 * The ellipsoid can be translated, rotated, and scaled using geometric transformations.
 * </p>
 *
 * <p>An ellipsoid is defined mathematically as:
 * <code>(x/a)^2 + (y/b)^2 + (z/c)^2 = 1</code>,
 * where {@code a}, {@code b}, and {@code c} are the radii along the X, Y, and Z axes, respectively.</p>
 *
 * @author
 * Benny Avrahami
 */
public class Ellipsoid extends Geometry {
    private final Point center;
    private final Vector radii;

    /**
     * Constructs an ellipsoid with the specified center and radii along the X, Y, and Z axes.
     *
     * @param center The center of the ellipsoid.
     * @param radii  The radii along the X, Y, and Z axes.
     */
    public Ellipsoid(Point center, Vector radii) {
        this.center = center;
        this.radii = radii;
    }

    @Override
    protected void calculateAABBHelper() {
        // Compute the minimum and maximum points of the AABB
        Point min = center.subtract(radii);
        Point max = center.add(radii);
        this.aabb = new AABB(min, max);
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray, double maxDistance) {
        Vector dir = ray.getDirection();
        Point origin = ray.getOrigin();

        // Transform ray into the normalized ellipsoid space
        double dx = dir.getX() / radii.getX();
        double dy = dir.getY() / radii.getY();
        double dz = dir.getZ() / radii.getZ();

        double ox = origin.getX() / radii.getX();
        double oy = origin.getY() / radii.getY();
        double oz = origin.getZ() / radii.getZ();

        // Compute quadratic equation coefficients
        double a = dx * dx + dy * dy + dz * dz;
        double b = 2 * (ox * dx + oy * dy + oz * dz);
        double c = ox * ox + oy * oy + oz * oz - 1;

        // Solve quadratic equation
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0) {
            return null; // No intersection
        }

        double sqrtDiscriminant = Math.sqrt(discriminant);
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // Filter intersections by distance
        if (t1 > maxDistance || t2 < 0) {
            return null;
        }

        if (t2 <= maxDistance) {
            return t1 >= 0
                    ? List.of(new GeoPoint(this, ray.getPoint(t1)), new GeoPoint(this, ray.getPoint(t2)))
                    : List.of(new GeoPoint(this, ray.getPoint(t2)));
        } else {
            return t1 >= 0 ? List.of(new GeoPoint(this, ray.getPoint(t1))) : null;
        }
    }

    @Override
    protected Ellipsoid translateHelper(Vector translationVector) {
        // Return a new Ellipsoid translated by the given vector
        return new Ellipsoid(center.add(translationVector), radii);
    }

    @Override
    protected Geometry rotateHelper(Quaternion rotation) {
        Vector xAxis = new Vector(radii.getX(), 0, 0);
        Vector yAxis = new Vector(0, radii.getY(), 0);
        Vector zAxis = new Vector(0, 0, radii.getZ());

        Vector rotatedXAxis = rotation.rotate(xAxis);
        Vector rotatedYAxis = rotation.rotate(yAxis);
        Vector rotatedZAxis = rotation.rotate(zAxis);

        // Compute new radii based on the rotated axes
        Vector newRadii = new Vector(rotatedXAxis.length(), rotatedYAxis.length(), rotatedZAxis.length());
        return new Ellipsoid(center, newRadii);
    }

    @Override
    protected Ellipsoid scaleHelper(Vector scale) {
        // Scale the radii by the given factors
        Vector scaledRadii = new Vector(
                radii.getX() * scale.getX(),
                radii.getY() * scale.getY(),
                radii.getZ() * scale.getZ()
        );
        return new Ellipsoid(center, scaledRadii);
    }

    @Override
    public Vector getNormal(Point point) {
        // Transform the point into the ellipsoid's normalized space
        double nx = (point.getX() - center.getX()) / (radii.getX() * radii.getX());
        double ny = (point.getY() - center.getY()) / (radii.getY() * radii.getY());
        double nz = (point.getZ() - center.getZ()) / (radii.getZ() * radii.getZ());

        // Create and normalize the resulting vector
        return new Vector(nx, ny, nz).normalize();
    }

    @Override
    public String toString() {
        return String.format("Ellipsoid{center=%s, radii=%s}", center, radii);
    }

    public Vector getRadii() {
        return radii;
    }

    public Point getCenter() {
        return center;
    }
}

package geometries;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.List;

/**
 * An interface for geometric shapes that can be intersected by a ray.
 * Implementing classes will provide the logic to find intersection points.
 */
public abstract class Intersectable {

    public static class GeoPoint {
        public Geometry geometry;
        public Point point;

        public GeoPoint(Geometry geometry, Point point) {
            this.geometry = geometry;
            this.point = point;
        }

        @Override
        public String toString() {
            return String.format("GeoPoint", Geometry.class.getName());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            return obj instanceof GeoPoint other
                    && other.geometry == geometry
                    && other.point.equals(point);
        }

    }

    /**
     * Finds the intersection points of a given ray with this geometric shape.
     *
     * @param ray The ray to intersect with this geometric shape.
     * @return A list of intersection points, if any, between the ray and the geometric shape.
     */

    public final List<Point> findIntersections(Ray ray) {
        List<GeoPoint> geoList = findGeoIntersections(ray);
        return geoList == null ? null
                : geoList.stream().map(gp -> gp.point).toList();
    }


    protected abstract List<GeoPoint> findGeoIntersectionsHelper(Ray ray);

    public final List<GeoPoint> findGeoIntersections(Ray ray) {
        return findGeoIntersectionsHelper(ray);
    }
}

package geometries;

import primitives.Point;

/**
 * A record representing a geometric point of intersection.
 * <p>
 * Each {@code GeoPoint} contains the specific geometry associated with the intersection
 * and the 3D point where the intersection occurs.
 * </p>
 *
 * @param geometry the geometry associated with the intersection.
 * @param point    the intersection point in 3D space.
 */
public record GeoPoint(Geometry geometry, Point point) {

    /**
     * Returns a string representation of this {@code GeoPoint}.
     *
     * @return a string in the format "GeoPoint{geometry=..., point=...}".
     */
    @Override
    public String toString() {
        return String.format("GeoPoint{geometry=%s, point=%s}", geometry.getClass().getName(), point);
    }

    /**
     * Compares this {@code GeoPoint} with another object for equality.
     * Two {@code GeoPoint} objects are considered equal if their geometry and point are identical.
     *
     * @param obj the object to compare with this {@code GeoPoint}.
     * @return {@code true} if the specified object is equal to this {@code GeoPoint}, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Same object
        if (!(obj instanceof GeoPoint(Geometry otherGeometry, Point otherPoint))) return false; // Different type
        return geometry == otherGeometry && point.equals(otherPoint);
    }
}

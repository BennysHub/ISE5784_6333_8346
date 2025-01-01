package geometries;

import primitives.Material;
import primitives.Point;

public interface SignedDistance {
    double signedDistance(Point point);
    record SDFResult(double distance, Material material) { }
}

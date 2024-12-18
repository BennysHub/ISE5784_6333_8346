package geometries;

import primitives.Vector;

public interface Transformable {

    Intersectable translate(Vector translationVector);

    Intersectable rotate(Vector axis, double angleInRadians);

    Intersectable scale(Vector scale);



    default Intersectable scale(double scale) {
        return scale(new Vector(scale, scale, scale));
    }

    default Intersectable translateX(double dx) {
        return translate(new Vector(dx, 0, 0));
    }

    default Intersectable translateY(double dy) {
        return translate(new Vector(0, dy, 0));
    }

    default Intersectable translateZ(double dz) {
        return translate(new Vector(0, 0, dz));
    }
}

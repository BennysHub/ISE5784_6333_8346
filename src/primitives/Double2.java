package primitives;

import static primitives.Util.isZero;

public record Double2(double x, double y) {

    public static Double2 ZERO = new Double2(0,0);

    public Double2 scale(double number) {
        return new Double2(x * number, y * number);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Double2 other
                && isZero(x - other.x)
                && isZero(y - other.y);
    }
}

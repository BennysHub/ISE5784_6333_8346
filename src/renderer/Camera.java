package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * The {@code Camera} class represents a camera in a 3D scene.
 * It is defined by its location and orientation vectors (right, up, and to).
 * It can construct rays through a view plane for rendering purposes.
 *
 * @author TzviYisrael and Benny
 */
public class Camera implements Cloneable {
    private Point location;
    private Vector right, up, to;
    private double height = 0.0, width = 0.0, vpDistance = 0.0; //view plane distance;

    /**
     * Default constructor for {@code Camera}.
     */
    private Camera() {
    }

    /**
     * Returns a new {@code Builder} instance for {@code Camera}.
     *
     * @return a new {@code Builder} instance.
     */
    public static Builder getBuilder() {
        return null;
    }

    /**
     * Constructs a ray through a specific pixel in the view plane.
     *
     * @param nX the number of pixels in the x-axis.
     * @param nY the number of pixels in the y-axis.
     * @param j  the pixel index in the x-axis.
     * @param i  the pixel index in the y-axis.
     * @return the constructed {@code Ray}.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        return null;
    }

    /**
     * The {@code Builder} class for {@code Camera}.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point p) {
            //TODO: check p
            //if()
            //{throw new IllegalArgumentException()}
            camera.location = p;
            return this;
        }

        public Builder setDirection(Vector right, Vector up) {
            if (isZero(right.dotProduct(up))) {
                throw new IllegalArgumentException("the vectors not perpendicular");
            }
            camera.right = right.normalize();
            camera.up = up.normalize();
            return this;
        }

        public Builder setVpSize(double height, double width) {
            if (height < 0 || width < 0) {
                throw new IllegalArgumentException("the height and width are negative");
            }
            camera.height = height;
            camera.width = width;
            return this;
        }

        public Builder setVpDistance(double vpDistance) {
            if (vpDistance < 0) {
                throw new IllegalArgumentException("the vpDistance is negative");
            }
            camera.vpDistance = vpDistance;
            return this;
        }

//        public Camera build(){
//
//        }

    }
}

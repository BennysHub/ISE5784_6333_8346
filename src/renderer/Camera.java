package renderer;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.MissingResourceException;

import static primitives.Util.alignZero;
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
     * Constructs a ray through a specific pixel in the view plane.
     *
     * @param nX the number of pixels in the x-axis.
     * @param nY the number of pixels in the y-axis.
     * @param j  the pixel index in the x-axis.
     * @param i  the pixel index in the y-axis.
     * @return the constructed {@code Ray}.
     */
    public Ray constructRay(int nX, int nY, int j, int i) {
        final Point center = location.add(to.scale(vpDistance));
        final double ratioY = height / nY;
        final double ratioX = width / nX;

        final double yI = -(i - (double) (nY - 1) / 2) * ratioY;
        final double xJ = (j - (double) (nX - 1) / 2) * ratioX;

        Point pIJ = center;
        if (xJ != 0) pIJ = pIJ.add(right.scale(xJ));
        if (yI != 0) pIJ = pIJ.add(up.scale(yI));

        return new Ray(location, pIJ.subtract(location));
    }

    /**
     * Returns a new {@code Builder} instance for {@code Camera}.
     *
     * @return a new {@code Builder} instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * The {@code Builder} class for {@code Camera}.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        public Builder setLocation(Point p) {
            camera.location = p;
            return this;
        }

        public Builder setDirection(Vector right, Vector up) {
            if (!isZero(right.dotProduct(up))) {
                throw new IllegalArgumentException("the vectors not perpendicular");
            }
            camera.right = right.normalize();
            camera.up = up.normalize();
            return this;
        }

        public Builder setVpSize(double height, double width) {
            if (alignZero(height) <= 0 || alignZero(width) <= 0) {
                throw new IllegalArgumentException("the height and width are negative");
            }
            camera.height = height;
            camera.width = width;
            return this;
        }

        public Builder setVpDistance(double vpDistance) {
            if (alignZero(vpDistance) <= 0) {
                throw new IllegalArgumentException("the vpDistance is negative");
            }
            camera.vpDistance = vpDistance;
            return this;
        }

        /**
         * The build method makes sure the build was successful
         *
         * @return new Instant of camera
         */
        public Camera build() {
            if (camera.location == null) {
                throw new MissingResourceException("Missing camera location", "Camera", "location");
            }
            if (camera.right == null) {
                throw new MissingResourceException("Missing camera right vector", "Camera", "right");
            }
            if (camera.up == null) {
                throw new MissingResourceException("Missing camera up vector", "Camera", "up");
            }
            camera.to = camera.up.crossProduct(camera.right).normalize();
            if (camera.to == null) {
                throw new MissingResourceException("Missing camera to vector", "Camera", "to");
            }
            if (alignZero(camera.height) <= 0 || alignZero(camera.width) <= 0) {
                throw new MissingResourceException("Invalid view plane dimensions", "Camera", "height/width");
            }
            if (alignZero(camera.vpDistance) <= 0) {
                throw new MissingResourceException("Invalid view plane distance", "Camera", "vpDistance");
            }

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}


//public class Camera{
//    private Point location;
//    private Vector right, up, to;
//    private double height = 0.0, width = 0.0, vpDistance = 0.0; //view plane distance;
//
//    /**
//     * Default constructor for {@code Camera}.
//     */
//    private Camera(Builder builder) {
//        this.location = builder.location;
//        this.right = builder.right;
//        this.up = builder.up;
//        this.to = builder.to;
//        this.height = builder.height;
//        this.width = builder.width;
//        this.vpDistance = builder.vpDistance;
//    }
//
//    /**
//     * Returns a new {@code Builder} instance for {@code Camera}.
//     *
//     * @return a new {@code Builder} instance.
//     */
//    public static Builder getBuilder() {
//        return null;
//    }
//
//    /**
//     * Constructs a ray through a specific pixel in the view plane.
//     *
//     * @param nX the number of pixels in the x-axis.
//     * @param nY the number of pixels in the y-axis.
//     * @param j  the pixel index in the x-axis.
//     * @param i  the pixel index in the y-axis.
//     * @return the constructed {@code Ray}.
//     */
//    public Ray constructRay(int nX, int nY, int j, int i) {
//        return null;
//    }
//
//    /**
//     * The {@code Builder} class for {@code Camera}.
//     */
//    public static class Builder {
//        private Point location;
//        private Vector right, up, to;
//        private double height = 0.0, width = 0.0, vpDistance = 0.0; //view plane distance;
//
//        public Builder setLocation(Point p) {
//            //TODO: check p
//            //if()
//            //{throw new IllegalArgumentException()}
//            this.location = p;
//            return this;
//        }
//
//        public Builder setDirection(Vector right, Vector up) {
//            if (isZero(right.dotProduct(up))) {
//                throw new IllegalArgumentException("the vectors not perpendicular");
//            }
//            this.right = right.normalize();
//            this.up = up.normalize();
//            return this;
//        }
//
//        public Builder setVpSize(double height, double width) {
//            if (height < 0 || width < 0) {
//                throw new IllegalArgumentException("the height and width are negative");
//            }
//            this.height = height;
//            this.width = width;
//            return this;
//        }
//
//        public Builder setVpDistance(double vpDistance) {
//            if (vpDistance <= 0) {
//                throw new IllegalArgumentException("the vpDistance is negative");
//            }
//            this.vpDistance = vpDistance;
//            return this;
//        }
//
//        /**
//         * The build method makes sure the build was successful
//         * @return
//         * new Instant of camera
//         */
//        public Camera build()  {
//            if (this.location == null) {
//                throw new MissingResourceException("Missing camera location", "Camera", "location");
//            }
//            if (this.right == null) {
//                throw new MissingResourceException("Missing camera right vector", "Camera", "right");
//            }
//            if (this.up == null) {
//                throw new MissingResourceException("Missing camera up vector", "Camera", "up");
//            }
//            //setting 'to' vector,'to' is always vertical to up and right with length of 1
//            this.to = this.up.crossProduct(this.right).normalize();
//            if (this.to == null) {
//                throw new MissingResourceException("Missing camera to vector", "Camera", "to");
//            }
//            if (this.height <= 0 || this.width <= 0) {
//                throw new MissingResourceException("Missing view plane dimensions", "Camera", "height/width");
//            }
//            if (this.vpDistance <= 0) {
//                throw new MissingResourceException("Missing view plane distance", "Camera", "vpDistance");
//            }
//
//            return  new Camera(this);
//        }
//
//    }
//}

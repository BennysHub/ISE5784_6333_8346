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
     * Returns a new {@code Builder} instance for {@code Camera}.
     *
     * @return a new {@code Builder} instance.
     */
    public static Builder getBuilder() {
        return new Builder();
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
        // Calculate the center point of the view plane
        final Point center = location.add(to.scale(vpDistance));

        // Calculate the width and height ratios of a pixel
        final double ratioY = height / nY;
        final double ratioX = width / nX;

        // Calculate the pixel's position on the view plane
        final double yI = -(i - (double) (nY - 1) / 2) * ratioY;
        final double xJ = (j - (double) (nX - 1) / 2) * ratioX;

        // Starting from the center, move to the pixel's position
        Point pIJ = center;
        if (xJ != 0) pIJ = pIJ.add(right.scale(xJ));
        if (yI != 0) pIJ = pIJ.add(up.scale(yI));

        // Create the ray from the camera location to the pixel's position
        return new Ray(location, pIJ.subtract(location));
    }

    /**
     * The {@code Builder} class for {@code Camera}.
     * This class follows the Builder design pattern to provide a flexible solution for constructing a {@code Camera} object.
     */
    public static class Builder {
        private final Camera camera = new Camera();

        /**
         * Sets the location of the camera.
         *
         * @param p the point representing the location of the camera.
         * @return the current Builder object for chaining method calls.
         */
        public Builder setLocation(Point p) {
            camera.location = p;
            return this;
        }

        /**
         * Sets the direction of the camera based on the right and up vectors.
         * The vectors must be perpendicular to each other.
         *
         * @param right the right direction vector.
         * @param up    the up direction vector.
         * @return the current Builder object for chaining method calls.
         * @throws IllegalArgumentException if the vectors are not perpendicular.
         */
        public Builder setDirection(Vector right, Vector up) {
            if (!isZero(right.dotProduct(up))) {
                throw new IllegalArgumentException("the vectors are not perpendicular");
            }
            camera.right = right.normalize();
            camera.up = up.normalize();
            return this;
        }

        /**
         * Sets the view plane size of the camera.
         *
         * @param height the height of the view plane.
         * @param width  the width of the view plane.
         * @return the current Builder object for chaining method calls.
         * @throws IllegalArgumentException if the height or width are non-positive.
         */
        public Builder setVpSize(double height, double width) {
            if (alignZero(height) <= 0 || alignZero(width) <= 0) {
                throw new IllegalArgumentException("the height and width must be positive");
            }
            camera.height = height;
            camera.width = width;
            return this;
        }

        /**
         * Sets the distance of the view plane from the camera.
         *
         * @param vpDistance the distance of the view plane from the camera.
         * @return the current Builder object for chaining method calls.
         * @throws IllegalArgumentException if the view plane distance is non-positive.
         */
        public Builder setVpDistance(double vpDistance) {
            if (alignZero(vpDistance) <= 0) {
                throw new IllegalArgumentException("the view plane distance must be positive");
            }
            camera.vpDistance = vpDistance;
            return this;
        }

        /**
         * Constructs a new {@code Camera} instance using the parameters set in the {@code Builder}.
         * This method ensures that all required fields are properly set and that the camera's configuration is valid.
         *
         * @return a new instance of {@code Camera} with the configured parameters.
         * @throws MissingResourceException if any of the required fields are not set or are invalid.
         */
        public Camera build() {
            if (camera.location == null) {
                throw new MissingResourceException("Missing camera location", Camera.class.getName(), "location");
            }
            if (camera.right == null) {
                throw new MissingResourceException("Missing camera right vector", Camera.class.getName(), "right");
            }
            if (camera.up == null) {
                throw new MissingResourceException("Missing camera up vector", Camera.class.getName(), "up");
            }
            camera.to = camera.up.crossProduct(camera.right).normalize();
            if (camera.to == null) {
                throw new MissingResourceException("Missing camera 'to' vector", Camera.class.getName(), "to");
            }
            if (alignZero(camera.height) <= 0 || alignZero(camera.width) <= 0) {
                throw new MissingResourceException("Invalid view plane dimensions", Camera.class.getName(), "height/width");
            }
            if (alignZero(camera.vpDistance) <= 0) {
                throw new MissingResourceException("Invalid view plane distance", Camera.class.getName(), "vpDistance");
            }

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Failed to clone the camera object", e);
            }
        }

    }
}
package renderer;

import primitives.Color;
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
    private Vector right;
    private Vector up;
    private Vector to;
    private double vpHeight = 0.0;
    private double vpWidth = 0.0;
    private double vpDistance = 0.0; //view plane distance;
    private Point center; // viewing plane center point
    private ImageWriter imageWriter;
    private RayTracerBase rayTracerBase;

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

        // Calculate the width and height ratios of a pixel
        final double ratioY = vpHeight / nY;
        final double ratioX = vpWidth / nX;

        // Calculate the pixel's position on the view plane
        final double yI = -(i - (nY - 1) / 2d) * ratioY;
        final double xJ = (j - (nX - 1) / 2d) * ratioX;

        // Starting from the center, move to the pixel's position
        Point pIJ = center;
        if (xJ != 0) pIJ = pIJ.add(right.scale(xJ));
        if (yI != 0) pIJ = pIJ.add(up.scale(yI));

        // Create the ray from the camera location to the pixel's position
        return new Ray(location, pIJ.subtract(location));
    }

    /**
     * Renders the entire image by iterating through each pixel and casting a ray to determine the color.
     *
     * @return the Camera instance for method chaining
     */
    public Camera renderImage() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();
        for (int x = nX - 1; x >= 0; --x)
            for (int y = nY - 1; y >= 0; --y)
                castRay(nX, nY, x, y);
        return this;
    }

    /**
     * Casts a ray through the given pixel coordinates and writes the computed color to the image.
     *
     * @param nX the number of pixels in the x-direction
     * @param nY the number of pixels in the y-direction
     * @param x  the x-coordinate of the pixel
     * @param y  the y-coordinate of the pixel
     */
    private void castRay(int nX, int nY, int x, int y) {
        Ray ray = constructRay(nX, nY, x, y);
        Color color = rayTracerBase.traceRay(ray);
        imageWriter.writePixel(x, y, color);
    }

    /**
     * Prints a grid on the image with the specified interval and color.
     *
     * @param interval the spacing between grid lines
     * @param color    the color of the grid lines
     * @return the Camera instance for method chaining
     */
    public Camera printGrid(int interval, Color color) {

        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();
        for (int x = nX - 1; x >= 0; --x)
            for (int y = nY - 1; y >= 0; --y)
                if (x % interval == 0 || y % interval == 0)
                    imageWriter.writePixel(x, y, color);
        return this;
    }

    /**
     * Writes the image to a file.
     */
    public void writeToImage() {
        imageWriter.writeToImage();
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
         * @param to the direction vector.
         * @param up the up direction vector.
         * @return the current Builder object for chaining method calls.
         * @throws IllegalArgumentException if the vectors are not perpendicular.
         */
        public Builder setDirection(Vector to, Vector up) {
            if (!isZero(to.dotProduct(up))) {
                throw new IllegalArgumentException("the vectors are not perpendicular");
            }
            camera.to = to.normalize();
            camera.up = up.normalize();
            // Updating Vector to based on right, up vectors
            camera.right = camera.to.crossProduct(camera.up);
            return this;

        }

        /**
         * Sets the vectors of the camera, so it will point to the target point
         *
         * @param target the point the camera is directed towards.
         * @return the current Builder object for chaining method calls.
         */
        public Builder setTarget(Point target) {
            camera.to = target.subtract(camera.location).normalize();

            camera.up = new Vector(0, 1, 0); // The y-axis is up
            if (!isZero(camera.to.dotProduct(camera.up))) {
                camera.up = new Vector(0, 0, 1); // Switch to Z-axis if Vector to is (0, 1, 0)
            }
            camera.right = camera.to.crossProduct(camera.up).normalize();
            camera.up = camera.right.crossProduct(camera.to);
            return this;
        }

        /**
         * Rotates the 'right' and 'up' vectors of the camera by a given angle in degrees
         * in a clockwise direction.
         *
         * @param angle the angle by which to rotate the vectors, in degrees.
         * @return the current Builder object for chaining method calls.
         */
        public Builder rotateVectors(double angle) {
            double radians = Math.toRadians(angle); //convert to radians
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            if (isZero(cos)) {
                Vector newRight = camera.up.scale(-sin);
                Vector newUp = camera.right.scale(sin);
                camera.right = newRight.normalize();
                camera.up = newUp.normalize();
                return this;
            }
            if (isZero(sin)) {
                Vector newRight = camera.right.scale(cos);
                Vector newUp = camera.up.scale(cos);
                camera.right = newRight.normalize();
                camera.up = newUp.normalize();
                return this;
            }
            Vector newRight = camera.right.scale(cos)
                    .add(camera.up.scale(-sin));
            Vector newUp = camera.right.scale(sin)
                    .add(camera.up.scale(cos));
            camera.right = newRight.normalize();
            camera.up = newUp.normalize();
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
            camera.vpHeight = height;
            camera.vpWidth = width;
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
         * Sets the ImageWriter for the camera.
         *
         * @param imageWriter the ImageWriter to be set
         * @return the Builder instance for method chaining
         */
        public Builder setImageWriter(ImageWriter imageWriter) {
            camera.imageWriter = imageWriter;
            return this;
        }

        /**
         * Sets the RayTracerBase for the camera.
         *
         * @param rayTracer the RayTracerBase to be set
         * @return the Builder instance for method chaining
         */
        public Builder setRayTracer(RayTracerBase rayTracer) {
            camera.rayTracerBase = rayTracer;
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
            if (camera.location == null)
                throw new MissingResourceException("Missing camera location", Camera.class.getName(), "location");

            if (camera.right == null)
                throw new MissingResourceException("Missing camera right vector", Camera.class.getName(), "right");

            if (camera.up == null)
                throw new MissingResourceException("Missing camera up vector", Camera.class.getName(), "up");

            if (camera.to == null)
                throw new MissingResourceException("Missing camera 'to' vector", Camera.class.getName(), "to");

            if (!isZero(camera.to.dotProduct(camera.up)))
                throw new IllegalArgumentException("the vectors are not perpendicular");

            if (alignZero(camera.vpHeight) <= 0 || alignZero(camera.vpWidth) <= 0)
                throw new MissingResourceException("Invalid view plane dimensions", Camera.class.getName(), "height/width");

            if (alignZero(camera.vpDistance) <= 0)
                throw new MissingResourceException("Invalid view plane distance", Camera.class.getName(), "vpDistance");

            if (camera.imageWriter == null)
                throw new MissingResourceException("Missing camera imageWriter", Camera.class.getName(), "imageWriter");

            if (camera.rayTracerBase == null)
                throw new MissingResourceException("Missing camera rayTracerBase", Camera.class.getName(), "rayTracerBase");

            camera.center = camera.location.add(camera.to.scale(camera.vpDistance));

            if (camera.center == null)
                throw new MissingResourceException("Missing camera center", Camera.class.getName(), "center");

            try {
                return (Camera) camera.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError("Failed to clone the camera object", e);
            }
        }
    }
}
package renderer;

import geometries.Geometries;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.renderstrategies.antialiasingstrategies.DefaultColoring;
import renderer.renderstrategies.antialiasingstrategies.PixelColoringStrategy;
import renderer.renderstrategies.antialiasingstrategies.SuperSamplingAntiAliasing;
import scene.Scene;
import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;


/**
 * The {@code Camera} class represents a camera in a 3D scene.
 * It is defined by its location and orientation vectors (right, up, and to).
 * It can construct rays through a view plane for rendering purposes.
 *
 * @author TzviYisrael and Benny
 */
public class Camera {
    private final ImageWriter imageWriter;
    private final PixelColoringStrategy pixelColoringStrategy;

    /**
     * Pixel manager for supporting:
     * <ul>
     * <li>multi-threading</li>
     * <li>debug print of progress percentage in Console window/tab</li>
     * </ul>
     */
    private PixelManager pixelManager;

    /**
     * Default constructor for {@code Camera}.
     */
    private Camera(Builder cameraBuilder) {
        this.imageWriter = cameraBuilder.imageWriter;
        this.pixelColoringStrategy = cameraBuilder.pixelColoringStrategy;
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
     * Renders the entire image by iterating through each pixel and casting a ray to determine the color.
     *
     * @return the Camera instance for method chaining
     */
    public Camera renderImage() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();

        pixelManager = new PixelManager(nY, nX, 0);
        if (RenderSettings.threadsCount < 2) {
            for (int x = nX - 1; x >= 0; --x)
                for (int y = nY - 1; y >= 0; --y)
                    castRay(x, y);
        } else { // see further... option 2
            int threadsCount = RenderSettings.threadsCount;
            var threads = new LinkedList<Thread>(); // list of threads
            while (threadsCount-- > 0) // add the appropriate number of threads
                threads.add(new Thread(() -> { // add a thread with its code
                    PixelManager.Pixel pixel; // current pixel(row,col)
                    // allocate pixel(row,col) in loop until there are no more pixels
                    while ((pixel = pixelManager.nextPixel()) != null)
                        // cast ray through pixel (and color it – inside castRay)
                        castRay(pixel.col(), pixel.row());
                }));
            // start all the threads
            for (var thread : threads) thread.start();
            // wait until all the threads have finished
            try {
                for (var thread : threads) thread.join();
            } catch (InterruptedException ignore) {
            }
        }
//        else {
//            IntStream.range(0, nY).parallel()
//                    .forEach(i -> IntStream.range(0, nX).parallel() // for each row:
//                            .forEach(j -> castRay(j, i))); // for each column in row
//        }
        return this;
    }

    /**
     * Casts a ray through the given pixel coordinates and writes the computed color to the image.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     */
    private void castRay(int x, int y) {
        Color pixelColor = pixelColoringStrategy.calcalatePixelColor(x, y);
        imageWriter.writePixel(x, y, pixelColor);
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
                if (x % interval == 0 || y % interval == 0) imageWriter.writePixel(x, y, color);
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
        private Point location;
        private Vector right;
        private Vector up;
        private Vector to;
        private double vpHeight;
        private double vpWidth;
        private double vpDistance;
        private ImageWriter imageWriter;
        private RayTracerBase rayTracerBase;
        private PixelColoringStrategy pixelColoringStrategy;

        private boolean antiAliasingFlag = false;
        boolean rayTracerWasSet = false;


        /**
         * Sets the location of the camera.
         *
         * @param p the point representing the location of the camera.
         * @return the current Builder object for chaining method calls.
         */
        public Builder setLocation(Point p) {
            location = p;
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
            this.to = to.normalize();
            this.up = up.normalize();
            // Updating Vector to based on right, up vectors
            this.right = this.to.crossProduct(this.up);
            return this;

        }

        /**
         * Sets the vectors of the camera, so it will point to the target point
         *
         * @param target the point the camera is directed towards.
         * @return the current Builder object for chaining method calls.
         */
        public Builder setTarget(Point target) {
            this.to = target.subtract(this.location).normalize();

            this.up = new Vector(0, 1, 0); // The y-axis is up
//            if (!isZero(camera.to.dotProduct(camera.up))) {
            if (this.up.equals(this.to) || this.up.equals(this.to.scale(-1))) {
                this.up = new Vector(0, 0, 1); // Switch to Z-axis if Vector to is (0, 1, 0)
            }
            this.right = this.to.crossProduct(this.up).normalize();
            this.up = this.right.crossProduct(this.to);
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
            this.vpHeight = height;
            this.vpWidth = width;
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
            this.vpDistance = vpDistance;
            return this;
        }


        public Builder setResolution(String imageName, int nX, int nY) {
            this.imageWriter = new ImageWriter(imageName, nX, nY);
            return this;
        }

        /**
         * Enables or disables soft shadows in the render.
         *
         * @param flag a boolean indicating whether soft shadows should be enabled (true) or disabled (false).
         * @return the current Builder object for chaining method calls.
         */
        public Builder setSoftShadows(Boolean flag) {
            RenderSettings.softShadowsEnabled = flag;
            return this;
        }

        /**
         * Enables or disables BVH in the render.
         *
         * @param flag a boolean indicating whether BVH should be enabled (true) or disabled (false).
         * @return the current Builder object for chaining method calls.
         */
        public Builder setBVH(Boolean flag) {
            if (flag) {
                setCBR(true);
                this.rayTracerBase.scene.geometries.buildBVH();
            }
            RenderSettings.BVHIsEnabled = flag;
            return this;
        }

        public Builder setCBR(Boolean flag) {
            if (flag)
                this.rayTracerBase.scene.geometries.calculateAABB();
            RenderSettings.CBRIsEnabled = flag;
            return this;
        }


        public Builder setScene(Scene scene) {
            this.rayTracerBase = new SimpleRayTracer(scene);
            rayTracerWasSet = true;
            return this;
        }

        public Builder setMultiThreading(int threadsCount) {
            RenderSettings.threadsCount = threadsCount;
            return this;
        }

        public Builder duplicateScene(Vector vector) {
            if (rayTracerWasSet)
                throw new UnsupportedOperationException("there is no scene to duplicate");
            Geometries duplicate = (Geometries) this.rayTracerBase.scene.geometries.duplicateObject(vector);
            this.rayTracerBase.scene.geometries.add(duplicate);
            return this;
        }

        public Builder setAntiAliasing(boolean flag) {
            antiAliasingFlag = flag;
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
            if (this.location == null)
                throw new MissingResourceException("Missing camera location", Camera.class.getName(), "location");

            if (this.right == null)
                throw new MissingResourceException("Missing camera right vector", Camera.class.getName(), "right");

            if (this.up == null)
                throw new MissingResourceException("Missing camera up vector", Camera.class.getName(), "up");

            if (this.to == null)
                throw new MissingResourceException("Missing camera 'to' vector", Camera.class.getName(), "to");

            if (!isZero(this.to.dotProduct(this.up)))
                throw new IllegalArgumentException("the vectors are not perpendicular");

            if (alignZero(this.vpHeight) <= 0 || alignZero(this.vpWidth) <= 0)
                throw new MissingResourceException("Invalid view plane dimensions", Camera.class.getName(), "height/width");

            if (alignZero(this.vpDistance) <= 0)
                throw new MissingResourceException("Invalid view plane distance", Camera.class.getName(), "vpDistance");

            if (this.imageWriter == null)
                throw new MissingResourceException("Missing camera imageWriter", Camera.class.getName(), "imageWriter");

            if (this.rayTracerBase == null)
                throw new MissingResourceException("Missing camera rayTracerBase", Camera.class.getName(), "rayTracerBase");


            //after all, the checks above set by order!!
            //-----------1
            Point center = this.location.add(this.to.scale(this.vpDistance));
            //-----------2
            if (center == null)
                throw new MissingResourceException("Missing camera center", Camera.class.getName(), "center");
            //-----------3
            ViewPlane viewPlane = new ViewPlane(right, up, vpHeight, vpWidth, center, imageWriter.getNx(), imageWriter.getNy());
            //-----------4
            pixelColoringStrategy = antiAliasingFlag ? new SuperSamplingAntiAliasing(viewPlane, rayTracerBase, location) : new DefaultColoring(viewPlane, rayTracerBase, location);

            return new Camera(this);
        }
    }
}

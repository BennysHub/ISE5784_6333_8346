package renderer;

import geometries.Geometries;
import lighting.LightSource;
import primitives.Color;
import primitives.Matrix;
import primitives.Point;
import primitives.Vector;
import renderer.anti_aliasing_rendering.SSAA4X;
import renderer.anti_aliasing_rendering.SuperSamplingAntiAliasing;
import scene.Scene;

import java.util.MissingResourceException;

import static primitives.Util.alignZero;

/**
 * The {@code Camera} class represents a camera in a 3D scene.
 * It is defined by its position and orientation vectors (right, up, and forward).
 * The camera constructs rays through a view plane for rendering purposes.
 * <p>
 * This class follows the Builder design pattern for flexible and clear configuration.
 * </p>
 *
 * @author TzviYisrael and Benny
 */
public class Camera {
    private final Render renderEngine;

    /**
     * Constructs a {@code Camera} using the provided {@code Builder}.
     *
     * @param builder The {@code Builder} containing the camera configuration.
     */
    protected Camera(Builder builder) {
        this.renderEngine = builder.renderEngine;
    }


    /**
     * Creates a new {@code Builder} instance for configuring a {@code Camera}.
     *
     * @return a new {@code Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Writes the rendered image to a file.
     */
    public void writeToImage() {
        renderEngine.writeToImage();
    }

    /**
     * Prints a grid overlay on the image.
     *
     * @param interval The spacing between grid lines.
     * @param color    The color of the grid lines.
     * @return The current {@code Camera} instance for chaining.
     */
    public Camera printGrid(int interval, Color color) {
        renderEngine.printGrid(interval, color);
        return this;
    }

    /**
     * Renders the image using the configured render settings.
     *
     * @return The current {@code Camera} instance for chaining.
     */
    public Camera renderImage() {
        if (RenderSettings.parallelStreamsEnabled) {
            renderEngine.parallelStreamsRender();
        } else if (RenderSettings.multiThreadingEnabled) {
            renderEngine.multiThreadingRender(RenderSettings.threadsCount);
        } else {
            renderEngine.render();
        }
        return this;
    }

    /**
     * The {@code Builder} class for {@code Camera}.
     * This class follows the Builder design pattern to provide a flexible solution for constructing a {@code Camera} object.
     */
    public static class Builder {
        private Point position;
        private Vector right;
        private Vector up;
        private Vector forward;
        private double vpHeight;
        private double vpWidth;
        private double vpDistance;
        private ViewPlane viewPlane;
        private ImageWriter imageWriter;
        private int resolutionX;
        private int resolutionY;
        private String imageName;
        private RayTracerBase rayTracer;
        private Render renderEngine;
        private boolean rayTracerSet = false;
        private boolean bvhBuilt = false;
        private double apertureSize;
        private double focalLength;


        /**
         * Sets the location of the camera.
         *
         * @param p the point representing the location of the camera.
         * @return the current Builder object for chaining method calls.
         */
        public Builder setPosition(Point p) {
            position = p;
            return this;
        }

        /**
         * Sets the camera's orientation using forward and up vectors.
         * The vectors must be perpendicular.
         *
         * @param forward The forward-facing {@link Vector}.
         * @param up      The upward-facing {@link Vector}.
         * @return The current {@code Builder} instance for chaining.
         * @throws IllegalArgumentException If the vectors are not perpendicular.
         */
        public Builder setOrientation(Vector forward, Vector up) {
            if (!forward.isPerpendicular(up)) {
                throw new IllegalArgumentException("The forward and up vectors must be perpendicular.");
            }
            this.forward = forward.normalize();
            this.up = up.normalize();
            this.right = this.forward.crossProduct(this.up);
            return this;
        }

        /**
         * Sets the camera's orientation by targeting a specific point.
         *
         * @param target The target {@link Point} the camera should face.
         * @param up     The upward-facing {@link Vector}.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setOrientation(Point target, Vector up) {
            this.forward = target.subtract(this.position).normalize();

            if (up.isParallel(forward)) {
                throw new IllegalArgumentException("The 'up' vector cannot be parallel to the 'forward' vector.");
            }

            this.right = forward.crossProduct(up).normalize();
            this.up = right.crossProduct(forward);
            return this;
        }


        //set the camara 'to' vector based on camara location to target point,
        //the given 'up' vector is not necessary orthogonal to vector 'to'
        //so to get up vector that is orthogonal to 'to' we use vector rejection.
        public Builder setDirection_(Point target, Vector up) {
            forward = target.subtract(this.position).normalize();

            if (forward.isParallel(up))
                throw new IllegalArgumentException("Vector 'up' can't be parallel to vector 'to' ");

            this.up = up.reject(forward).normalize();
            right = forward.crossProduct(this.up);
            return this;
        }


        //changing camara location while keeping the camara pointing at a target
        public Builder keepInFocus(Point target, Point camaraLocation) {

            position = camaraLocation;
            Vector newTo = target.subtract(this.position).normalize();
            Vector axis = forward.isParallel(newTo) ? right : forward.crossProduct(newTo);

            double angle = Math.acos(forward.dotProduct(newTo) / (forward.length() * newTo.length()));
            Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);
            up = rotationMatrix.multiply(up);
            forward = newTo;
            right = forward.crossProduct(up);
            return this;
        }

        /**
         * Sets the view plane dimensions.
         *
         * @param height The height of the view plane.
         * @param width  The width of the view plane.
         * @return The current {@code Builder} instance for chaining.
         * @throws IllegalArgumentException If height or width is non-positive.
         */
        public Builder setViewPlaneSize(double height, double width) {
            if (alignZero(height) <= 0 || alignZero(width) <= 0) {
                throw new IllegalArgumentException("View plane dimensions must be positive.");
            }
            this.vpHeight = height;
            this.vpWidth = width;
            return this;
        }

        /**
         * Sets the view plane's distance from the camera.
         *
         * @param distance The distance of the view plane.
         * @return The current {@code Builder} instance for chaining.
         * @throws IllegalArgumentException If the distance is non-positive.
         */
        public Builder setViewPlaneDistance(double distance) {
            if (alignZero(distance) <= 0) {
                throw new IllegalArgumentException("View plane distance must be positive.");
            }
            this.vpDistance = distance;
            return this;
        }

        /**
         * Sets the image resolution.
         *
         * @param resolutionX The horizontal resolution (number of pixels).
         * @param resolutionY The vertical resolution (number of pixels).
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setResolution(int resolutionX, int resolutionY) {
            this.resolutionX = resolutionX;
            this.resolutionY = resolutionY;
            return this;
        }

        /**
         * Sets the name of the output image file.
         *
         * @param name The name of the image file.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setImageName(String name) {
            this.imageName = name;
            return this;
        }

        /**
         * Enables or disables soft shadows.
         *
         * @param enabled {@code true} to enable soft shadows, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableSoftShadows(boolean enabled) {
            RenderSettings.softShadowsEnabled = enabled;
            return this;
        }

        /**
         * Sets the quality of antialiasing.
         *
         * @param qualityLevel The desired {@link QualityLevel}.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setAntiAliasingQuality(QualityLevel qualityLevel) {
            RenderSettings.antiAliasingQuality = qualityLevel;
            return this;
        }

        /**
         * Enables or disables BVH acceleration.
         *
         * @param enabled {@code true} to enable BVH, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableBVH(boolean enabled) {
            enableCBR(enabled);//BVH use CBR
            RenderSettings.BVHIsEnabled = enabled;
            return this;
        }

        /**
         * Sets the aperture size for depth-of-field rendering.
         *
         * @param apertureSize The size of the aperture.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setApertureSize(double apertureSize) {
            this.apertureSize = apertureSize;
            return this;
        }

        /**
         * Sets the scene for the camera.
         *
         * @param scene The {@link Scene} to be rendered.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setScene(Scene scene) {
            this.rayTracer = new SimpleRayTracer(scene);
            this.rayTracerSet = true;
            return this;
        }

        /**
         * Sets the quality of depth-of-field rendering.
         *
         * @param qualityLevel The desired {@link QualityLevel}.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setDepthOfFieldQuality(QualityLevel qualityLevel) {
            RenderSettings.depthOfFieldQuality = qualityLevel;
            return this;
        }

        /**
         * Configures the camera to use Central Bounding Region (CBR) optimization.
         *
         * @param flag {@code true} to enable CBR optimization, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableCBR(boolean flag) {
            RenderSettings.CBRIsEnabled = flag;
            return this;
        }

        /**
         * Sets the number of threads to use for multithreaded rendering.
         *
         * @param threadsCount The number of threads to use.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setThreadsCount(int threadsCount) {
            RenderSettings.threadsCount = threadsCount;
            return this;
        }

        /**
         * Enables or disables multithreaded rendering.
         *
         * @param flag {@code true} to enable multi-threading, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableMultiThreading(boolean flag) {
            RenderSettings.multiThreadingEnabled = flag;
            return this;
        }

        /**
         * Enables or disables parallel stream rendering.
         *
         * @param flag {@code true} to enable parallel streams, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableParallelStreams(boolean flag) {
            RenderSettings.parallelStreamsEnabled = flag;
            return this;
        }

        /**
         * Enables or disables antialiasing for the render.
         *
         * @param flag {@code true} to enable anti-aliasing, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableAntiAliasing(boolean flag) {
            RenderSettings.antiAliasingEnabled = flag;
            return this;
        }

        /**
         * Enables or disables depth-of-field rendering.
         *
         * @param flag {@code true} to enable depth-of-field, {@code false} to disable.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder enableDepthOfField(boolean flag) {
            RenderSettings.depthOfFieldEnabled = flag;
            return this;
        }

        /**
         * Sets the focal length for the depth-of-field effect.
         *
         * @param focalLength The desired focal length.
         * @return The current {@code Builder} instance for chaining.
         */
        public Builder setFocalLength(double focalLength) {
            this.focalLength = focalLength;
            return this;
        }

        public Builder setSoftShadowsQuality(QualityLevel qualityLevel) {
            RenderSettings.softShadowQuality = qualityLevel;
            return this;
        }

        /**
         * Configures the {@link ImageWriter} for rendering the image.
         */
        private void configureImageWriter() {
            imageWriter = new ImageWriter(imageName, resolutionX, resolutionY);
        }

        /**
         * Configures the {@link ViewPlane} based on the current settings.
         */
        private void configureViewPlane() {
            Point vpCenter = position.add(forward.scale(vpDistance));
            viewPlane = new ViewPlane(right, up, forward, vpHeight, vpWidth, vpCenter, imageWriter.getNx(), imageWriter.getNy());
        }

        /**
         * Configures the {@link Render} engine based on the current settings.
         */
        private void configureRenderEngine() {
            if (RenderSettings.depthOfFieldEnabled) {
                renderEngine = new DOFRendering(imageWriter, viewPlane, rayTracer, position, apertureSize, focalLength, RenderSettings.depthOfFieldQuality);
                return;
            }

            if (RenderSettings.antiAliasingEnabled) {
                switch (RenderSettings.antiAliasingQuality) {
                    case LOW -> renderEngine = new SSAA4X(imageWriter, viewPlane, rayTracer, position);
                    case MEDIUM -> throw new UnsupportedOperationException("Anti-aliasing quality 'MEDIUM' is not supported.");
                    case HIGH, ULTRA -> renderEngine = new SuperSamplingAntiAliasing(imageWriter, viewPlane, rayTracer, position);
                }
                return;
            }

            renderEngine = new Render(imageWriter, viewPlane, rayTracer, position);
        }

        /**
         * Builds and returns a {@link Camera} instance based on the current configuration.
         *
         * @return A configured {@link Camera} instance.
         * @throws MissingResourceException If any required configuration is missing or invalid.
         */
        public Camera build() {
            if (position == null) {
                throw new MissingResourceException("Camera position is missing.", Camera.class.getName(), "position");
            }
            if (right == null || up == null || forward == null) {
                throw new MissingResourceException("Camera orientation is incomplete.", Camera.class.getName(), "right/up/forward");
            }
            if (!forward.isPerpendicular(up)) {
                throw new IllegalArgumentException("The 'forward' and 'up' vectors must be perpendicular.");
            }
            if (resolutionX <= 0 || resolutionY <= 0) {
                throw new MissingResourceException("Image resolution is invalid.", Camera.class.getName(), "resolutionX/resolutionY");
            }
            if (imageName == null) {
                throw new MissingResourceException("Image name is missing.", Camera.class.getName(), "imageName");
            }
            if (alignZero(vpHeight) <= 0 || alignZero(vpWidth) <= 0) {
                throw new MissingResourceException("View plane dimensions are invalid.", Camera.class.getName(), "vpHeight/vpWidth");
            }
            if (alignZero(vpDistance) <= 0) {
                throw new MissingResourceException("View plane distance is invalid.", Camera.class.getName(), "vpDistance");
            }
            if (rayTracer == null) {
                throw new MissingResourceException("Ray tracer is not set.", Camera.class.getName(), "rayTracer");
            }

            if (RenderSettings.CBRIsEnabled) {
                rayTracer.scene.geometries.calculateAABB();
            }

            if (RenderSettings.BVHIsEnabled && !bvhBuilt) {
                rayTracer.scene.geometries.buildBVH();
                bvhBuilt = true;
            }

            if (RenderSettings.softShadowsEnabled) {
                rayTracer.scene.lights.forEach(light -> light.computeSamples(RenderSettings.softShadowQuality));
            }

            configureImageWriter();
            configureViewPlane();
            configureRenderEngine();

            return new Camera(this);
        }
    }
}

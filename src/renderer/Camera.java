package renderer;

import geometries.Geometries;
import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.anti_aliasing_rendering.AntiAliasingUltra;
import renderer.anti_aliasing_rendering.SSAA4X;
import renderer.anti_aliasing_rendering.SuperSamplingAntiAliasing;
import scene.Scene;

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
public class Camera {
    private final Render renderBase;

    /**
     * Default constructor for {@code Camera}.
     */
    protected Camera(Builder cameraBuilder) {
        renderBase = cameraBuilder.render;
    }


    /**
     * Returns a new {@code Builder} instance for {@code Camera}.
     *
     * @return a new {@code Builder} instance.
     */
    public static Builder getBuilder() {
        return new Builder();
    }


    public void writeToImage() {
        renderBase.writeToImage();
    }

    public Camera printGrid(int interval, Color color) {
        renderBase.printGrid(interval, color);
        return this;
    }

    public Camera renderImage() {

        if (RenderSettings.parallelStreamsEnabled)
            renderBase.parallelStreamsRender();
        else if (RenderSettings.multiThreadingEnabled)
            renderBase.multiThreadingRender(RenderSettings.threadsCount);
        else
            renderBase.render();

        return this;
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
        private Point vpCenter;
        private ViewPlane viewPlane;
        private ImageWriter imageWriter;
        private int nX;
        private int nY;
        private String imageName;
        private RayTracerBase rayTracerBase;
        private Render render;
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

            this.up = Vector.UNIT_Y; // The y-axis is up
//            if (!isZero(camera.to.dotProduct(camera.up))) {
            if (this.up.equals(this.to) || this.up.equals(this.to.scale(-1))) {
                this.up = Vector.UNIT_Z; // Switch to Z-axis if Vector to is (0, 1, 0)
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


        public Builder setImageName(String imageName) {
            this.imageName = imageName;
            return this;
        }

        public Builder setResolution(int nX, int nY) {
            this.nX = nX;
            this.nY = nY;
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


        public Builder setAntiAliasingQuality(QualityLevel qualityLevel) {
            RenderSettings.antiAliasingQuality = qualityLevel;
            return this;
        }

        /**
         * Enables or disables BVH in the render.
         *
         * @param flag a boolean indicating whether BVH should be enabled (true) or disabled (false).
         * @return the current Builder object for chaining method calls.
         */
        public Builder setBVH(Boolean flag) {
            if (flag)
                setCBR(true);
            RenderSettings.BVHIsEnabled = flag;
            return this;
        }

        public Builder setCBR(Boolean flag) {
            RenderSettings.CBRIsEnabled = flag;
            return this;
        }


        public Builder setScene(Scene scene) {
            this.rayTracerBase = new SimpleRayTracer(scene);
            rayTracerWasSet = true;
            return this;
        }

        public Builder setThreadsCount(int threadsCount) {
            RenderSettings.threadsCount = threadsCount;
            return this;
        }

        public Builder setMultiThreading(boolean flag) {
            RenderSettings.multiThreadingEnabled = flag;
            return this;
        }

        public Builder setParallelStreams(boolean flag) {
            RenderSettings.parallelStreamsEnabled = flag;
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
            RenderSettings.antiAliasingEnabled = flag;
            return this;
        }

        private void setImageWriter() {
            imageWriter = new ImageWriter(imageName, nX, nY);
        }

        private void setVPCenter() {
            vpCenter = location.add(to.scale(vpDistance));
        }

        private void setViewingPlane() {
            viewPlane = new ViewPlane(right, up, vpHeight, vpWidth, vpCenter, imageWriter.getNx(), imageWriter.getNy());
        }

        private void setRender() {
            if (!RenderSettings.antiAliasingEnabled) {
                render = new Render(imageWriter, viewPlane, rayTracerBase, location);
                return;
            }

            switch (RenderSettings.antiAliasingQuality) {
                case LOW:
                    System.out.println("Applying low quality settings");
                    render = new SSAA4X(imageWriter, viewPlane, rayTracerBase, location);
                    break;
                case MEDIUM:
                    System.out.println("Applying medium quality settings");
                    // Apply medium quality settings
                    break;
                case HIGH:
                    System.out.println("Applying high quality settings");
                    render = new SuperSamplingAntiAliasing(imageWriter, viewPlane, rayTracerBase, location);
                    break;
                case ULTRA:
                    System.out.println("Applying ultra quality settings");
                    render = new AntiAliasingUltra(imageWriter, viewPlane, rayTracerBase, location);
                    break;
            }
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

            if (nX <= 0 || nY <= 0)
                throw new MissingResourceException("Missing image resolution", Camera.class.getName(), "nX, nY");

            if (imageName == null)
                throw new MissingResourceException("Missing image name", Camera.class.getName(), "imageName");

            if (alignZero(this.vpHeight) <= 0 || alignZero(this.vpWidth) <= 0)
                throw new MissingResourceException("Invalid view plane dimensions", Camera.class.getName(), "height/width");

            if (alignZero(this.vpDistance) <= 0)
                throw new MissingResourceException("Invalid view plane distance", Camera.class.getName(), "vpDistance");

            if (this.rayTracerBase == null)
                throw new MissingResourceException("Missing camera rayTracerBase", Camera.class.getName(), "rayTracerBase");

            if (RenderSettings.CBRIsEnabled)
                this.rayTracerBase.scene.geometries.calculateAABB();

            if (RenderSettings.BVHIsEnabled)
                this.rayTracerBase.scene.geometries.buildBVH();

            if (!RenderSettings.softShadowsEnabled)
                RenderSettings.SHADOW_RAYS_SAMPLE_COUNT = 1;


            setImageWriter();
            if (this.imageWriter == null)
                throw new MissingResourceException("Missing camera imageWriter", Camera.class.getName(), "imageWriter");


            setVPCenter();
            if (vpCenter == null)
                throw new MissingResourceException("ERROR constructing camera center", Camera.class.getName(), "center");

            setViewingPlane();
            if (viewPlane == null)
                throw new MissingResourceException("ERROR constructing camera viewPlane", Camera.class.getName(), "viewPlane");

            setRender();
            if (render == null)
                throw new MissingResourceException("ERROR constructing camera render", Camera.class.getName(), "render");

            return new Camera(this);
        }
    }
}

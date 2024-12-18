package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Base rendering engine that supports multiple rendering modes:
 * - Sequential rendering.
 * - Multithreaded rendering.
 * - Rendering using parallel streams.
 *
 * <p>Handles ray construction for pixels and writing the resulting image.</p>
 *
 * @author Benny Avrahami
 */
public class Render implements Rendering, MultiThreadingRendering, ParallelStreamsRendering {

    protected final ImageWriter imageWriter;
    protected final ViewPlane viewPlane;
    protected final RayTracerBase rayTracer;
    protected final Point cameraPosition;
    private PixelManager pixelTracker;

    /**
     * Constructs a Render instance with the specified image writer, view plane, ray tracer, and camera position.
     *
     * @param imageWriter    The image writer for outputting the rendered image.
     * @param viewPlane      The view plane for determining pixel positions and directions.
     * @param rayTracer      The ray tracer responsible for tracing rays through the scene.
     * @param cameraPosition The position of the camera in the scene.
     */
    public Render(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point cameraPosition) {
        this.imageWriter = imageWriter;
        this.viewPlane = viewPlane;
        this.rayTracer = rayTracer;
        this.cameraPosition = cameraPosition;
    }

    /**
     * Renders the image sequentially.
     */
    @Override
    public void render() {
        renderSequentially(imageWriter.getNx(), imageWriter.getNy());
    }

    /**
     * Renders the image using multithreading.
     *
     * @param numberOfThreads The number of threads to use for rendering.
     */
    @Override
    public void multiThreadingRender(int numberOfThreads) {
        renderWithMultithreading(imageWriter.getNx(), imageWriter.getNy(), numberOfThreads);
    }

    /**
     * Renders the image using parallel streams.
     */
    @Override
    public void parallelStreamsRender() {
        renderWithParallelStreams(imageWriter.getNx(), imageWriter.getNy());
    }

    private void renderWithParallelStreams(int imageWidth, int imageHeight) {
        IntStream.range(0, imageWidth * imageHeight).parallel().forEach(index -> {
            int x = index % imageWidth;
            int y = index / imageWidth;
            castRay(x, y);
        });
    }

    private void renderSequentially(int imageWidth, int imageHeight) {
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                castRay(x, y);
            }
        }
    }

    /**
     * Casts a ray through the specified pixel coordinates and writes the computed color to the image.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     */
    protected void castRay(int x, int y) {
        Ray ray = constructRayForPixel(x, y);
        Color pixelColor = rayTracer.traceRay(ray);
        imageWriter.writePixel(x, y, pixelColor);
    }

    private void renderWithMultithreading(int imageWidth, int imageHeight, int numberOfThreads) {
        pixelTracker = new PixelManager(imageHeight, imageWidth, 0);
        List<Thread> threads = new LinkedList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            threads.add(new Thread(this::processPixels));
        }
        startAndJoinThreads(threads);
    }

    private void processPixels() {
        PixelManager.Pixel pixel;
        while ((pixel = pixelTracker.nextPixel()) != null) {
            castRay(pixel.col(), pixel.row());
        }
    }

    private void startAndJoinThreads(List<Thread> threads) {
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
                // Handle interrupted exceptions gracefully.
            }
        }
    }

    /**
     * Constructs a ray through the center of the specified pixel.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The constructed ray.
     */
    protected Ray constructRayForPixel(int x, int y) {
        return new Ray(cameraPosition, viewPlane.getPixelCenter(x, y));
    }

    /**
     * Prints a grid on the image at the specified interval and color.
     *
     * @param interval The spacing between grid lines.
     * @param color    The color of the grid lines.
     */
    public void printGrid(int interval, Color color) {
        applyToAllPixels((x, y) -> {
            if (x % interval == 0 || y % interval == 0) {
                imageWriter.writePixel(x, y, color);
            }
        });
    }

    /**
     * Writes the rendered image to a file.
     */
    public void writeToImage() {
        imageWriter.writeToImage();
    }

    private void applyToAllPixels(PixelAction action) {
        int imageWidth = imageWriter.getNx();
        int imageHeight = imageWriter.getNy();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                action.apply(x, y);
            }
        }
    }

    @FunctionalInterface
    private interface PixelAction {
        void apply(int x, int y);
    }
}

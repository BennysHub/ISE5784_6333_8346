package renderer.anti_aliasing_rendering;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.Render;
import renderer.ViewPlane;

import java.util.stream.IntStream;

/**
 * Implements 4X super sampling for antialiasing in rendering.
 * This technique improves image quality by sampling multiple points within each pixel
 * and averaging the results to reduce aliasing artifacts.
 *
 * <p>Supports both single-threaded and parallel rendering.</p>
 */
public class SSAA4X extends Render {

    /**
     * Array storing colors calculated for each super sampled pixel.
     */
    protected Color[][] superSampledColors;

    /**
     * Constructs a renderer with 4X super sampling for antialiasing.
     *
     * <p>The view plane is adjusted to support super sampling by adding extra rows and columns.</p>
     *
     * @param imageWriter    The image writer to output the rendered image.
     * @param viewPlane      The view plane for the rendering process.
     * @param rayTracer      The ray tracer for tracing rays through the scene.
     * @param cameraPosition The position of the camera in the scene.
     */
    public SSAA4X(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point cameraPosition) {
        super(
                imageWriter,
                new ViewPlane(
                        viewPlane.right,
                        viewPlane.up,
                        viewPlane.direction,
                        viewPlane.vpHeight + viewPlane.pixelHeight,
                        viewPlane.vpWidth + viewPlane.pixelWidth,
                        viewPlane.center,
                        viewPlane.nX + 1,
                        viewPlane.nY + 1
                ),
                rayTracer,
                cameraPosition
        );
        superSampledColors = new Color[this.viewPlane.nX][this.viewPlane.nY];
    }

    @Override
    public void parallelStreamsRender() {
        renderPixelsParallel(viewPlane.nX, viewPlane.nY);
        writeSuperSampledPixelsParallel();
    }

    @Override
    public void render() {
        renderPixels(viewPlane.nX, viewPlane.nY);
        writeSuperSampledPixels();
    }

    private void renderPixelsParallel(int nX, int nY) {
        IntStream.range(0, nX * nY).parallel().forEach(index -> {
            int x = index % nX;
            int y = index / nX;
            calculatePixelColor(x, y);
        });
    }

    private void renderPixels(int nX, int nY) {
        for (int y = 0; y < nY; y++) {
            for (int x = 0; x < nX; x++) {
                calculatePixelColor(x, y);
            }
        }
    }

    /**
     * Calculates the color for a super sampled pixel at the given coordinates.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     */
    protected void calculatePixelColor(int x, int y) {
        Ray ray = new Ray(cameraPosition, viewPlane.getPixelCenter(x, y));
        superSampledColors[x][y] = rayTracer.traceRay(ray);
    }

    private void writeSuperSampledPixelsParallel() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();

        IntStream.range(0, nX * nY).parallel().forEach(index -> {
            int x = index % nX;
            int y = index / nX;
            imageWriter.writePixel(x, y, calculatePixelAverageColor(x, y));
        });
    }

    private void writeSuperSampledPixels() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();

        for (int y = 0; y < nY; y++) {
            for (int x = 0; x < nX; x++) {
                imageWriter.writePixel(x, y, calculatePixelAverageColor(x, y));
            }
        }
    }

    /**
     * Computes the average color for a pixel based on its super-sampled colors.
     *
     * @param x The x-coordinate of the pixel.
     * @param y The y-coordinate of the pixel.
     * @return The average color for the pixel.
     */
    protected Color calculatePixelAverageColor(int x, int y) {
        return Color.average(
                pixelTopLeftSample(x, y),
                pixelTopRightSample(x, y),
                pixelBottomLeftSample(x, y),
                pixelBottomRightSample(x, y)
        );
    }

    protected Color pixelTopLeftSample(int x, int y) {
        return superSampledColors[x][y];
    }

    protected Color pixelTopRightSample(int x, int y) {
        return superSampledColors[x][y + 1];
    }

    protected Color pixelBottomLeftSample(int x, int y) {
        return superSampledColors[x + 1][y];
    }

    protected Color pixelBottomRightSample(int x, int y) {
        return superSampledColors[x + 1][y + 1];
    }
}

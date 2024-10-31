package renderer.anti_aliasing_rendering;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.Render;
import renderer.ViewPlane;

import java.util.stream.IntStream;

public class SSAA4X extends Render {

    ViewPlane newViewPlane = new ViewPlane(viewPlane.right, viewPlane.up, viewPlane.vpHeight + viewPlane.pixelHeight, viewPlane.vpWidth + viewPlane.pixelWidth, viewPlane.center, viewPlane.nX + 1, viewPlane.nY + 1);


    Color[][] pixelColors = new Color[newViewPlane.nX][newViewPlane.nY];

    public SSAA4X(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }


    @Override
    public void parallelStreamsRender() {
        parallelStreamsRender(newViewPlane.nX, newViewPlane.nY);
        writePixelsParallel();
    }

    @Override
    public void render() {
        render(newViewPlane.nX, newViewPlane.nY);
        writePixels();
    }

    private void parallelStreamsRender(int nX, int nY) {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> calcColor(j, i)));
    }

    private void render(int nX, int nY) {
        for (int i = 0; i < nY; i++) {
            for (int j = 0; j < nX; j++) {
                calcColor(j, i);
            }
        }
    }


    private void calcColor(int x, int y) {
        Ray ray = new Ray(camaraLocation, newViewPlane.getPixelCenter(x, y));
        pixelColors[x][y] = rayTracer.traceRay(ray);
    }

    public void writePixelsParallel() {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();

        IntStream.range(0, nX).parallel().forEach(x -> {
            IntStream.range(0, nY).parallel().forEach(y -> {
                imageWriter.writePixel(x, y, pixelColors[x][y]
                        .add(pixelColors[x + 1][y], pixelColors[x][y + 1], pixelColors[x + 1][y + 1])
                        .reduce(4));
            });
        });
    }

    public void writePixels() {
        for (int x = 0; x < imageWriter.getNx(); x++)
            for (int y = 0; y < imageWriter.getNy(); y++)
                imageWriter.writePixel(x, y, pixelColors[x][y].add(pixelColors[x + 1][y], pixelColors[x][y + 1], pixelColors[x + 1][y + 1]).reduce(4));
    }
}

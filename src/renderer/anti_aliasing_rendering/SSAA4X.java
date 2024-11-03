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

    protected ViewPlane viewPlaneHelper = new ViewPlane(viewPlane.right, viewPlane.up, viewPlane.vpHeight + viewPlane.pixelHeight, viewPlane.vpWidth + viewPlane.pixelWidth, viewPlane.center, viewPlane.nX + 1, viewPlane.nY + 1);


    Color[][] pixelColors = new Color[viewPlaneHelper.nX][viewPlaneHelper.nY];

    public SSAA4X(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }


    @Override
    public void parallelStreamsRender() {
        parallelStreamsRender(viewPlaneHelper.nX, viewPlaneHelper.nY);
        writePixelsParallel();
    }

    @Override
    public void render() {
        render(viewPlaneHelper.nX, viewPlaneHelper.nY);
        writePixels();
    }

    protected void parallelStreamsRender(int nX, int nY) {
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


    protected void calcColor(int x, int y) {
        Ray ray = new Ray(camaraLocation, viewPlaneHelper.getPixelCenter(x, y));
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
                imageWriter.writePixel(x, y, pixelAvrageColor(x, y));
    }

    protected Color pixelAvrageColor(int x, int y) {
        return Color.average(pixelLeftUpSample(x, y), pixelRightUpSample(x, y), pixelLeftDownSample(x, y), pixelRightDownSample(x, y));
    }

    protected Color pixelLeftUpSample(int x, int y) {
        return pixelColors[x][y];
    }

    protected Color pixelRightUpSample(int x, int y) {
        return pixelColors[x][y + 1];
    }

    protected Color pixelLeftDownSample(int x, int y) {
        return pixelColors[x + 1][y];
    }

    protected Color pixelRightDownSample(int x, int y) {
        return pixelColors[x + 1][y + 1];
    }
}

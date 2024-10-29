package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Render {

    protected final ImageWriter imageWriter;
    protected final ViewPlane viewPlane;
    protected final RayTracerBase rayTracer;
    protected final Point camaraLocation;

    public Render(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        this.imageWriter = imageWriter;
        this.viewPlane = viewPlane;
        this.rayTracer = rayTracer;
        this.camaraLocation = camaraLocation;
    }

//    public void renderImage(){
//        int nX = imageWriter.getNx();
//        int nY = imageWriter.getNy();
//
//        if (RenderSettings.threadsCount < 2)
//            singleThreadedRender(nX, nY);
//        else
//            multiThreadedRender(nX, nY);
//    }

    public abstract void renderImage();


//    private void singleThreadedRender(int nX, int nY) {
//        for (int x = nX - 1; x >= 0; --x)
//            for (int y = nY - 1; y >= 0; --y)
//                castRay(x, y);
//    }
//
//    private void streamParallelRender(int nX, int nY) {
//        IntStream.range(0, nY).parallel()
//                .forEach(i -> IntStream.range(0, nX).parallel()
//                        .forEach(j -> castRay(j, i)));
//    }


//
//    private void multiThreadedRender(int nX, int nY) {
//        pixelManager = new PixelManager(nY, nX, 0);
//        int threadsCount = RenderSettings.threadsCount;
//        List<Thread> threads = new LinkedList<>();
//
//        while (threadsCount-- > 0) {
//            threads.add(new Thread(this::processPixels));
//        }
//
//        startAndJoinThreads(threads);
//    }
//
//    private void processPixels() {
//        PixelManager.Pixel pixel;
//        while ((pixel = pixelManager.nextPixel()) != null) {
//            castRay(pixel.col(), pixel.row());
//        }
//    }
//
//    private void startAndJoinThreads(List<Thread> threads) {
//        threads.forEach(Thread::start);
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException ignore) {
//            }
//        }
//    }

    protected Ray constructCentralRay(int x, int y){
        return new Ray(camaraLocation, viewPlane.getPixelCenter(x,y));
    }

    /**
     * Casts a ray through the given pixel coordinates and writes the computed color to the image.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     */
    protected void castRay(int x, int y) {
        Ray ray = constructCentralRay(x, y);
        Color pixelColor = rayTracer.traceRay(ray);
        imageWriter.writePixel(x, y, pixelColor);
    }

    /**
     * Prints a grid on the image with the specified interval and color.
     *
     * @param interval the spacing between grid lines
     * @param color    the color of the grid lines
     */
    public void printGrid(int interval, Color color) {
        int nX = imageWriter.getNx();
        int nY = imageWriter.getNy();
        for (int x = nX - 1; x >= 0; --x)
            for (int y = nY - 1; y >= 0; --y)
                if (x % interval == 0 || y % interval == 0) imageWriter.writePixel(x, y, color);
    }

    /**
     * Writes the image to a file.
     */
    public void writeToImage() {
        imageWriter.writeToImage();
    }

}

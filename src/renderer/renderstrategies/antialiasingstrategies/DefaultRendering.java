package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import renderer.ImageWriter;
import renderer.RayTracerBase;
import renderer.Render;
import renderer.ViewPlane;

import java.util.stream.IntStream;

public class DefaultRendering extends Render {


    public DefaultRendering(ImageWriter imageWriter, ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(imageWriter, viewPlane, rayTracer, camaraLocation);
    }

    @Override
    public void renderImage() {

        streamParallelRender(imageWriter.getNx(), imageWriter.getNy());
//        for (int x = 0; x < imageWriter.getNx(); x++)
//            for (int y = 0; y< imageWriter.getNy(); y++)
//                castRay(x, y);
    }

    private void streamParallelRender(int nX, int nY) {
        IntStream.range(0, nY).parallel()
                .forEach(i -> IntStream.range(0, nX).parallel()
                        .forEach(j -> castRay(j, i)));
    }

}

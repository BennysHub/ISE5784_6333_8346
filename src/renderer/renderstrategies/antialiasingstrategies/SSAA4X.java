package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public class SSAA4X extends PixelSamplingStrategy {

    public SSAA4X(ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        //sample each pixel 4 corners, corners can be reused for other pixels samples total sample is increased from x* Y to (x+1)*(y+1)
        super(
                new ViewPlane(viewPlane.right, viewPlane.up, viewPlane.vpHeight + viewPlane.pixelHeight, viewPlane.vpWidth + viewPlane.pixelWidth, viewPlane.center, viewPlane.nX + 1, viewPlane.nY + 1),
                rayTracer,
                camaraLocation);
    }

    @Override
    public Color calcalatePixelColor(int x, int y) {
        Color color = rayTracer.traceRay(constructCentralRay(x, y))
                .add(rayTracer.traceRay(constructCentralRay(x + 1, y)),
                        rayTracer.traceRay(constructCentralRay(x + 1, y)),
                        rayTracer.traceRay(constructCentralRay(x + 1, y + 1)));
        return color.reduce(4);
    }
}

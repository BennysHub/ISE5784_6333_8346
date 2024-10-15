package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public class DefaultColoring extends PixelColoringStrategy {
    public DefaultColoring(ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(viewPlane, rayTracer, camaraLocation);
    }

    @Override
    public Color calcalatePixelColor(int x, int y) {
        return rayTracer.traceRay(constructCentralRay(x, y));
    }
}

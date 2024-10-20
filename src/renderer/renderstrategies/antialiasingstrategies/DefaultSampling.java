package renderer.renderstrategies.antialiasingstrategies;

import primitives.Color;
import primitives.Point;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public class DefaultSampling extends PixelSamplingStrategy {
    public DefaultSampling(ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        super(viewPlane, rayTracer, camaraLocation);
    }

    @Override
    public Color calcalatePixelColor(int x, int y) {
        return rayTracer.traceRay(constructCentralRay(x, y));
    }
}

package renderer.renderstrategies.antialiasingstrategies;


import primitives.Color;
import primitives.Point;
import primitives.Ray;
import renderer.RayTracerBase;
import renderer.ViewPlane;

public abstract class PixelColoringStrategy {

    protected final ViewPlane viewPlane;
    protected final RayTracerBase rayTracer;
    protected final Point camaraLocation;

    protected PixelColoringStrategy(ViewPlane viewPlane, RayTracerBase rayTracer, Point camaraLocation) {
        this.viewPlane = viewPlane;
        this.rayTracer = rayTracer;
        this.camaraLocation = camaraLocation;
    }


    public abstract Color calcalatePixelColor(int x, int y);

    public Ray constructCentralRay(int x, int y){
        return new Ray(camaraLocation, viewPlane.getPixelCenter(x,y));
    }


}

package renderer;
import primitives.Color;
import primitives.Ray;
import scene.Scene;

public abstract class RayTracerBase {
    protected Scene scene;

    public RayTracerBase(Scene scene){
        this.scene = scene;
    }

    public Color traceRay(Ray ray){
        return Color.BLACK;
    }


}

package renderer;
import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

public abstract class RayTracerBase {
    protected Scene scene;

    public RayTracerBase(Scene scene){
        this.scene = scene;
    }

    public Color traceRay(Ray ray){
        var list = scene.geometries.findIntersections(ray);
        return (list == null)? Color.BLACK : calcColor(ray.findClosestPoint(list));
    }

    private Color calcColor(Point point){
        return scene.background;
    }

}

package renderer.renderstrategies.shadowstrategies;

import geometries.Intersectable;
import lighting.LightSource;
import primitives.Double3;
import primitives.Vector;
import renderer.RenderSettings;
import scene.Scene;

import java.util.List;

public abstract class  ShadowStrategy  {

     protected final Scene scene;

     public ShadowStrategy(Scene scene){
          this.scene = scene;
     }

     public abstract Double3 transparency(Intersectable.GeoPoint gp, LightSource light, Vector n);


     /**
      * Calculates the cumulative transparency factor for a collection of intersections.
      *
      * @param intersections the collection of GeoPoint intersections
      * @return the cumulative transparency factor as a Double3
      */
     protected Double3 totalTransparency(List<Intersectable.GeoPoint> intersections) {
          Double3 ktr = Double3.ONE;
          if (intersections == null) return ktr;
          for (Intersectable.GeoPoint p : intersections) {
               ktr = ktr.product(p.geometry.getMaterial().kT);
               if (ktr.lowerThan(RenderSettings.MIN_CALC_COLOR_K))
                    return ktr;
          }
          return ktr;
     }
}

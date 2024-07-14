package lighting;

import primitives.Color;
import primitives.Double3;

public class AmbientLight {

    final private Color intensity;
    final public static AmbientLight NONE = new AmbientLight(Color.BLACK, 0d);

    public AmbientLight(Color iA, Double3 kA) {
        intensity = iA.scale(kA);
    }

    public AmbientLight(Color iA, Double kA) {
        intensity = iA.scale(kA);
    }

    public Color getIntensity(){
        return intensity;
    }
}

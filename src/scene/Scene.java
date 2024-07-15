package scene;

import geometries.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * The Scene class represents a scene in a 3D environment.
 * It includes properties such as the name, background color, ambient light, and geometries within the scene.
 *
 * @author Benny Avrahami and Tzvi Yisrael
 */
public class Scene {
    /**
     * The name of the scene
     */
    public final String name;

    /**
     * The background color of the scene
     */
    public Color background = Color.BLACK;

    /**
     * The ambient light in the scene, default is no ambient light
     */
    public AmbientLight ambientLight = AmbientLight.NONE;

    /**
     * The collection of geometries in the scene
     */
    public Geometries geometries = new Geometries();

    /**
     * Constructs a Scene object with a specified name.
     *
     * @param sceneName the name of the scene
     */
    public Scene(String sceneName) {
        name = sceneName;
    }

    /**
     * Sets the background color of the scene.
     *
     * @param background the new background color
     * @return the current Scene object, for method chaining
     */
    public Scene setBackground(Color background) {
        this.background = background;
        return this;
    }

    /**
     * Sets the ambient light of the scene.
     *
     * @param ambientLight the new ambient light
     * @return the current Scene object, for method chaining
     */
    public Scene setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    /**
     * Sets the geometries in the scene.
     *
     * @param geometries the new collection of geometries
     * @return the current Scene object, for method chaining
     */
    public Scene setGeometries(Geometries geometries) {
        this.geometries = geometries;
        return this;
    }
}

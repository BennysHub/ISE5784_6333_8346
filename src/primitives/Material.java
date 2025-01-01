package primitives;

/**
 * Class representing the material properties of a surface in a 3D scene.
 * Includes diffuse and specular reflection coefficients and shininess factor.
 *
 * @author Benny Avrahami
 */
public class Material {

    /**
     * The diffuse reflection coefficient, default is zero.
     */
    public Double3 kD = Double3.ZERO;
    /**
     * The specular reflection coefficient, default is zero.
     */
    public Double3 kS = Double3.ZERO;
    /**
     * The transmission coefficient, default is zero.
     */
    public Double3 kT = Double3.ZERO;
    /**
     * The reflection coefficient, default is zero.
     */
    public Double3 kR = Double3.ZERO;
    /**
     * The shininess factor, default is zero.
     */
    public int shininess = 0;
    /**
     * The emission color of the material, default is black.
     */
    protected Color emission = Color.BLACK;

    /**
     * Gets the emission color of the geometry.
     *
     * @return the emission color of the geometry
     */
    public Color getEmission() {
        return emission;
    }

    /**
     * Sets the emission color of the geometry.
     *
     * @param emission the new emission color
     * @return the current Geometry object, for method chaining
     */
    public Material setEmission(Color emission) {
        this.emission = emission;
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param kD the diffuse reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKd(Double kD) {
        this.kD = new Double3(kD);
        return this;
    }

    /**
     * Sets the specular reflection coefficient.
     *
     * @param kS the specular reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKs(Double kS) {
        this.kS = new Double3(kS);
        return this;
    }

    /**
     * Sets the diffuse reflection coefficient.
     *
     * @param kD the diffuse reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKd(Double3 kD) {
        this.kD = kD;
        return this;
    }

    /**
     * Sets the specular reflection coefficient.
     *
     * @param kS the specular reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKs(Double3 kS) {
        this.kS = kS;
        return this;
    }

    /**
     * Sets the transmission coefficient.
     *
     * @param kT the transmission coefficient
     * @return the current Material instance for chaining
     */
    public Material setKt(Double kT) {
        if (kT < 0 || kT > 1)
            throw new IllegalArgumentException("Transparency  Attenuation coefficient  must be between [0,1]");
        this.kT = new Double3(kT);
        return this;
    }

    /**
     * Sets the transmission coefficient.
     *
     * @param kT the transmission coefficient
     * @return the current Material instance for chaining
     */
    public Material setKt(Double3 kT) {
        if (Double3.ONE.lowerThan(kT) || kT.lowerThan(Double3.ZERO))
            throw new IllegalArgumentException("Transparency  Attenuation coefficient  must be between [0,1]");
        this.kT = kT;
        return this;
    }

    /**
     * Sets the reflection coefficient.
     *
     * @param kR the reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKr(Double kR) {
        if (kR < 0 || kR > 1)
            throw new IllegalArgumentException("Reflection Attenuation coefficient  must be between [0,1]");
        this.kR = new Double3(kR);
        return this;
    }

    /**
     * Sets the reflection coefficient.
     *
     * @param kR the reflection coefficient
     * @return the current Material instance for chaining
     */
    public Material setKr(Double3 kR) {
        if (Double3.ONE.lowerThan(kR) || kR.lowerThan(Double3.ZERO))
            throw new IllegalArgumentException("Reflection Attenuation coefficient must be between [0,1]");
        this.kR = kR;
        return this;
    }

    /**
     * Sets the shininess factor.
     *
     * @param nShininess the shininess factor
     * @return the current Material instance for chaining
     */
    public Material setShininess(int nShininess) {
        this.shininess = nShininess;
        return this;
    }


    // Predefined materials for common surfaces
    public static final Material MATTE_PLASTIC = new Material()
            .setKd(new Double3(0.8))
            .setKs(new Double3(0.2))
            .setShininess(10);

    public static final Material GLOSSY_PLASTIC = new Material()
            .setKd(new Double3(0.6))
            .setKs(new Double3(0.8))
            .setShininess(50);

    public static final Material METAL = new Material()
            .setKd(new Double3(0.1))
            .setKs(new Double3(1.0))
            .setKr(new Double3(0.9))
            .setShininess(200);

    public static final Material RUBBER = new Material()
            .setKd(new Double3(0.7))
            .setKs(new Double3(0.1))
            .setShininess(10);

    public static final Material MIRROR = new Material()
            .setKd(Double3.ZERO)
            .setKs(new Double3(1.0))
            .setKr(new Double3(1.0))
            .setShininess(300);

    public static final Material GLASS = new Material()
            .setKd(Double3.ZERO)
            .setKs(new Double3(0.9))
            .setKt(new Double3(0.9))
            .setShininess(100);

    public static final Material STONE = new Material()
            .setKd(new Double3(0.8))
            .setKs(new Double3(0.1))
            .setShininess(20);

    public static final Material WOOD = new Material()
            .setKd(new Double3(0.6))
            .setKs(new Double3(0.2))
            .setShininess(5);

    public static final Material WATER = new Material()
            .setKd(new Double3(0.2))
            .setKs(new Double3(0.8))
            .setKt(new Double3(0.95))
            .setShininess(300);

    public static final Material GOLD = new Material()
            .setKd(new Double3(0.3))
            .setKs(new Double3(0.9))
            .setKr(new Double3(0.7))
            .setShininess(250);

    public static final Material SILVER = new Material()
            .setKd(new Double3(0.2))
            .setKs(new Double3(1.0))
            .setKr(new Double3(0.85))
            .setShininess(200);

    public static final Material DIAMOND = new Material()
            .setKd(Double3.ZERO)
            .setKs(new Double3(0.95))
            .setKt(new Double3(0.98))
            .setShininess(400);

    public static final Material BRICK = new Material()
            .setKd(new Double3(0.7))
            .setKs(new Double3(0.1))
            .setShininess(10);

    public static final Material CONCRETE = new Material()
            .setKd(new Double3(0.9))
            .setKs(new Double3(0.05))
            .setShininess(5);

    public static final Material CERAMIC = new Material()
            .setKd(new Double3(0.5))
            .setKs(new Double3(0.6))
            .setShininess(80);

    public static final Material DEFAULT = new Material()
            .setKd(new Double3(0.5))
            .setKs(new Double3(0.5))
            .setShininess(30);
}

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

}

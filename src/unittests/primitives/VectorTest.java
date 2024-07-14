package primitives;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static primitives.Util.isZero;

/**
 * Unit tests for the primitives.Vector class.
 * Each method is tested separately with Equivalence Partitioning and Boundary Value Analysis.
 *
 * @author Benny Avrahami
 */
class VectorTest {

    private static final Vector v1 = new Vector(1, 2, 3);
    private static final Vector v2 = new Vector(-2, -4, -6); // Parallel opposite vector to v1
    private static final Vector v3 = new Vector(0, 3, -2); // Orthogonal vector to v1
    private static final Vector v4 = new Vector(1, 2, 2);
    private static final double DELTA = 0.000001; // Delta value for accuracy when comparing 'double' types

    /**
     * Test method for {@link primitives.Vector#Vector(double, double, double)}.
     */
    @Test
    void testVectorConstructor() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Constructing a vector with non-zero coordinates
        assertDoesNotThrow(() -> new Vector(1, 1, 1),
                "Constructing a vector with non-zero coordinates should not throw an exception.");

        // =============== Boundary Values Tests ==================

        // TC10: Constructing a vector with zero coordinates
        assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                "Constructing a vector with zero coordinates should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Vector#length()}.
     */
    @Test
    void testVectorLength() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: The length of a non-zero vector
        assertEquals(3d, v4.length(), DELTA,
                "The length of the vector is incorrect.");

        // TC02: The length of a unit vector
        assertEquals(1d, new Vector(1, 0, 0).length(), DELTA,
                "The length of a unit vector should be 1.");

        // =============== Boundary Values Tests ==================

        // TC10: The length of a vector is the square root of the length squared
        assertEquals(Math.sqrt(v1.lengthSquared()), v1.length(),
                "The length of a vector should be the square root of the length squared.");
    }

    /**
     * Test method for {@link primitives.Vector#lengthSquared()}
     */
    @Test
    void testVectorLengthSquared() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: The length squared of a non-zero vector
        assertEquals(9d, v4.lengthSquared(), DELTA,
                "The length squared of the vector is incorrect.");

        // TC02: The length squared of a unit vector
        assertEquals(1d, new Vector(1, 0, 0).lengthSquared(), DELTA,
                "The length squared of a unit vector should be 1.");

        // =============== Boundary Values Tests ==================

        // TC10: The length of a vector is the square root of the length squared
        assertEquals(Math.pow(v1.length(), 2), v1.lengthSquared(),
                "The length of a vector should be the square root of the length squared.");

    }

    /**
     * Test method for {@link primitives.Vector#add(primitives.Vector)}.
     */
    @Test
    void testAdd() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Adding two non-opposite vectors
        assertEquals(new Vector(1, 5, 1), v1.add(v3),
                "Adding two non-opposite vectors should result in the correct vector.");

        // =============== Boundary Values Tests ==================

        // TC10: Adding opposite vectors
        assertThrows(IllegalArgumentException.class, () -> v1.add(new Vector(-1, -2, -3)),
                "Adding opposite vectors should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Vector#subtract(primitives.Point)}.
     */
    @Test
    void testSubtract() {

        final Point p1 = new Point(1, 2, 3);
        final Point p2 = new Point(-5, 2, 49);
        // ============ Equivalence Partitions Tests ==============

        // TC01: Subtracting two points returns a vector from the second point to the first point
        assertEquals(new Vector(6, 0, -46), p1.subtract(p2),
                "Subtracting one point from another did not return the correct vector.");

        // =============== Boundary Values Tests ==================

        // TC10: Subtracting the same point should throw an exception
        assertThrows(IllegalArgumentException.class, () -> p1.subtract(p1),
                "Subtracting a point from itself should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Vector#scale(double)}.
     */
    @Test
    void testScale() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Scaling a vector by a positive number
        assertEquals(new Vector(2, 4, 6), v1.scale(2),
                "Scaling a vector by a positive number did not result in the correct vector.");

        // TC02: Scaling a vector by a negative number
        assertEquals(new Vector(-2, -4, -6), v1.scale(-2),
                "Scaling a vector by a negative number did not result in the correct vector.");

        // =============== Boundary Values Tests ==================

        // TC10: Scaling a vector by zero
        assertThrows(IllegalArgumentException.class, () -> v1.scale(0),
                "Scaling a vector by zero should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
     */
    @Test
    void testDotProduct() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Dot product of non-orthogonal, non-parallel vectors
        assertFalse(isZero(v1.dotProduct(v4)),
                "Dot product of non-orthogonal, non-parallel vectors should not be zero.");

        // =============== Boundary Values Tests ==================

        // TC10: Dot product of orthogonal vectors
        assertEquals(0d, v1.dotProduct(v3), DELTA,
                "Dot product of orthogonal vectors should be zero.");

        // TC20: Dot product of parallel vectors same direction
        assertEquals(28d, v1.dotProduct(new Vector(2, 4, 6)), DELTA,
                "Dot product of parallel vectors should be the product of their lengths.");

        // TC30: Dot product of parallel vectors opposite directions
        assertEquals(-28d, v1.dotProduct(v2), DELTA,
                "Dot product of parallel vectors with opposite directions should be the minus product of their lengths.");
    }

    /**
     * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
     */
    @Test
    void testCrossProduct() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Cross product of non-parallel vectors
        Vector crossProduct = v1.crossProduct(v3);
        assertTrue(isZero(crossProduct.dotProduct(v1)) && isZero(crossProduct.dotProduct(v3)),
                "Cross product of non-parallel vectors should be orthogonal to both.");

        // TC02: Cross product resulting in correct length
        assertTrue(isZero(v1.crossProduct(v3).length() - v1.length() * v3.length()),
                "Cross product did not result in a vector with the correct length.");

        // =============== Boundary Values Tests ==================

        // TC10: Cross product of parallel vectors
        assertThrows(IllegalArgumentException.class, () -> v1.crossProduct(v2),
                "Cross product of parallel vectors should throw an exception.");
    }

    /**
     * Test method for {@link primitives.Vector#normalize()}.
     */
    @Test
    void testNormalize() {
        // ============ Equivalence Partitions Tests ==============

        // TC01: Normalizing a vector
        Vector normalizedV1 = v1.normalize();
        assertTrue(isZero(normalizedV1.length() - 1),
                "Normalizing a vector did not result in a unit vector.");

        // =============== Boundary Values Tests ==================

        // TC10: Normalized vector direction
        assertTrue(v1.dotProduct(normalizedV1) > 0,
                "The normalized vector should have the same direction as the original vector.");
    }
}

package primitives;

/**
 * A class representing a matrix with methods for matrix manipulation, including
 * multiplication with vectors and other matrices, as well as generating a rotation matrix.
 * The matrix is stored as a 2D array of doubles.
 *
 * @author Benny Avrahami
 */
public class Matrix {
    private final double[][] data;

    /**
     * Constructor to create a matrix with the specified number of rows and columns.
     *
     * @param rows The number of rows in the matrix.
     * @param cols The number of columns in the matrix.
     */
    public Matrix(int rows, int cols) {
        data = new double[rows][cols];
    }

    /**
     * Constructor to create a matrix from a 2D array of doubles.
     *
     * @param data A 2D array representing the matrix.
     */
    public Matrix(double[][] data) {
        this.data = data;
    }

    /**
     * Creates a rotation matrix for rotating around the given axis by the specified angle.
     *
     * @param axis  The axis of rotation (a vector).
     * @param angle The angle by which to rotate (in radians).
     * @return A new rotation matrix.
     */
    public static Matrix rotationMatrix(Vector axis, double angle) {
        axis = axis.normalize();
        double x = axis.getX();
        double y = axis.getY();
        double z = axis.getZ();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double oneMinusCos = 1.0 - cos;

        double[][] rotationData = {
                {cos + x * x * oneMinusCos, x * y * oneMinusCos - z * sin, x * z * oneMinusCos + y * sin},
                {y * x * oneMinusCos + z * sin, cos + y * y * oneMinusCos, y * z * oneMinusCos - x * sin},
                {z * x * oneMinusCos - y * sin, z * y * oneMinusCos + x * sin, cos + z * z * oneMinusCos}
        };

        return new Matrix(rotationData);
    }

    /**
     * Gets the underlying 2D array representing the matrix.
     *
     * @return The matrix data.
     */
    public double[][] getData() {
        return data;
    }

    /**
     * Gets a specific element from the matrix.
     *
     * @param row The row index.
     * @param col The column index.
     * @return The value at the specified position.
     */
    public double get(int row, int col) {
        return data[row][col];
    }

    /**
     * Sets a specific element in the matrix.
     *
     * @param row   The row index.
     * @param col   The column index.
     * @param value The value to set at the specified position.
     */
    public void set(int row, int col, double value) {
        data[row][col] = value;
    }

    /**
     * Gets the number of rows in the matrix.
     *
     * @return The number of rows.
     */
    public int getRowCount() {
        return data.length;
    }

    /**
     * Gets the number of columns in the matrix.
     *
     * @return The number of columns.
     */
    public int getColCount() {
        return data[0].length;
    }

    /**
     * Multiplies the matrix with a vector.
     *
     * @param vector The vector to multiply with.
     * @return The resulting vector after multiplication.
     */
    public Vector multiply(Point vector) {
        double[] result = new double[3];
        double[] vecData = {vector.getX(), vector.getY(), vector.getZ()};

        for (int i = 0; i < 3; i++) {
            result[i] = 0;
            for (int j = 0; j < 3; j++) {
                result[i] += data[i][j] * vecData[j];
            }
        }

        return new Vector(result[0], result[1], result[2]);
    }

    /**
     * Multiplies the matrix with another matrix.
     *
     * @param other The other matrix to multiply with.
     * @return The resulting matrix after multiplication.
     */
    public Matrix multiply(Matrix other) {
        int rows = this.getRowCount();
        int cols = other.getColCount();
        int commonDim = this.getColCount();
        Matrix result = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0;
                for (int k = 0; k < commonDim; k++) {
                    sum += this.get(i, k) * other.get(k, j);
                }
                result.set(i, j, sum);
            }
        }
        return result;
    }

    /**
     * Returns a string representation of the matrix.
     *
     * @return A string representing the matrix.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : data) {
            for (double val : row) {
                sb.append(val).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

package primitives;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {

    @Test
    public void testRotationMatrix() {
        Vector axis = new Vector(0, 0, 1);
        double angle = Math.PI / 2; // 90 degrees

        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);

        double[][] expectedData = {
                {0, -1, 0},
                {1, 0, 0},
                {0, 0, 1}
        };

        double[][] actualData = rotationMatrix.getData();

        // Use tolerance for comparison
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(expectedData[i][j], actualData[i][j], 1e-9);
            }
        }
    }

    @Test
    public void testMatrixVectorMultiplication() {
        double[][] matrixData = {
                {1, 0, 0},
                {0, 1, 0},
                {0, 0, 1}
        };

        Matrix identityMatrix = new Matrix(matrixData);
        Vector vector = new Vector(1, 2, 3);

        Vector result = identityMatrix.multiply(vector);

        assertEquals(1, result.getX());
        assertEquals(2, result.getY());
        assertEquals(3, result.getZ());
    }

    @Test
    public void testMatrixMultiplication() {
        double[][] data1 = {{1, 2}, {3, 4}};
        double[][] data2 = {{2, 0}, {1, 2}};
        Matrix matrix1 = new Matrix(data1);
        Matrix matrix2 = new Matrix(data2);
        Matrix result = matrix1.multiply(matrix2);

        assertEquals(2, result.getRowCount(), "Result matrix row count should be 2");
        assertEquals(2, result.getColCount(), "Result matrix column count should be 2");
        assertEquals(4, result.get(0, 0), "Element at (0,0) should be 4");
        assertEquals(4, result.get(0, 1), "Element at (0,1) should be 4");
        assertEquals(10, result.get(1, 0), "Element at (1,0) should be 10");
        assertEquals(8, result.get(1, 1), "Element at (1,1) should be 8");
    }

    @Test
    public void testRotationMatrixMultiplication() {
        Vector axis = new Vector(0, 0, 1);
        double angle = Math.PI / 2; // 90 degrees

        Matrix rotationMatrix = Matrix.rotationMatrix(axis, angle);
        Vector vector = new Vector(1, 0, 0);

        Vector result = rotationMatrix.multiply(vector);

        assertEquals(0, result.getX(), 1e-9);
        assertEquals(1, result.getY(), 1e-9);
        assertEquals(0, result.getZ(), 1e-9);
    }
}

package renderer;

import geometries.Geometries;
import org.junit.jupiter.api.Test;
import primitives.Color;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ImageWriter.
 * Author: Benny Avrahami and Tzvi Yisrael
 */
class ImageWriterTest {

    /**
     * Test method for basic grid.
     */
    @Test
    void gridTest() {
        final int nX = 800, nY = 500, step = 50;
        final ImageWriter imageWriter = new ImageWriter("grid", nX, nY);

        final Color color1 = new Color(90, 30, 70);
        final Color color2 = new Color(34, 231, 20);
        for (int x = 0; x < nX; x++)
            for (int y = 0; y < nY; y++)
                imageWriter.writePixel(x, y, x % step == 0 || y % step == 0 ? color2 : color1);

        imageWriter.writeToImage();
    }
}
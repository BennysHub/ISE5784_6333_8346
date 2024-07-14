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
        ImageWriter imageWriter = new ImageWriter("grid", 800, 500);
        for (int x = 0; x < 800; x++)
            for (int y = 0; y < 500; y++)
                if (x % 50 == 0 || y % 50 == 0) {
                    imageWriter.writePixel(x, y, new Color(90, 30, 70));
                } else
                    imageWriter.writePixel(x, y, new Color(34, 231, 20));
        ;


        imageWriter.writeToImage();
    }
}
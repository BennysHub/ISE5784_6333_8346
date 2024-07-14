package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static org.junit.jupiter.api.Assertions.*;

class ImageWriterTest {

    @Test
    void gridTest() {
        ImageWriter imageWriter = new ImageWriter("grid", 800, 500);
        for (int x = 0; x < 800; x++)
            for (int y = 0; y < 500; y++)
                if (x % 50 == 0 || y % 50 == 0) {
                    imageWriter.writePixel(x, y, new Color(90, 30, 70));
                } else
                    imageWriter.writePixel(x, y, new Color(34, 231, 20)); ;


        imageWriter.writeToImage();
    }
}
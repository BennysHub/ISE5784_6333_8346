package renderer;

import primitives.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Image writer class combines accumulation of pixel color matrix and finally
 * producing a non-optimized jpeg image from this matrix. The class although is
 * responsible for holding image related parameters of View Plane - pixel matrix
 * size and resolution
 *
 * @author Dan
 */
public class ImageWriter {
    /**
     * Directory path for the image file generation - relative to the user
     * directory
     */
    private static final String FOLDER_PATH = System.getProperty("user.dir") + "/images";

    /**
     * Image generation buffer (the matrix of the pixels)
     */
    private final BufferedImage image;
    /**
     * image file name, not including the file extension '.png'
     */
    private final String imageName;
    /**
     * logger for reporting I/O failures
     */
    private final Logger logger = Logger.getLogger("ImageWriter");

    // ***************** Constructors ********************** //

    /**
     * Image Writer constructor accepting image name and View Plane parameters,
     *
     * @param imageName the name of png file
     * @param nX        number of pixels by Width
     * @param nY        number of pixels by height
     */
    public ImageWriter(String imageName, int nX, int nY) {
        this.imageName = imageName;
        image = new BufferedImage(nX, nY, BufferedImage.TYPE_INT_RGB);
    }

    // ***************** Getters/Setters ********************** //

    /**
     * View Plane Y axis resolution
     *
     * @return the number of vertical pixels
     */
    public int getNy() {
        return image.getHeight();
    }

    /**
     * View Plane X axis resolution
     *
     * @return the number of horizontal pixels
     */
    public int getNx() {
        return image.getWidth();
    }

    // ***************** Operations ******************** //

    /**
     * Function writeToImage produces an unoptimized png file of the image according
     * to
     * pixel color matrix in the directory of the project
     */
    public void writeToImage() {
        try {
            File file = new File(FOLDER_PATH + '/' + imageName + ".png");
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O error", e);
            throw new IllegalStateException("I/O error - may be missing directory " + FOLDER_PATH, e);
        }
    }

    /**
     * The function writePixel writes a color of a specific pixel into pixel color
     * matrix
     *
     * @param xIndex X-axis index of the pixel
     * @param yIndex Y-axis index of the pixel
     * @param color  final color of the pixel
     */
    public void writePixel(int xIndex, int yIndex, Color color) {
        image.setRGB(xIndex, yIndex, color.getColor().getRGB());
    }

}


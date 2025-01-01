package renderer;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a view plane in 3D rendering.
 * <p>
 * The view plane is a virtual plane where the 3D scene is projected for rendering.
 * It is defined by its orientation vectors, dimensions, resolution, and central position.
 * </p>
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>Defines the spatial arrangement of pixels on the view plane.</li>
 *   <li>Supports pixel position calculations in world coordinates.</li>
 *   <li>Encapsulates view plane dimensions and resolution for rendering calculations.</li>
 * </ul>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * Vector right = new Vector(1, 0, 0);
 * Vector up = new Vector(0, 1, 0);
 * Vector direction = new Vector(0, 0, -1);
 * Point center = new Point(0, 0, -5);
 * double height = 2;
 * double width = 2;
 * int nX = 800;
 * int nY = 800;
 *
 * ViewPlane viewPlane = new ViewPlane(right, up, direction, height, width, center, nX, nY);
 * Point pixelCenter = viewPlane.getPixelCenter(400, 400); // Calculate the center of a pixel.
 * }</pre>
 */
public class ViewPlane {

    /** The normal vector of the view plane, pointing toward the camera. */
    public final Vector direction;

    /** The vector pointing to the right of the view plane. */
    public final Vector right;

    /** The vector pointing upward on the view plane. */
    public final Vector up;

    /** The height of the view plane in world units. */
    public final double vpHeight;

    /** The width of the view plane in world units. */
    public final double vpWidth;

    /** The center point of the view plane in world coordinates. */
    public final Point center;

    /** The number of horizontal pixels in the view plane (width resolution). */
    public final int nX;

    /** The number of vertical pixels in the view plane (height resolution). */
    public final int nY;

    /** The width of a single pixel in world units. */
    public final double pixelWidth;

    /** The height of a single pixel in world units. */
    public final double pixelHeight;

    /**
     * Constructs a {@code ViewPlane} with specified orientation, size, position, and resolution.
     *
     * @param right     The vector pointing to the right of the view plane.
     * @param up        The vector pointing upward on the view plane.
     * @param direction The normal vector of the view plane, pointing towards the camera.
     * @param vpHeight  The height of the view plane in world units.
     * @param vpWidth   The width of the view plane in world units.
     * @param center    The center point of the view plane in world coordinates.
     * @param nX        The horizontal resolution (number of pixels along the width).
     * @param nY        The vertical resolution (number of pixels along the height).
     */
    public ViewPlane(Vector right, Vector up, Vector direction, double vpHeight, double vpWidth, Point center, int nX, int nY) {
        this.right = right;
        this.up = up;
        this.direction = direction;
        this.vpHeight = vpHeight;
        this.vpWidth = vpWidth;
        this.center = center;
        this.nX = nX;
        this.nY = nY;
        this.pixelWidth = vpWidth / nX;
        this.pixelHeight = vpHeight / nY;
    }

    /**
     * Calculates the center point of a specific pixel on the view plane.
     * <p>
     * This method converts pixel coordinates from the resolution grid to world coordinates
     * on the view plane.
     * </p>
     *
     * @param x The horizontal index of the pixel (0-based).
     * @param y The vertical index of the pixel (0-based).
     * @return The {@code Point} representing the center of the specified pixel in world coordinates.
     */
    public Point getPixelCenter(int x, int y) {
        final double ratioY = vpHeight / nY;
        final double ratioX = vpWidth / nX;

        // Calculate the pixel's position on the view plane
        final double xJ = (x - (nX - 1) / 2d) * ratioX;
        final double yI = -(y - (nY - 1) / 2d) * ratioY;

        // Starting from the center, move to the pixel's position
        return center.add(right.scale(xJ)).add(up.scale(yI));
    }
}

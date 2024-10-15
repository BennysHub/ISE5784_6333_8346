package renderer;

import primitives.Point;
import primitives.Vector;

public class ViewPlane {
    public final Vector right;
    public final Vector up;
    public final double vpHeight;
    public final double vpWidth;
    public final Point center; // viewing plane center point
    public final int nX;
    public final int nY;
    public final double pixelWidth;
    public final double pixelHeight;

    public ViewPlane(Vector right, Vector up, double vpHeight, double vpWidth, Point center, int nX, int nY) {
        this.right = right;
        this.up = up;
        this.vpHeight = vpHeight;
        this.vpWidth = vpWidth;
        this.center = center;
        this.nX = nX;
        this.nY = nY;
        this.pixelWidth = vpWidth / nX;
        this.pixelHeight = vpHeight / nY;
    }

    public  Point getPixelCenter(int x, int y){
        final double ratioY = vpHeight / nY;
        final double ratioX = vpWidth / nX;

        // Calculate the pixel's position on the view plane
        final double xJ = (x - (nX - 1) / 2d) * ratioX;
        final double yI = -(y - (nY - 1) / 2d) * ratioY;

        // Starting from the center, move to the pixel's position
        Point pixelCenter = center;
        if (xJ != 0) pixelCenter = pixelCenter.add(right.scale(xJ));
        if (yI != 0) pixelCenter = pixelCenter.add(up.scale(yI));
        return pixelCenter;
    }
}

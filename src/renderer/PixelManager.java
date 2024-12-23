package renderer;

/**
 * PixelManager is a helper class. It is used for multi-threading in the
 * renderer and
 * to follow up its progress.<br/>
 * A Camera uses one pixel manager object and several Pixel objects - one in
 * each thread.
 *
 * @author Dan Zilberstein
 */
class PixelManager {
    /**
     * Printing format
     */
    private static final String PRINT_FORMAT = "%5.1f%%\r";
    /**
     * Mutual exclusion object for synchronizing next pixel allocation between
     * threads
     */
    private final Object mutexNext = new Object();
    /**
     * Mutual exclusion object for printing progress percentage in a console window
     * by different threads
     */
    private final Object mutexPixels = new Object();
    /**
     * Maximum rows of pixels
     */
    private int maxRows = 0;
    /**
     * Maximum columns of pixels
     */
    private int maxCols = 0;
    /**
     * Total number of pixels in the generated image
     */
    private long totalPixels = 0L;
    /**
     * Currently processed row of pixels
     */
    private volatile int cRow = 0;
    /**
     * Currently processed column of pixels
     */
    private volatile int cCol = -1;
    /**
     * Number of pixels that have been processed
     */
    private volatile long pixels = 0L;
    /**
     * Last printed progress update percentage
     */
    private volatile int lastPrinted = 0;
    /**
     * Flag of debug printing progress percentage
     */
    private boolean print = false;
    /**
     * Progress percentage printing interval
     */
    private long printInterval = 100L;

    /**
     * Initialize pixel manager data for multi-threading
     *
     * @param maxRows  the number of pixel rows
     * @param maxCols  the number of pixel columns
     * @param interval print time interval in seconds, zero if printing is not
     *                 required
     */
    PixelManager(int maxRows, int maxCols, double interval) {
        this.maxRows = maxRows;
        this.maxCols = maxCols;
        totalPixels = (long) maxRows * maxCols;
        printInterval = (int) (interval * 10);
        if (print = printInterval != 0) System.out.printf(PRINT_FORMAT, 0d);
    }

    /**
     * Function for thread-safe manipulating of the main follow-up Pixel object - this
     * function is a critical section for all the threads, and the pixel manager data
     * is the shared data of this critical section.<br/>
     * The function provides the next available pixel number for each call.
     *
     * @return True if the next pixel is allocated, False if there are no more pixels
     */
    Pixel nextPixel() {
        synchronized (mutexNext) {
            if (cRow == maxRows) return null;

            ++cCol;
            if (cCol < maxCols)
                return new Pixel(cRow, cCol);

            cCol = 0;
            ++cRow;
            if (cRow < maxRows)
                return new Pixel(cRow, cCol);
        }
        return null;
    }

    /**
     * Finish pixel processing by updating and printing of progress percentage
     */
    void pixelDone() {
        boolean flag = false;
        int percentage = 0;
        synchronized (mutexPixels) {
            ++pixels;
            if (print) {
                percentage = (int) (1000L * pixels / totalPixels);
                if (percentage - lastPrinted >= printInterval) {
                    lastPrinted = percentage;
                    flag = true;
                }
            }
            if (flag) System.out.printf(PRINT_FORMAT, percentage / 10d);
        }
    }

    /**
     * Immutable class for object containing allocated pixel
     *
     * @param col for the x of the pixel
     * @param row for the y of the pixel
     */
    record Pixel(int col, int row) {
    }
}

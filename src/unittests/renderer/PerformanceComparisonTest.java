package renderer;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Vector;
import scene.Scene;
import scene.SceneJsonParser;

import java.util.concurrent.TimeUnit;

/**
 * PerformanceComparisonTest class for comparing the performance of different
 * camera render configurations.
 */
public class PerformanceComparisonTest {

    Scene scene = new SceneJsonParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene");
    Camera.Builder cameraBase = Camera.builder()
            .setPosition(new Point(-75, 60, -90))
            .setOrientation(new Vector(0, -0.2, -1), new Vector(0, 1, -0.2))
            .setViewPlaneDistance(150)
            .setViewPlaneSize(300, 300)
            .setScene(scene)
            .enableSoftShadows(false)
            .setResolution(600, 600)
            .setImageName("snowGlobeBuildTime");

    /**
     * Tests the performance of various camera configurations and prints the execution time
     * in hh:mm:ss:ms format.
     */
    @Test
    public void testPerformance() {
        long startTime, endTime, duration;

        // Testing base
        startTime = System.nanoTime();
        base();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("Base execution time: " + formatDuration(duration));

        // Testing multiThreading
        startTime = System.nanoTime();
        multiThreading();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("MultiThreading execution time: " + formatDuration(duration));

        // Testing CBR
        startTime = System.nanoTime();
        cbr();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("CBR execution time: " + formatDuration(duration));

        // Testing BVH
        startTime = System.nanoTime();
        bvh();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("BVH execution time: " + formatDuration(duration));

        // Testing BVH plus MultiThreading
        startTime = System.nanoTime();
        bvhPlusMultiThreading();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("BVH plus MultiThreading execution time: " + formatDuration(duration));

        // Testing BVH plus ParallelStreams
        startTime = System.nanoTime();
        bvhPlusParallelStreams();
        endTime = System.nanoTime();
        duration = endTime - startTime;
        System.out.println("BVH plus ParallelStreams execution time: " + formatDuration(duration));
    }

    /**
     * Formats the duration from nanoseconds to hh:mm:ss:ms format.
     *
     * @param duration The duration in nanoseconds.
     * @return The formatted duration string.
     */
    private String formatDuration(long duration) {
        long hours = TimeUnit.NANOSECONDS.toHours(duration);
        long minutes = TimeUnit.NANOSECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours);
        long milliseconds = TimeUnit.NANOSECONDS.toMillis(duration) - TimeUnit.SECONDS.toMillis(seconds) - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.HOURS.toMillis(hours);

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, milliseconds);
    }

    /**
     * Renders the image using the base camera configuration.
     */
    private void base() {
        Camera camera = cameraBase.build();
        camera.renderImage();
    }

    /**
     * Renders the image using the multi-threading camera configuration.
     */
    private void multiThreading() {
        Camera camera = cameraBase.enableMultiThreading(true).setThreadsCount(16).build();
        camera.renderImage();
    }

    /**
     * Renders the image using the CBR camera configuration.
     */
    private void cbr() {
        Camera camera = cameraBase.enableCBR(true).build();
        camera.renderImage();
    }

    /**
     * Renders the image using the BVH camera configuration.
     */
    private void bvh() {
        Camera camera = cameraBase.enableBVH(true).build();
        camera.renderImage();
    }

    /**
     * Renders the image using the BVH plus multi-threading camera configuration.
     */
    private void bvhPlusMultiThreading() {
        Camera camera = cameraBase
                .setScene(new SceneJsonParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene"))  // build BVH again
                .enableMultiThreading(true)
                .setThreadsCount(16)
                .enableBVH(true)
                .build();
        camera.renderImage();
    }

    /**
     * Renders the image using the BVH plus parallel-streams camera configuration.
     */
    private void bvhPlusParallelStreams() {
        Camera camera = cameraBase
                .setScene(new SceneJsonParser("src/unittests/renderer/json/snowGlobe.json", "Test Scene"))  // build BVH again
                .enableParallelStreams(true)
                .enableBVH(true)
                .build();
        camera.renderImage();
    }
}

package nl.tudelft.sp.modelchecker.videoprocessing.lib;

import java.io.IOException;
import net.bramp.ffmpeg.FFprobe;

public interface Loader {

    /**
     * Load library based on context.
     */
    void loadLibrary() throws IOException;

    /**
     * get FFprobe.
     *
     * @return FFprobe
     */
    FFprobe getFFprobe();

}

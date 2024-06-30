package nl.tudelft.sp.modelchecker.videoprocessing.lib;

import java.io.IOException;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.stereotype.Service;

@Service
public class WindowsLoader implements Loader {

    private static WindowsLoader instance;
    private final String ffmpegPath = "C:/ffmpeg/bin";
    private FFprobe ffprobe;

    private WindowsLoader() {
    }

    /**
     * create new instance of loader.
     *
     * @return WindowsLoader
     */
    public static WindowsLoader getInstance() {
        if (instance == null) {
            synchronized (WindowsLoader.class) {
                if (instance == null) {
                    instance = new WindowsLoader();
                }
            }
        }
        return instance;
    }

    /**
     * Load concrete implementation of library.
     */
    @Override
    public void loadLibrary() throws IOException {
        ffprobe = new FFprobe(ffmpegPath + "/ffprobe");
    }

    @Override
    public FFprobe getFFprobe() {
        return ffprobe;
    }
}

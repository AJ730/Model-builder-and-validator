package nl.tudelft.sp.modelchecker.videoprocessing.lib;

import java.io.IOException;
import net.bramp.ffmpeg.FFprobe;
import org.springframework.stereotype.Service;

@Service
public class LinuxLoader implements Loader {

    private static LinuxLoader instance;
    private final String ffmpegPath = "/usr/bin";
    private FFprobe ffprobe;

    private LinuxLoader() {
    }


    /**
     * create new instance of loader.
     *
     * @return LinuxLoader
     */
    public static LinuxLoader getInstance() {
        if (instance == null) {
            synchronized (LinuxLoader.class) {
                if (instance == null) {
                    instance = new LinuxLoader();
                }
            }
        }

        return instance;
    }


    @Override
    public void loadLibrary() throws IOException {
        ffprobe = new FFprobe(ffmpegPath + "/ffprobe");
    }

    @Override
    public FFprobe getFFprobe() {
        return ffprobe;
    }
}

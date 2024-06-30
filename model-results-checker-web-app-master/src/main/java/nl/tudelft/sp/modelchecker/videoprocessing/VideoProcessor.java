package nl.tudelft.sp.modelchecker.videoprocessing;

import static nl.tudelft.sp.modelchecker.videoprocessing.factory.LoaderFactory.getLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.transaction.NotSupportedException;
import lombok.Getter;
import net.bramp.ffmpeg.FFprobe;
import nl.tudelft.sp.modelchecker.videoprocessing.lib.Loader;
import nl.tudelft.sp.modelchecker.videoprocessing.servicebeans.FpsExtractor;
import org.h2.store.fs.FileUtils;

public class VideoProcessor {

    private String videoPath;

    @Getter
    private double fps;

    /**
     * execute video processor to get the fps.
     *
     * @param inputStream inputStream
     * @throws IOException IOException
     * @throws NotSupportedException NotSupportedException
     */
    public void run(InputStream inputStream)
            throws IOException, NotSupportedException {

        createPaths(inputStream);
        getFps(videoPath);
        cleanup();
    }

    /**
     * Cleanup the files afterwards.
     */
    private void cleanup() {
        FileUtils.delete(videoPath);
    }

    private void getFps(String inputFile)
            throws IOException, NotSupportedException {

        Loader loader = getLoader();
        loader.loadLibrary();

        FFprobe ffprobe = loader.getFFprobe();

        FpsExtractor fpsExtractor = new FpsExtractor(ffprobe);
        fpsExtractor.extract(inputFile);
        fps = fpsExtractor.getFps();
    }


    private void createPaths(InputStream inputStream) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String dataPath = System.getProperty("user.dir") + "/data/";
        videoPath = dataPath + "video" + uuid + ".mp4";
        Files.copy(inputStream, Paths.get(videoPath),
                StandardCopyOption.REPLACE_EXISTING);

    }
}

package nl.tudelft.sp.modelchecker.videoprocessing.servicebeans;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import nl.tudelft.sp.modelchecker.videoprocessing.services.Extractor;

@Setter
@Getter
public class FpsExtractor implements Extractor {

    FFprobe ffprobe;
    Double fps;

    /**
     * constructor.
     *
     * @param ffprobe ffprobe
     */
    public FpsExtractor(FFprobe ffprobe) {
        this.ffprobe = ffprobe;
    }


    /**
     * extract fps of the video.
     *
     * @param inputFile inputFile
     * @throws IOException IOException
     */
    public void extract(String inputFile) throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe(inputFile);
        FFmpegStream stream = probeResult.getStreams().get(0);
        this.fps = stream.avg_frame_rate.doubleValue();
    }
}

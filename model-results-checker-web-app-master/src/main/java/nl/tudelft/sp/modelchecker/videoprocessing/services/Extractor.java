package nl.tudelft.sp.modelchecker.videoprocessing.services;

import java.io.IOException;

public interface Extractor {

    /**
     * extract fps of the video.
     *
     * @param inputFile inputFile
     * @throws IOException IOException
     */
    void extract(String inputFile) throws IOException;
}

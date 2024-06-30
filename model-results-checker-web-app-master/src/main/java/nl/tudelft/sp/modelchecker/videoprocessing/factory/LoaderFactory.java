package nl.tudelft.sp.modelchecker.videoprocessing.factory;

import javax.transaction.NotSupportedException;
import lombok.NoArgsConstructor;
import nl.tudelft.sp.modelchecker.videoprocessing.lib.LinuxLoader;
import nl.tudelft.sp.modelchecker.videoprocessing.lib.Loader;
import nl.tudelft.sp.modelchecker.videoprocessing.lib.WindowsLoader;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Service
public class LoaderFactory {

    /**
     * Get a specific loader.
     *
     * @return loader
     * @throws NotSupportedException NotSupportedException
     */
    public static Loader getLoader() throws NotSupportedException {
        if (SystemUtils.IS_OS_LINUX) {
            return LinuxLoader.getInstance();
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            return WindowsLoader.getInstance();
        }
        throw new NotSupportedException("Only Linux and Windows are supported");
    }
}

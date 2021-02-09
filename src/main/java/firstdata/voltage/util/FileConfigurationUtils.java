package firstdata.voltage.util;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstdata.voltage.exceptions.VoltageConfigurationException;

public class FileConfigurationUtils {
    
    private static final Logger LOG = LoggerFactory.getLogger(FileConfigurationUtils.class);
    
    /**
     * Close Stream
     *
     */
    public static void closeInputStream(InputStream reader) throws VoltageConfigurationException {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) { 
                LOG.error("Failed closing the input stream");
            }
        }
    }

}

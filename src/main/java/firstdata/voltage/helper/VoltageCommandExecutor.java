package firstdata.voltage.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstdata.voltage.configuration.VoltageHelperConfiguration;
import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.exceptions.VoltageExecutionException;
import com.voltage.securedata.enterprise.VeException;

public class VoltageCommandExecutor {

    private static final int VE_ERROR_CODE_NETWORK_ERROR = 572;
    private static final int VE_ERROR_CODE_TIMEOUT = 590;
    private static final Logger LOG = LoggerFactory.getLogger(VoltageCommandExecutor.class);
    
    /**
     * Command Executor
     *
     */    
    public static Object execute(VoltageCallbackCommand command,Object... obj) throws VoltageExecutionException, VoltageConfigurationException {
        VoltageHelperConfiguration config = VoltageHelperConfiguration.getInstance();
        int tryCount = 0;
        Object result = null;
        do {
            try {
                result = command.execute(obj);
            } catch (VeException ve) {
                if (! isRetryRequired(ve.getErrorCode()) || tryCount == config.getConnectionTimeoutRetryCount()) {
                    throw new VoltageExecutionException(tryCount, "Failed to execute after " + tryCount + " attempts", ve);
                }
                tryCount++;
                sleep(config.getConnectionTimeoutRetryIntervalMillis());
            }
        } while (result == null);     
        return result;
    }
    
    /**
     * Check if retry required
     *
     */

    private static boolean isRetryRequired(int errorCode) throws VoltageConfigurationException {
        return (VoltageHelperConfiguration.getInstance().isConnectionTimeoutRetry() && isConnectionTimeoutError(errorCode));
    }

    /**
     * Check if connection timeout error
     *
     */
    
    private static boolean isConnectionTimeoutError(int errorCode) {
        return (errorCode == VE_ERROR_CODE_NETWORK_ERROR || errorCode == VE_ERROR_CODE_TIMEOUT);
    }
    
    private static void sleep(int intervalInMillis) {
        try {
            Thread.sleep(intervalInMillis);
        } catch (InterruptedException ie) {
            LOG.error("thread sleep interrupted during rery wait interval...");
        }        
    }    
}

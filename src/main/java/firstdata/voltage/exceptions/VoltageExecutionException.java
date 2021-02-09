package firstdata.voltage.exceptions;

/**
 * Created by f3cjg6l on 10/12/16.
 */
public class VoltageExecutionException extends VoltageException {
    
    private int attempts;
    
    public VoltageExecutionException(int tryCount, String message, Throwable cause) {
        super(message, cause);
        attempts = tryCount;
    }

    public int getAttempts() {
        return attempts;
    }   
}

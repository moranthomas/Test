package firstdata.voltage.exceptions;


public class VoltageException extends VoltageUtilsException {

    private static final long serialVersionUID = -5944729689947033267L;
    
    public VoltageException(String message) {
        super(message);
    }
    
    public VoltageException(String message, Throwable exp) {
        super(message, exp);
    }
}
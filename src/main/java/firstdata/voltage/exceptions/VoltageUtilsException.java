package firstdata.voltage.exceptions;

public class VoltageUtilsException extends Exception {

    private static final long serialVersionUID = -5944729689947033267L;
    
    public VoltageUtilsException(String message) {
        super(message);
    }
    
    public VoltageUtilsException(String message, Throwable exp) {
        super(message, exp);
    }
  
    public VoltageUtilsException(String message, Object[] objects) {
         super(String.format(message, objects));        
    }
    
    public VoltageUtilsException(String message, Throwable exp, Object... obj) {
        super(String.format(message, obj), exp);
    }
}

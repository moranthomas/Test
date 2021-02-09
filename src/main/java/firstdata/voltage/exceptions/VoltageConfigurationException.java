package firstdata.voltage.exceptions;

public class VoltageConfigurationException extends VoltageException {

    private static final long serialVersionUID = -5944729689947033267L;

    public VoltageConfigurationException(String message) {
        super(message);
    }

    public VoltageConfigurationException(String message, Throwable exp) {
        super(message, exp);
    }
}
package firstdata.voltage.helper;

import com.voltage.securedata.enterprise.VeException;

public interface VoltageCallbackCommand {
    Object execute(Object... obj) throws VeException;
}

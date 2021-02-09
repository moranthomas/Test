package firstdata.voltage.helper;

import com.voltage.securedata.enterprise.FPE;
import com.voltage.securedata.enterprise.VeException;

public class VoltageFpeProtectAccessCommand implements VoltageCallbackCommand {

    /**
     * FPE Protect/Access Executor
     *
     */
    public Object execute(Object... obj) throws VeException {
        if ((((String) obj[2]).equals("protect"))) {
            return (String)((FPE) obj[0]).protect((String) obj[1]);
        }
        return (String)((FPE) obj[0]).access((String) obj[1]);
    }
}

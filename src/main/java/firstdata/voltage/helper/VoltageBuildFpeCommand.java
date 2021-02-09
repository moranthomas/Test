package firstdata.voltage.helper;

import com.voltage.securedata.enterprise.LibraryContext;
import com.voltage.securedata.enterprise.VeException;

public class VoltageBuildFpeCommand implements VoltageCallbackCommand {
    
    /**
     * FPE Builder
     *
     */
    public Object execute(Object... obj) throws VeException {
        LibraryContext libraryCtxt = (LibraryContext) obj[0];
                
        return libraryCtxt.getFPEBuilder((String) obj[1]).setIdentity((String) obj[2])
        .setUsernamePassword((String) obj[3], (String) obj[4]).build();
    }
}

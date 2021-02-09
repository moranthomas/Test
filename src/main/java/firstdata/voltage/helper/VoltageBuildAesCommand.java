package firstdata.voltage.helper;

import com.voltage.securedata.enterprise.LibraryContext;
import com.voltage.securedata.enterprise.VeException;

public class VoltageBuildAesCommand implements VoltageCallbackCommand {
    
    /**
     * AES Builder
     *
     */
    public Object execute(Object... obj) throws VeException {
        LibraryContext libraryCtxt = (LibraryContext) obj[0];
                
        return libraryCtxt.getAESBuilder().setIdentity((String) obj[1])
        .setUsernamePassword((String) obj[2], (String) obj[3]).build();
    }
}

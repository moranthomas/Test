package firstdata.voltage.helper;

import com.voltage.securedata.enterprise.LibraryContext;
import com.voltage.securedata.enterprise.VeException;

public class VoltageBuildLibContextCommand implements VoltageCallbackCommand {
    
    /**
     * Build Library Context
     *
     */
    public Object execute(Object... obj) throws VeException { 
        LibraryContext libraryCtxt;
        if (((String) obj[1]).isEmpty()) {
            libraryCtxt = (LibraryContext) new LibraryContext.Builder().setPolicyURL((String) obj[0])
                    .setTrustStorePath((String) obj[2]).build();
        } else {
            libraryCtxt = (LibraryContext) new LibraryContext.Builder().setPolicyURL((String) obj[0])
                .setFileCachePath((String) obj[1]).setTrustStorePath((String) obj[2]).build();
        }
        return libraryCtxt;
    }
}

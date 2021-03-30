package Guice;

import Guice.impl.DefaultCommunicator;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Communication {

    @Inject
    private DefaultCommunicator communicator;

    public Communication(Boolean keepRecords) {
        if (keepRecords) {
           log.info("keeping records");
        }
    }

    public boolean sendMessage(String message) {

        return communicator.sendMessage(message);
    }

    public DefaultCommunicator getCommunicator() {
            return this.communicator;
    }

}
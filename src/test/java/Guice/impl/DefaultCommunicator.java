package Guice.impl;

import Guice.CommunicationMode;
import Guice.Communicator;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultCommunicator implements Communicator {


    @Inject
    //@Named("EmailComms")
    CommunicationMode emailCommsMode;

    public DefaultCommunicator() {

    }

    public boolean sendMessage(String message) {
       log.info("Message Sent: {}", message );
        return true;
    }

}

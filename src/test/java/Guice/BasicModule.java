package Guice;

import com.google.inject.AbstractModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(Communication.class).toConstructor(Communication.class.getConstructor(Boolean.class));
            bind(CommunicationMode.class).to(EmailCommunicationMode.class);
            bind(Boolean.class).toInstance(true);
        } catch (NoSuchMethodException ex) {
            log.error("" + ex);
        } catch (SecurityException ex) {
            log.error("" + ex);
        }
    }
}
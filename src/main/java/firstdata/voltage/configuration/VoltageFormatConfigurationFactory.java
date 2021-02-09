package firstdata.voltage.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.util.VoltageConstants;

public class VoltageFormatConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(VoltageFormatConfigurationFactory.class);

    public static FormatConfiguration create() throws VoltageConfigurationException {

        LOG.debug("Creating Voltage configuration handler instance..");
        VoltageHelperConfiguration fileConfigInstance = VoltageHelperConfiguration.getInstance();
        FormatConfiguration formatConfiguration = null;
        try {
            String className = fileConfigInstance.getConfigurationHandlerClassName();
            LOG.debug("class name in factory:" + className + ":");

            if (className.trim().length() == 0) {
                LOG.debug("Creating default handler");
                formatConfiguration = new VoltageFormatConfiguration();
            } else {
                LOG.debug("Creating custom handler");
                formatConfiguration = newConfigurationClass(className);
            }
        } catch (VoltageConfigurationException e) {
            LOG.error("Configuration Loading Failed in Factory", e);
            throw new VoltageConfigurationException(VoltageConstants.ERR_FAILED_LOADING_FORMAT_CONFIGURATION_FACTORY, e);
        }
        return formatConfiguration;
    }

    private static FormatConfiguration newConfigurationClass(final String className) throws VoltageConfigurationException {

        FormatConfiguration formatConfiguration = null;
        LOG.debug("Loading custom handler class " + className);
        try {
            Class<?> newClass = Class.forName(className);
            if (!FormatConfiguration.class.isAssignableFrom(newClass)) {
                LOG.error("The Custom class doesn't implement the FormatConfiguration interface");
                throw new VoltageConfigurationException(VoltageConstants.ERR_INTERFACE_IMPLEMENTATION_FAILURE);
            }
            formatConfiguration = (FormatConfiguration) newClass.newInstance();
        } catch (ClassNotFoundException ce) {
            LOG.error("Failed to load user defined class : Class Not Found", ce);
            throw new VoltageConfigurationException(VoltageConstants.ERR_FAILED_LOADING_USR_DEFINED_CLASS, ce);
        }
        catch (InstantiationException ie) {
            LOG.error("Failed to load user defined class : InstantiationException", ie);
            throw new VoltageConfigurationException(VoltageConstants.ERR_FAILED_LOADING_USR_DEFINED_CLASS, ie);
        }
        catch (IllegalAccessException ie) {
            LOG.error("Failed to load user defined class : IllegalAccessException", ie);
            throw new VoltageConfigurationException(VoltageConstants.ERR_FAILED_LOADING_USR_DEFINED_CLASS, ie);
        }
        return formatConfiguration;
    }
}

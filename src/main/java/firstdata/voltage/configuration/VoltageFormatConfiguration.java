package firstdata.voltage.configuration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcalonline.util.DCalPropertiesMgr;
import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.util.FileConfigurationUtils;
import com.fdc.security.util.AppnSecurityAPI;

public class VoltageFormatConfiguration implements FormatConfiguration {

    static final Logger LOG = LoggerFactory.getLogger(VoltageFormatConfiguration.class);
    private final String configFormat = System.getProperty("voltage.formatConfiguration",
            "dcalDefaults.properties");
    private Map<String, VoltageFormat> formatMap = new HashMap<String, VoltageFormat>();
  //  private Properties formatLoaderProperty = new Properties();

    public VoltageFormatConfiguration() throws VoltageConfigurationException {
        InputStream loadInputStream = null;
        Properties formatLoaderProperty = new Properties();
        
        LOG.debug("Loading Format Configuration File From Classpath.." + configFormat);
        try {
            
           // loadInputStream = stream(configFormat);
            //formatLoaderProperty.load(loadInputStream);

        } /*catch (IOException e) {
            LOG.error("Failed to Load Voltage Configuration File:",e);
            throw new VoltageConfigurationException(VoltageConstants.ERR_FAILED_LOADING_VOLTAGE_FORMAT_FILE,e);
        }*/ finally {
            FileConfigurationUtils.closeInputStream(loadInputStream);
        }
        //this.formatLoaderProperty = formatLoaderProperty;

        LOG.debug("Constructor Completed");
    }

    /*private static InputStream stream(String file) throws IOException, VoltageConfigurationException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (inputStream == null) {
            LOG.error("Format Configuration File not found");
            throw new VoltageConfigurationException("Format Configuration File not found");
        }
        return inputStream;
    }*/

    /**
     * Get Voltage Format Configuration
     *
     */
    public VoltageFormat getFormat(String dataFormat) {

        VoltageFormat getConfigFormat;
        if (formatMap.containsKey(dataFormat)) {
            LOG.debug("Fetching Format Object for: " + AppnSecurityAPI.encoder().canonicalize(dataFormat));
            getConfigFormat = formatMap.get(dataFormat);
        } else {
        	dataFormat = AppnSecurityAPI.encoder().canonicalize(dataFormat);
            String format = DCalPropertiesMgr.getString(dataFormat + "_Format");
            LOG.debug("Format not found in Map..Creating Format Bean for Format : " + AppnSecurityAPI.encoder().canonicalize(format));
            VoltageFormat voltageFormat = new VoltageFormat();
            voltageFormat.setFormat(format);
            String identity = DCalPropertiesMgr.getString(dataFormat + "_Identity");
            voltageFormat.setIdentity(identity);
            String minLength = DCalPropertiesMgr.getString(dataFormat + "_MinLength");
            voltageFormat.setMinLength(minLength);
            String maxLength = DCalPropertiesMgr.getString(dataFormat + "_MaxLength");
            voltageFormat.setMaxLength(maxLength);
            String lengthInputSpec = DCalPropertiesMgr.getString(dataFormat + "_LengthCheck");
            voltageFormat.setLengthInputSpec(lengthInputSpec);
            String inputSpec = DCalPropertiesMgr.getString(dataFormat + "_InputSpec");
            voltageFormat.setInputSpec(inputSpec);
            String requiresPadding = DCalPropertiesMgr.getString(dataFormat + "_RequiresPadding");
            voltageFormat.setPadding(requiresPadding);
            String requiresEncoding = DCalPropertiesMgr.getString(dataFormat + "_Requires_Base64_Encoding");
            voltageFormat.setbase64Encoding(requiresEncoding);
            String paddingLeft = DCalPropertiesMgr.getString(dataFormat + "_Pad_Left");
            voltageFormat.setPaddingLeft(paddingLeft);
            String paddingChar = DCalPropertiesMgr.getString(dataFormat + "_Padding_Char");
            voltageFormat.setPaddingChar(paddingChar);
            
           /* String format = formatLoaderProperty.getProperty(dataFormat + "_Format");
            LOG.debug("Format not found in Map..Creating Format Bean for Format : " + format);
            VoltageFormat voltageFormat = new VoltageFormat();
            voltageFormat.setFormat(format);
            String identity = formatLoaderProperty.getProperty(dataFormat + "_Identity");
            voltageFormat.setIdentity(identity);
            String minLength = formatLoaderProperty.getProperty(dataFormat + "_MinLength");
            voltageFormat.setMinLength(minLength);
            String maxLength = formatLoaderProperty.getProperty(dataFormat + "_MaxLength");
            voltageFormat.setMaxLength(maxLength);
            String lengthInputSpec = formatLoaderProperty.getProperty(dataFormat + "_LengthCheck");
            voltageFormat.setLengthInputSpec(lengthInputSpec);
            String inputSpec = formatLoaderProperty.getProperty(dataFormat + "_InputSpec");
            voltageFormat.setInputSpec(inputSpec);
            String requiresPadding = formatLoaderProperty.getProperty(dataFormat + "_RequiresPadding");
            voltageFormat.setPadding(requiresPadding);
            String requiresEncoding = formatLoaderProperty.getProperty(dataFormat + "_Requires_Base64_Encoding");
            voltageFormat.setbase64Encoding(requiresEncoding);
            String paddingLeft = formatLoaderProperty.getProperty(dataFormat + "_Pad_Left");
            voltageFormat.setPaddingLeft(paddingLeft);
            String paddingChar = formatLoaderProperty.getProperty(dataFormat + "_Padding_Char");
            voltageFormat.setPaddingChar(paddingChar);*/

            LOG.debug("Adding object to format map");
            formatMap.put(dataFormat, voltageFormat);
            getConfigFormat = formatMap.get(dataFormat);
        }
        return getConfigFormat;
    }

}

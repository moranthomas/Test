package firstdata.voltage.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcalonline.util.DCalPropertiesMgr;
import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.util.FileConfigurationUtils;
import com.firstdata.voltage.util.VoltageConstants;
//import com.dcalonline.util.voltage.VoltageUtils.CryptoUtil;
//import com.dcalonline.util.voltage.exception.CryptoException;

public class VoltageHelperConfiguration {

    static final Logger LOG = LoggerFactory.getLogger(VoltageHelperConfiguration.class);
   // private final String configFile = System.getProperty("voltage.configuration", "dcalDefaults.properties");
    //private PropertiesConfiguration globalProperty = new PropertiesConfiguration();
    private static VoltageHelperConfiguration configInstance;
    private final String keyClientPolicyUrl = "ClientPolicyURL";
    private final String keyCachePath = "CachePath";
    private final String keyTrustStorePath = "TrustStorePath";
    private final String keyVoltageAccountId = "VoltageAccountId";
    private final String keyVoltageAuthCode = "VoltageAuthCode";
    private final String keyInputSpecValidation = "InputSpecValidation";
    private final String keyConnectionTimOutRetry = "ConnectionTimeoutRetry";
    private final String keyConnectionTimeOutRetryCount = "ConnectionTimeoutRetryCount";
    private final String keyConnectionTimeoutIntervalMillis = "ConnectionTimeoutRetryIntervalMillis";
    private final String keyConfigHandlerClassName = "VoltageConfigurationHandler";
    
    private String clientPolicyUrl;
    private String trustStorePath;
    private String cachePath;
    private String voltageAccountId;
    private String voltagePassword;
    private boolean requiresInputValidation;
    private boolean connectionTimeoutRetry = false;
    private int connectionTimeoutRetryCount;
    private int connectionTimeoutDefaultRetryCount = 3;
    private int connectionTimeoutRetryIntervalMillis;
    private int connectionTimeoutRetryDefaultIntervalMillis = 1000;
    private String configurationHandlerClassName;

    public static VoltageHelperConfiguration getInstance() throws VoltageConfigurationException {

        synchronized (VoltageHelperConfiguration.class) {
            if (configInstance == null) {
                try {
                    configInstance = new VoltageHelperConfiguration();
                } catch (VoltageConfigurationException e) {
                    LOG.error("Voltage File Configuration Initialization Failed ", e);
                    throw new VoltageConfigurationException("Voltage File Configuration Initialization Failed", e);
                }
            }
        }
        return configInstance;
    }
    
    public static void cleanUp() {
        VoltageHelperConfiguration.configInstance = null;
    }

    private VoltageHelperConfiguration() throws VoltageConfigurationException {        
        loadConfiguration();
        validateConfiguration();
    }

    public String getClientPolicyUrl() {
        return clientPolicyUrl;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getCachePath() {
        return cachePath;
    }

    public String getVoltageAccountId() {
        return voltageAccountId;
    }

    public String getVoltagePassword() {
        return voltagePassword;
    }

    public boolean requiresInputValidation() {
        return requiresInputValidation;
    }

    public boolean isConnectionTimeoutRetry() {
        return connectionTimeoutRetry;
    }

    public int getConnectionTimeoutRetryCount() {        
        if (validateRetryCount(1,15,connectionTimeoutRetryCount)) {
            return connectionTimeoutRetryCount;
        }
        return connectionTimeoutDefaultRetryCount;
    }

    public int getConnectionTimeoutRetryIntervalMillis() {
        if (validateRetryIntervalMillis(200,3000,connectionTimeoutRetryIntervalMillis)) {
            return connectionTimeoutRetryIntervalMillis;
        }
        return connectionTimeoutRetryDefaultIntervalMillis;
    }
    
    public String getConfigurationHandlerClassName() {
        return configurationHandlerClassName;
    }
    
    private int getIntValue(String value) {
        int intValue = 0;
        if (!value.isEmpty()) {
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException ne) {
                LOG.error("Invalid value where number expected : " + value);
            }
        }
        return intValue;
    }

    /**
     * Load Voltage Configuration
     *
     */
    protected void loadConfiguration() throws VoltageConfigurationException {
        InputStream inputStream = null;
        Properties configProperty = new Properties();
        PropertiesConfiguration globalProperty = null;        
      //  LOG.debug("Voltage Config file:-" + configFile);
        LOG.debug("Loading Configuration File From Classpath..");
        try {
           // inputStream = stream(configFile);
            //configProperty.load(inputStream);
        	voltagePassword = DCalPropertiesMgr.getString(keyVoltageAuthCode,"");
            
        	//String password = configProperty.getProperty(keyVoltagePassword);
            
            if (voltagePassword == null || voltagePassword.isEmpty()) {
                LOG.error("Password Cannot be Null..");
                throw new VoltageConfigurationException("Password Cannot be Null..");
            }
            //String filePath = getFilePath(configFile);
            //globalProperty = new PropertiesConfiguration(filePath);

          /*  if (!password.startsWith("crypt:")) {
                LOG.debug("Clear Text Password Found.. Encrypting");
                CryptoUtil crypto = CryptoUtil.getInstance();
                final String encryptedPswd = crypto.encrypt(password);
                LOG.debug("Password Encrypted..");

                LOG.debug("Updating Encrypted Password into the Configuration File");

                globalProperty = new PropertiesConfiguration(filePath);
                globalProperty.setProperty(keyVoltagePassword, encryptedPswd);
                globalProperty.save();

                FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
                globalProperty.setReloadingStrategy(strategy);
                LOG.debug("Voltage Credentials Encrypted and Saved");

                LOG.debug("Successfully Updated Encrypted Password into the Configuration File");
            }*/
        } /*catch (IOException io) {
            LOG.error("Failed to Load Voltage Helper Configuration Constructor:", io);
            throw new VoltageConfigurationException(VoltageConstants.ERR_VOLTAGE_HELPER_CONFIG_FAILED, io);
        } */
       /* catch (ConfigurationException ce) {
            LOG.error("Failed to Load Voltage Helper Configuration Constructor:", ce);
            throw new VoltageConfigurationException(VoltageConstants.ERR_VOLTAGE_HELPER_CONFIG_FAILED, ce);
        } 
        catch (CryptoException crype) {
            LOG.error("Failed to Load Voltage Helper Configuration Constructor:", crype);
            throw new VoltageConfigurationException(VoltageConstants.ERR_VOLTAGE_HELPER_CONFIG_FAILED, crype);
        }*/ finally {
            FileConfigurationUtils.closeInputStream(inputStream);
        }
       // this.globalProperty = globalProperty;
        LOG.debug("Loading Properties");
        clientPolicyUrl = getProperty(keyClientPolicyUrl);
        LOG.debug("Client Policy URL-->" + clientPolicyUrl);
        cachePath = getProperty(keyCachePath);
        trustStorePath = getProperty(keyTrustStorePath);
        voltageAccountId = getProperty(keyVoltageAccountId);
        requiresInputValidation = isRequiresValidation();
        connectionTimeoutRetry = isRetry();
        connectionTimeoutRetryCount =  getIntValue(getProperty(keyConnectionTimeOutRetryCount));
        connectionTimeoutRetryIntervalMillis = getIntValue(getProperty(keyConnectionTimeoutIntervalMillis));
        configurationHandlerClassName = getProperty(keyConfigHandlerClassName);
        
        LOG.debug("Constructor Completed");        
    }
    
    protected String getFilePath(String configurationfile) throws IOException {

        String[] fileSplit = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String file = loader.getResource(configurationfile).toString();
        String operatingSystem = System.getProperty("os.name");

        if (operatingSystem.startsWith("Win")) {
            fileSplit = file.split("file:/");
        } else {
            fileSplit = file.split("file:");
        }
        return fileSplit[1];
    }
    
    private boolean isRetry() {
        return !getProperty(keyConnectionTimOutRetry).equals("") && "Yes".equalsIgnoreCase(getProperty(keyConnectionTimOutRetry)) ? true : false ;
    }
    
    private boolean isRequiresValidation() {
        return !getProperty(keyInputSpecValidation).equals("") && "Yes".equalsIgnoreCase(getProperty(keyInputSpecValidation)) ? true : false ;
    }

    private static InputStream stream(String file) throws IOException,FileNotFoundException, VoltageConfigurationException {  

    	/*File file1 = new File("VoltageConfiguration.properties");
    	String name = file1.getAbsolutePath();*/
    	
    	//InputStream inputStream = VoltageHelperConfiguration.class.getResourceAsStream(file); 
    	 // InputStream inputStream = VoltageHelperConfiguration.class.getClassLoader().getResourceAsStream(file);
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        if (inputStream == null) {
            LOG.error("Configuration File not found");
            throw new VoltageConfigurationException("Configuration File not found");
        }
        return inputStream;
    }

   /* private String getProperty(String key) {
        if (globalProperty.getProperty(key) != null) {            
            return globalProperty.getProperty(key).toString();
        }
        return "";
    }  */  
    
    private String getProperty(String key) {
        if (DCalPropertiesMgr.getString(key) != null) {            
            return DCalPropertiesMgr.getString(key).toString();
        }
        return "";
    }  
    
    private void validateConfiguration() throws VoltageConfigurationException {
        validateCredentialsAreSpecified();
        validateCachePathAndPolicyUrl();
        validateTrustStorePath();
    }
    
    private void validateCredentialsAreSpecified() throws VoltageConfigurationException {
        if (voltageAccountId.trim().isEmpty()) {
            LOG.error("Voltage credentials are not Provided..");
            throw new VoltageConfigurationException(VoltageConstants.ERR_VOLTAGE_CREDENTIALS_NOT_PROVIDED);
        }
    }
    
    private boolean validateRetryIntervalMillis(int low,int high,int value) {
        if (!intervalContains(low,high,value)) {
            LOG.error("Connection Retry Interval is out of range");
        }
        return intervalContains(low,high,value);
    }
    
    private boolean validateRetryCount(int low,int high,int value) {
        if (!intervalContains(low,high,value)) {
            LOG.error("Connection time out Retry Count is out of range");
        }
        return intervalContains(low,high,value);
    }
    
    private boolean intervalContains(int low, int high, int value) {
        return value >= low && value <= high;
    }
    
    private void validateTrustStorePath() throws VoltageConfigurationException {
        if (!(System.getProperty("os.name").startsWith("Win"))
                && trustStorePath.isEmpty()) {
            LOG.error("Trust Store path is not Provided..");
            throw new VoltageConfigurationException(VoltageConstants.ERR_TRUSTSTORE_PATH_NOT_PROVIDED);
        }
    }
    
    private void validateCachePathAndPolicyUrl() throws VoltageConfigurationException {
        if ( clientPolicyUrl.isEmpty()) {
            LOG.error("Policy URL is not Provided..");
            throw new VoltageConfigurationException(VoltageConstants.ERR_POLICY_URL_NOT_PROVIDED);
        }
    }    


}

package firstdata.voltage.util;

public interface VoltageConstants {

    String ERR_VOLTAGE_HELPER_INITIALIZATION = "Voltage Helper initialization failed";
    String ERR_FAILED_ENCRYPTION = "Failed to encrypt record with Voltage";    
    String ERR_BUILDING_VOLTAGE_OBJECTS = "Failed to build Voltage objects";
    String ERR_FAILED_INPUT_VALIDATION = "Input Validation Failed";    
    String ERR_EMPTY_PADDING_CHAR = "Padding Character not provided";
    String ERR_NULL = "Null is not a valid parameter";
    String ERR_FAILED_LOADING_FPE = "Failed to Load FPE Object";
    String ERR_EMPTY_ARRAY = "Input Array cannot be empty or null";
    String ERR_FAILED_DECRYPTION = "Failed to decrypt record with Voltage";
    String ERR_FAILED_BASE64_ENCODE = "Failed to Encode with Base64";
    String ERR_FAILED_BASE64_DECODE = "Failed to Decode with Base64";
    String ERR_FAILED_LOADING_VOLTAGE_FORMAT_FILE = "Failed to Load Voltage Format Configuration File";
    String ERR_INPUTSTREAM_CLOSE_FAILED = "Failed to Close InputStream";
    String ERR_FAILED_LOADING_FORMAT_CONFIGURATION_FACTORY = "Failed to Load Configuration Factory";
    String ERR_INTERFACE_IMPLEMENTATION_FAILURE = "The Custom class doesn't implement the FormatConfiguration interface";
    String ERR_FAILED_LOADING_USR_DEFINED_CLASS = "Failed to load user defined class";
    String ERR_POLICY_URL_NOT_PROVIDED = "Cache Path/Client policy URL not provided..";
    String ERR_TRUSTSTORE_PATH_NOT_PROVIDED = "TrustStore Path is not Provided";
    String ERR_VOLTAGE_CREDENTIALS_NOT_PROVIDED = "Voltage credentials are not Provided..";
    String ERR_VOLTAGE_HELPER_CONFIG_FAILED = "Failed to Load Voltage Helper Configuration Constructor";
    String ERR_MIN_LENGTH_CHECK_FAILED = "Size of String after Encoding is less than Required Min Length";
    String ERR_REGEX_VALIDATION_FAILED = "Input Regex Validation Failed";
    String ERR_MAX_LENGTH_CHECK_FAILED = "Input Max Length Validation Failed";
    String ISO_CODE_USA = "840";
} 
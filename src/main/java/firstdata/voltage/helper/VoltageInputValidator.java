package firstdata.voltage.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firstdata.voltage.configuration.VoltageFormat;
import com.firstdata.voltage.configuration.VoltageHelperConfiguration;
import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.exceptions.VoltageException;
import com.firstdata.voltage.util.VoltageConstants;

public class VoltageInputValidator {

    private static final Logger LOG = LoggerFactory.getLogger(VoltageHelper.class);
    
    /**
     * Input validation
     *
     */
    public static void validate(String inputText, String dataFormat, VoltageFormat format)
            throws VoltageException, VoltageConfigurationException {
        try {
            if (inputText == null) {
                throw new VoltageException(VoltageConstants.ERR_FAILED_INPUT_VALIDATION);
            }

            if (VoltageHelperConfiguration.getInstance().requiresInputValidation()) {
                atleastMinLength(inputText, format);
                inputSpecMinLengthCheck(inputText, format);
                matchesInputCharSpec(inputText, format);
                withinMaxLength(inputText, format);
            }
        } catch (VoltageException e) {
            LOG.error("Input Validation Failed");
            throw new VoltageException(VoltageConstants.ERR_FAILED_INPUT_VALIDATION, e);
        }
    }

    private static void atleastMinLength(String inputText, VoltageFormat format) throws VoltageException {
        if (!format.requiresPadding() && ((4 * (inputText.length()) / 3) < format.getMinLength())) {
            LOG.error("Size of String after Encoding is less than Required Min Length");
            throw new VoltageException(VoltageConstants.ERR_MIN_LENGTH_CHECK_FAILED);
        }
    }

    private static void inputSpecMinLengthCheck(String inputText, VoltageFormat format) throws VoltageException {
        String lengthInputSpec = format.getLengthInputSpec();
        int minLength = format.getMinLength();
        if (lengthInputSpec.length() > 0) {
            int count = 0;
            for (char inputChar : inputText.toCharArray()) {
                count = regexCharMatch(inputChar, lengthInputSpec, minLength, count);
            }
            if (count < format.getMinLength()) {
                LOG.debug("Input validation failed for Min Length");
                throw new VoltageException("Input validation failed for Min Length");
            }
        }
    }

    private static int regexCharMatch(char inputChar, String lengthInputSpec, int minLength, int count) {
        if (Character.toString(inputChar).matches(lengthInputSpec) && count <= minLength) {
            count++;
        }
        return count;
    }

    private static void matchesInputCharSpec(String inputText, VoltageFormat format) throws VoltageException {
        if ((format.getInputSpec() != null && !format.getInputSpec().trim().isEmpty())
                && (!(inputText.matches(format.getInputSpec()) || format.getInputSpec().equals("default")))) {
            LOG.error("Input Regex Validation Failed");
            throw new VoltageException(VoltageConstants.ERR_REGEX_VALIDATION_FAILED);
        }
    }

    private static void withinMaxLength(String inputText, VoltageFormat format) throws VoltageException {
        
        int inputLen = inputText.length();
        String lengthInputSpec = format.getLengthInputSpec();
        if (lengthInputSpec.length() > 0) {
            inputLen = getLength(inputText, lengthInputSpec);
            
        }

        if (format.requiresBase64Encoding()) {
            inputLen = (4 * (inputText.length()) / 3);
        }
        if (inputLen > format.getMaxLength()) {
            LOG.error("Input Max Length Validation Failed");
            throw new VoltageException(VoltageConstants.ERR_MAX_LENGTH_CHECK_FAILED);
        }
    }

    private static int getLength(String inputText, String lengthInputSpec) {
        int count = 0;
        for (char inputChar : inputText.toCharArray()) {
            if (Character.toString(inputChar).matches(lengthInputSpec)) {
                count++;
            }
        }
        return count;
    }
}

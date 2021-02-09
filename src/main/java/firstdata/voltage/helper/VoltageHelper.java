package firstdata.voltage.helper;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dcalonline.security.DCalContext;
import com.dcalonline.security.ResourceBundleProcessor;
import com.dcalonline.util.DCalConstants;
import com.fdc.security.util.AppnSecurityAPI;
import com.firstdata.voltage.configuration.FormatConfiguration;
import com.firstdata.voltage.configuration.VoltageFormat;
import com.firstdata.voltage.configuration.VoltageFormatConfigurationFactory;
import com.firstdata.voltage.configuration.VoltageHelperConfiguration;
import com.firstdata.voltage.exceptions.VoltageConfigurationException;
import com.firstdata.voltage.exceptions.VoltageException;
import com.firstdata.voltage.util.VoltageConstants;
import com.voltage.securedata.enterprise.AES;
import com.voltage.securedata.enterprise.FPE;
import com.voltage.securedata.enterprise.LibraryContext;
import com.voltage.securedata.enterprise.VeException;



public class VoltageHelper {
	private static Logger logger = Logger.getLogger(VoltageHelper.class);
	 private FormatConfiguration formatConfig = null;
	 private static VoltageHelper voltageInstance = null;
	 private VoltageHelperConfiguration config = null;
	 private String voltageUsername;
	 private String password;
	 private LibraryContext libraryCtxt;
	 private AES aes;
	 private Map<String, FPE> synchronizedFpeMap = Collections.synchronizedMap(new HashMap<String, FPE>());
	 public static final int MASKING_TYPE_LAST_FOUR  = 1;
	 public static final int MASKING_TYPE_LAST_EIGHT = 2;

	/**
     * Provides a Singleton object of VoltageHelper. Use this object for
     * accessing the Voltage API methods(encrypt/decrypt).
     *
     * @return instance of VoltageHelper
     * @throws VoltageException
     * @throws VoltageConfigurationException
     */

    public static VoltageHelper getInstance(String voltagePassword) throws VoltageException {
        if (voltageInstance == null) {
        	synchronized (VoltageHelper.class) {
                try {
                    voltageInstance = new VoltageHelper(voltagePassword);
                } catch (VoltageException e) {
                	logger.error("Voltage Helper Initialization Failed ", e);
                    throw new VoltageException(VoltageConstants.ERR_VOLTAGE_HELPER_INITIALIZATION, e);
                }
            }
        }
        return voltageInstance;
    }

    public static VoltageHelper getInstance() throws VoltageException {

        if (voltageInstance == null) {
           	logger.error("VoltageHelper.getInstance() :: Voltage Helper cannot be Initialized without a database connection.");
            throw new VoltageException(VoltageConstants.ERR_VOLTAGE_HELPER_INITIALIZATION);
        }
        return voltageInstance;
    }
    
    private VoltageHelper(String voltagePassword) throws VoltageException, VoltageConfigurationException {
        try {
            initialize(voltagePassword);
            buildLibraryContext();
        } catch (VoltageException e) {
        	logger.error("Failed to build Voltage objects:- ", e);
            throw new VoltageException(VoltageConstants.ERR_BUILDING_VOLTAGE_OBJECTS, e);
        }
    }
	
	private void initialize(String voltagePassword) throws VoltageException, VoltageConfigurationException {
		try {
	        config = VoltageHelperConfiguration.getInstance();
	        formatConfig = VoltageFormatConfigurationFactory.create();
	        voltageUsername = config.getVoltageAccountId();
	        password = voltagePassword;
			} catch (Exception e) {
	        	logger.error("VoltageHelper.initialize() Exception closing database Connecxtion.", e);
	            throw new VoltageException(VoltageConstants.ERR_BUILDING_VOLTAGE_OBJECTS, e);
	        }
    }
	
/*	**
    * Builds the LibraryContext
    *
    * @throws VeException
    * @throws VoltageConfigurationException
    * @throws UnsupportedEncodingException
    * @throws VoltageException
    */

   private void buildLibraryContext() throws VoltageException {

	   logger.debug("Building Library Context");

       System.loadLibrary("vibesimplejava");
       VoltageCallbackCommand command = VoltageCallbackCommandFactory
               .create(VoltageCallbackCommandFactory.BUILD_LIBRARY_CONTEXT);
       libraryCtxt = (LibraryContext) VoltageCommandExecutor.execute(command,
               new Object[]{config.getClientPolicyUrl(), config.getCachePath(), config.getTrustStorePath()});

       logger.debug("Library Context successfully built...");
   }
   
   /**
    * Builds the FPE
    *
    * @throws VeException
    * @throws UnsupportedEncodingException
    * @throws VoltageException
    * @throws CryptoException
    * @throws VoltageConfigurationException
    */
   private FPE buildFpe(String dataFormat) throws VeException, VoltageException, VoltageConfigurationException {

       FPE fpe = null;
       if (synchronizedFpeMap.containsKey(dataFormat)) {
           fpe = synchronizedFpeMap.get(dataFormat);
       } else {
           fpe = buildNewFpe(dataFormat);
       }
       return fpe;
   }

   private FPE buildNewFpe(String dataFormat) throws VeException, VoltageException, VoltageConfigurationException {
       String format = formatConfig.getFormat(dataFormat).getFormat();
       String identity = formatConfig.getFormat(dataFormat).getIdentity();
       nullValidate(format, identity, "Voltage Format\\Identity is Null");
       VoltageCallbackCommand command = VoltageCallbackCommandFactory.create(VoltageCallbackCommandFactory.BUILD_FPE);
       FPE fpe = (FPE) VoltageCommandExecutor.execute(command, new Object[]{libraryCtxt, dataFormat,
               formatConfig.getFormat(dataFormat).getIdentity(), voltageUsername, password});
       logger.debug("Built FPE and adding The FPE Object for format:" + AppnSecurityAPI.encoder().canonicalize(dataFormat));
       
       synchronizedFpeMap.put(dataFormat, fpe);
       return fpe;
   }
   
   /**
    * Builds the AES
    *
    * @throws VeException
    * @throws CryptoException
    * @throws VoltageException
    */

   private void buildAes() throws VeException, VoltageException  {
       VoltageCallbackCommand command = VoltageCallbackCommandFactory.create(VoltageCallbackCommandFactory.BUILD_AES);
       aes = (AES) VoltageCommandExecutor.execute(command, new Object[]{libraryCtxt,
               formatConfig.getFormat("AlphaNum-eFPE").getIdentity(), voltageUsername, password});
	   
   }
   
   /**
   * Encrypts the inputBytes and returns the encrypted bytes as byte[].
   *
   * @param inputBytes bytes to be encrypted
   * @return Encrypted bytes. Returns Null if the passed parameter is Null.
   * @throws VoltageException
   */
  public byte[] encrypt(byte[] inputBytes) throws VoltageException {
      byte[] encryptedBytes = null;
      if (null == inputBytes) {
    	  logger.error("Null is not a valid parameter");
          throw new VoltageException(VoltageConstants.ERR_NULL);
      }
      try {
          buildAes();
          encryptedBytes = aes.protect(inputBytes);
      } catch (VeException e) {
    	  logger.error("Encryption Failure", e);
          throw new VoltageException(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
      }
      return encryptedBytes;
  }
   
  /**
   * Decrypts the encryptedBytes and returns the decrypted bytes as byte[].
   *
   * @param encryptedBytes Bytes to be decrypted
   * @return Decrypted bytes. Returns Null if the passed parameter is Null.
   * @throws VoltageException
   */
  public byte[] decrypt(byte[] encryptedBytes) throws VoltageException {

      byte[] decryptedBytes = null;
      if (null == encryptedBytes) {
    	  logger.error("Null is not a valid parameter");
          throw new VoltageException(VoltageConstants.ERR_NULL);
      }
      try {
          buildAes();
          decryptedBytes = aes.access(encryptedBytes);
      } catch (VeException e) {
    	  logger.error("AES decryption failure:", e);
          throw new VoltageException(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
      }

      return decryptedBytes;
  }
   
   /**
    * Padding Right
    */
   private String rightPadToLength(String plainText, int length, String paddingChar) {
       return StringUtils.rightPad(plainText, length, paddingChar);
   }

   /**
    * Padding Left
    */
   private String leftPadToLength(String plainText, int length, String paddingChar) {
       return StringUtils.leftPad(plainText, length, paddingChar);
   }

   private String padToMinLength(String plainText, String dataFormat) throws VoltageException {

       String paddedText = null;
       int minLength = formatConfig.getFormat(dataFormat).getMinLength();
       if (formatConfig.getFormat(dataFormat).requiresPaddingLeft()) {
           if (null == formatConfig.getFormat(dataFormat).getPaddingChar()
                   || formatConfig.getFormat(dataFormat).getPaddingChar().isEmpty()) {
        	   logger.error("Padding Character not provided");
               throw new VoltageException(VoltageConstants.ERR_EMPTY_PADDING_CHAR);
           }
           paddedText = leftPadToLength(plainText, minLength, formatConfig.getFormat(dataFormat).getPaddingChar());
       } else {
           if (null == formatConfig.getFormat(dataFormat).getPaddingChar()
                   || formatConfig.getFormat(dataFormat).getPaddingChar().isEmpty()) {
        	   logger.error("Padding Character not provided");
               throw new VoltageException(VoltageConstants.ERR_EMPTY_PADDING_CHAR);
           }
           paddedText = rightPadToLength(plainText, minLength, formatConfig.getFormat(dataFormat).getPaddingChar());
       }
       return paddedText;
   }

   private String stripPadCharacters(String plainText, String dataFormat) throws VoltageException {
       String strippedText = null;
       if (formatConfig.getFormat(dataFormat).requiresPaddingLeft()) {
           if (null == formatConfig.getFormat(dataFormat).getPaddingChar()
                   || formatConfig.getFormat(dataFormat).getPaddingChar().isEmpty()) {
        	   logger.error("Padding Character not provided");
               throw new VoltageException(VoltageConstants.ERR_EMPTY_PADDING_CHAR);
           }
           strippedText = stripStartPadCharacters(plainText, formatConfig.getFormat(dataFormat).getPaddingChar());
       } else {
           if (null == formatConfig.getFormat(dataFormat).getPaddingChar()
                   || formatConfig.getFormat(dataFormat).getPaddingChar().isEmpty()) {
        	   logger.error("Padding Character not provided");
               throw new VoltageException(VoltageConstants.ERR_EMPTY_PADDING_CHAR);
           }
           strippedText = stripEndPadCharacters(plainText, formatConfig.getFormat(dataFormat).getPaddingChar());
       }

       return strippedText;
   }
   
   /**
    * Strip right padding
    */

   private String stripEndPadCharacters(String plainText, String paddingChar) {

       return StringUtils.stripEnd(plainText, paddingChar);
   }

   /**
    * Strip left padding
    */
   private String stripStartPadCharacters(String plainText, String paddingChar) {

       return StringUtils.stripStart(plainText, paddingChar);
   }
	
    /**
     * Encrypt Single value
     *
     */
   public String encrypt(String plainText, String dataFormat) throws Exception {
       String returnValue = "";
       try {
			if ((null != plainText) && (!("".equals(plainText)))) {
				// check for valid card num and ssn before encrypting.
				if ((DCalConstants.CARD_eFPE.equals(dataFormat) && !isCardNumber(plainText, false))
						|| (DCalConstants.SSN_eFPE.equals(dataFormat) && !StringUtils.isNumeric(plainText))) {
					return plainText;
				} else if (DCalConstants.ALFANUM_eFPE.equals(dataFormat)
						|| (DCalConstants.SSN_eFPE.equals(dataFormat) && plainText.length() != 9)) {
					// partial encryption for accountNumber branch accountNumber and SSN (for length not equals 9).
					dataFormat = DCalConstants.ALFANUM_eFPE;
					if (plainText.length() < 4) {
						plainText = leftPadToLength(plainText, 4, formatConfig
								.getFormat(dataFormat).getPaddingChar());
					}
					VoltageFormat format = formatConfig.getFormat(dataFormat);
					VoltageInputValidator.validate(plainText, dataFormat, format);
					FPE fpe = buildFpe(dataFormat);
					returnValue = protect(plainText, dataFormat, fpe);
					returnValue += plainText.substring(plainText.length() - 4);
				} else {
					VoltageFormat format = formatConfig.getFormat(dataFormat);
					VoltageInputValidator.validate(plainText, dataFormat, format);
					FPE fpe = buildFpe(dataFormat);
					returnValue = protect(plainText, dataFormat, fpe);
				}
			}
       } catch (VeException e) {
       	logger.error("Voltage Encryption Failed:", e);
           throw new Exception(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
       }
       catch (VoltageException e) {
       	logger.error("Voltage Encryption Failed:", e);
           throw new Exception(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
       }
       catch (UnsupportedEncodingException e) {
       	logger.error("Voltage Encryption Failed:", e);
           throw new Exception(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
       }
       return returnValue;
   }

    /**
     * Protect Operation
     *
     */
    private String protect(String inputText, String dataFormat, FPE fpe)
            throws VoltageException, UnsupportedEncodingException, VeException, VoltageConfigurationException {

        String encodedString = processDataForEncrypt(dataFormat, inputText);
        VoltageCallbackCommand command = VoltageCallbackCommandFactory.create(VoltageCallbackCommandFactory.FPE_PROTECT_ACCESS);
        String cipherText = (String) VoltageCommandExecutor.execute(command,
                new Object[]{fpe, encodedString, "protect"});

        return cipherText;
    }

    private String processDataForEncrypt(String dataFormat, String inputText)
            throws VoltageException, UnsupportedEncodingException {
        String processedText = inputText;
        if (formatConfig.getFormat(dataFormat).requiresBase64Encoding()) {
            processedText = base64Encode(processedText.getBytes("UTF-8"));
        }

        if (formatConfig.getFormat(dataFormat).requiresPadding()) {
            processedText = padToMinLength(processedText, dataFormat);
        }
        return processedText;
    }
    
    /**
     * Base64 Encode
     *
     */
    public String base64Encode(byte[] plainTextBytes) throws VoltageException {
        String encodedString = null;
        boolean wrapLines = false;
        if (null == plainTextBytes) {
        	logger.error("Null is not a valid parameter");
            throw new VoltageException(VoltageConstants.ERR_NULL);
        }
        try {
            encodedString = libraryCtxt.base64Encode(plainTextBytes, wrapLines);
            logger.error("Base64 Encode completed..");
        } catch (VeException e) {
        	logger.error("Base64 encode Failure:", e);
            throw new VoltageException(VoltageConstants.ERR_FAILED_BASE64_ENCODE, e);
        }
        return encodedString;
    }

    
    private void nullValidate(String voltageParameters, String voltageParameters1, String logMsg)
            throws VoltageException {
        if (voltageParameters == null || voltageParameters1 == null) {
        	logger.error(logMsg);
            throw new VoltageException(logMsg);
        }
    }
    
    public boolean isCardNumber(String cardNo, boolean isEncrypted) {
    	if (cardNo.length() == 16) {
    		if (isEncrypted)
        		return !StringUtils.isNumeric(cardNo) && StringUtils.isNumeric(cardNo.substring(0, 6)) && StringUtils.isNumeric(cardNo.substring(12, 16));
        	else
        		return StringUtils.isNumeric(cardNo);
    	}
    	return false;
    }
    
    
    /**
     * Decrypt single value
     *
     */
	public String transform(String encryptedText, String dataFormat) throws VoltageException {
        String plainText = null;
        FPE fpe = null;
        try {
			if ((null == encryptedText) || ("".equals(encryptedText.trim())))
				plainText = "";
			else if (encryptedText.subSequence(0, 1).equals("*"))
				return encryptedText;
			// check for card no and ssn whether it is encrypted
			else if ((DCalConstants.CARD_eFPE.equals(dataFormat) && !isCardNumber(
					encryptedText, true))
					|| (DCalConstants.SSN_eFPE.equals(dataFormat) && StringUtils
							.isNumeric(encryptedText)))
				return encryptedText;
			else if (DCalConstants.ALFANUM_eFPE.equals(dataFormat)
					|| (DCalConstants.SSN_eFPE.equals(dataFormat) && encryptedText
							.length() != 9)) {
				if (encryptedText.length() < 19) {
					return encryptedText;
				}
				dataFormat = DCalConstants.ALFANUM_eFPE;
				encryptedText = StringEscapeUtils.unescapeHtml(encryptedText);
				fpe = buildFpe(dataFormat);
				plainText = access(
						encryptedText.substring(0, encryptedText.length() - 4),
						dataFormat, fpe);
			} else {
				fpe = buildFpe(dataFormat);
				plainText = access(encryptedText, dataFormat, fpe);
			}
        } catch (VeException e) {
        	logger.error("Voltage Encryption Failed:", e);
            throw new VoltageException(e.getMessage());
        }
        catch (VoltageException e) {
			logger.error("Voltage Encryption Failed:", e);
			throw new VoltageException(VoltageConstants.ERR_FAILED_ENCRYPTION,
					e);
        } catch (Exception e) {
        	logger.error("Error while encrypting string:", e);
            throw new VoltageException(e.getMessage());
        }
        return plainText;
    }
    
    /**
     * Access operation
     *
     */
    private String access(String inputText, String dataFormat, FPE fpe)
            throws VeException, VoltageException, VoltageConfigurationException {

        VoltageCallbackCommand command = VoltageCallbackCommandFactory.create(VoltageCallbackCommandFactory.FPE_PROTECT_ACCESS);
        String plainText = (String) VoltageCommandExecutor.execute(command, new Object[]{fpe, inputText, "access"});

        plainText = processData(dataFormat, plainText);
        return plainText;
    }
    private String processData(String dataFormat, String plainText) throws VoltageException {
        byte[] decodedByte;
        if (formatConfig.getFormat(dataFormat).requiresPadding()) {
            plainText = stripPadCharacters(plainText, dataFormat);
        }

        if (formatConfig.getFormat(dataFormat).requiresBase64Encoding()) {
            decodedByte = base64Decode(plainText);
            plainText = new String(decodedByte);
        }
        return plainText;
    }
    
    /**
     * Base64 Decode
     *
     */
    public byte[] base64Decode(String encodedString) throws VoltageException {

        byte[] decodedByte;
        boolean ignoreInvalidChars = true;
        if (null == encodedString) {
        	logger.error("Null is not a valid parameter");
            throw new VoltageException(VoltageConstants.ERR_NULL);
        }
        try {
            decodedByte = libraryCtxt.base64Decode(encodedString, ignoreInvalidChars);
        } catch (VeException e) {
        	logger.error("Base64 decode Failure:", e);
            throw new VoltageException(VoltageConstants.ERR_FAILED_BASE64_DECODE, e);
        }
        return decodedByte;
    }

    //
    // This is called from Template only.
    //
    public String decryptAndMask(String encryptedText, String dataFormat, int templateFormat, DCalContext dCalContext) throws VoltageException {
        String maskedString = "";
        try {
            if (2 == templateFormat)
                maskedString = decryptAndMask(encryptedText, dataFormat, false, dCalContext);
            else 
                maskedString = decryptAndMask(encryptedText, dataFormat, true, dCalContext);
        } catch (Exception e) {
            logger.error("Voltage Encryption Failed:", e);
        }
        return maskedString;
    }

    //
    // The normal method called from 360 code base.
    //
    public String decryptAndMask(String encryptedText, String dataFormat, DCalContext dCalContext) throws VoltageException {
    	return decryptAndMask(encryptedText, dataFormat, false, dCalContext);
    }
    
    //
    // Private because in general we do not want to give the option to override masking.
    //
    private String decryptAndMask(String encryptedText, String dataFormat, boolean overrideMasking, DCalContext dCalContext) throws VoltageException {

        String maskedText = "";
        String plainText = "";
        try {
             /* Get plain text string from Voltage
        	TODO: Modify this code to do decryption based on business justification if it causes performance issue.*/
            plainText = transform(encryptedText, dataFormat);
        	
            // Mask as per company requirements.
            maskedText = getMaskedString(plainText, dataFormat, overrideMasking, dCalContext);
        	
        } catch (Exception e) {
            logger.error("Voltage Encryption Failed:", e);
            throw new VoltageException(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
        }
        return maskedText;
    }

	private String getMaskedString(String plainText, String dataFormat, boolean overrideMasking, DCalContext dCalContext) throws VoltageException{
		
	    String maskedString = "";
		int maskingPreference = -1;
		String isoCode = "";
		
	    try {
	    	if (null == dCalContext) {
	    		maskingPreference = MASKING_TYPE_LAST_FOUR;
	    		isoCode = VoltageConstants.ISO_CODE_USA;
	    	} else {
	        	maskingPreference = dCalContext.getMaskingPreference();
	        	isoCode = dCalContext.getCompanyISO();
	    	}
	    	
			if (overrideMasking) {
				maskedString = plainText;
			} else if (((null != plainText) && (!("".equals(plainText))))) {
				if (plainText.subSequence(0, 1).equals("*"))
					maskedString = plainText;
				else if (DCalConstants.ALFANUM_eFPE.equals(dataFormat)) {
					maskedString = DCalConstants.MaskingEightStars
							+ plainText.substring(plainText.length() - 4);
				} else if (DCalConstants.CARD_eFPE.equals(dataFormat) && plainText.length() == 16) {
					if (isCashCard(plainText)) {
						maskedString = getCashCardName(plainText,
								isoCode);
					} else if (maskingPreference == MASKING_TYPE_LAST_EIGHT)
						maskedString = DCalConstants.MaskingEightStars
								+ plainText.substring(plainText.length() - 8);
					else
						maskedString = DCalConstants.MaskingTwelveStars
								+ plainText.substring(plainText.length() - 4);
				} else if (DCalConstants.SSN_eFPE.equals(dataFormat)) {
					maskedString = DCalConstants.MaskingFiveStars
							+ plainText.substring(plainText.length() - 4);
				} else if (DCalConstants.NUMERIC_eFPE.equals(dataFormat)) {
					maskedString = DCalConstants.MaskingEightStars
							+ plainText.substring(plainText.length() - 4);
				} else {
					return plainText;
				}
			}
	    } catch (Exception e) {
	        logger.error("VoltageUtil.getMaskedString :: Exception masking String " + e.getMessage());
	        throw new VoltageException(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
	    }
	    return  maskedString;
	}
	
	private boolean isCashCard(String inCardNumber)  throws Exception {
		boolean isCashCard = false;
		try {
			if (!((null == inCardNumber) || ("".equals(inCardNumber)))) {
				if (Character.isLetter(inCardNumber.charAt(inCardNumber.length()-1))) {
					isCashCard = true;
				}
			}
		} catch (Exception e) {
			logger.error("VoltageUtil.isCashCard :: Exception checking if Card is a Cash Card :: " + e.getMessage());
			throw e;
		}
		return isCashCard;
	}

	private boolean isEncryptedCashCard(String inCardNumber, String dataFormat)  throws Exception {
		boolean isCashCard = false;
		try {
			if (DCalConstants.CARD_eFPE.equals(dataFormat)) {
				if (!((null == inCardNumber) || ("".equals(inCardNumber)))) {
					if (inCardNumber.length() != 16) {
						isCashCard = true;
					}
				}
			}
		} catch (Exception e) {
			logger.error("VoltageUtil.isCashCard :: Exception checking if Card is a Cash Card :: " + e.getMessage());
			throw e;
		}
		return isCashCard;
	}

	private String getCashCardName(String inCardNumber, String isoCode)  throws Exception {
		String returnCardNumber = "";
		String cashTag = "";
		try {
			cashTag = ResourceBundleProcessor.getString(isoCode, "CASH") + " ";

			if (!((null == inCardNumber) || ("".equals(inCardNumber)))) {
				returnCardNumber = cashTag + inCardNumber.substring(inCardNumber.length() - 3);
			}
		} catch (Exception e) {
			logger.error("VoltageUtil.getCashCardName :: Exceptioncreating the Cash Card name " + e.getMessage());
			throw e;
		}
		return returnCardNumber;
	}
	
	   public byte[] encryptByteArray(byte[] plainText, String dataFormat) throws Exception {
	       String encodedString = "";
	       String cipherString = "";
	       byte[] byteStream = null;
	       try {
				FPE fpe = buildFpe(dataFormat);
				
	        	encodedString = base64Encode(plainText);

		        if (formatConfig.getFormat(dataFormat).requiresPadding()) {
		        	encodedString = padToMinLength(encodedString, dataFormat);
		        }
				
		        VoltageCallbackCommand command = VoltageCallbackCommandFactory.create(VoltageCallbackCommandFactory.FPE_PROTECT_ACCESS);
		        cipherString = (String) VoltageCommandExecutor.execute(command, new Object[]{fpe, encodedString, "protect"});
		        
		        byteStream = cipherString.getBytes("UTF-8");

	       } catch (VeException e) {
	       		logger.error("VoltageUtil.encryptByteArray :: Voltage Encryption Failed:", e);
	           throw new Exception(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
	       }
	       catch (VoltageException e) {
	       		logger.error("VoltageUtil.encryptByteArray :: Voltage Encryption Failed:", e);
	           throw new Exception(VoltageConstants.ERR_FAILED_ENCRYPTION, e);
	       }
	       return byteStream;
	   }

	   public byte[] decryptBytes(byte[] encryptedText, String dataFormat) throws VoltageException {
			String encryptedString= null;
	        String plainText = null;
	        FPE fpe = null;
	        byte[] decodedByte = null;
	        try {
					dataFormat = DCalConstants.ALFANUM_eFPE;
					fpe = buildFpe(dataFormat);
					
					encryptedString = new String(encryptedText);
					
					plainText = access(encryptedString, dataFormat, fpe);
		            decodedByte = base64Decode(plainText);

	        } catch (VeException e) {
	        	logger.error("VoltageUtil.decryptBytes :: Voltage Encryption Failed, so returning the encrypted String:", e);
	        	decodedByte = encryptedText;
	        }
	        catch (VoltageException e) {
	        	logger.error("VoltageUtil.decryptBytes :: Voltage Encryption Failed, so returning the encrypted String:", e);
	        	decodedByte = encryptedText;
	        } catch (Exception e) {
	        	logger.error("VoltageUtil.decryptBytes :: Error while encrypting string:", e);
	            throw new VoltageException(e.getMessage());
	        }
	        return decodedByte;
	    }
}
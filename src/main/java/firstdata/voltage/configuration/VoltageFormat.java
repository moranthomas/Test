package firstdata.voltage.configuration;


public class VoltageFormat {

    private String format;
    private String identity;
    private int minLength;
    private int maxLength;
    private String lengthInputSpec;
    private String inputSpec;
    private boolean requiresBase64Encoding = false;
    private boolean requiresPadding = false;
    private boolean paddingLeft = false;
    private String paddingChar;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        if (identity != null) {
            this.identity = identity;
        }        
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        int minimumLength = 0;
        if (minLength != null) {
            minimumLength = Integer.parseInt(minLength);            
        } else {
            minimumLength = 0; 
        }
        this.minLength = minimumLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        int maximumLength = 0;
        if (maxLength != null) {
            maximumLength = Integer.parseInt(maxLength);
        } 
        this.maxLength = maximumLength;
    }
    
    public void setInputSpec(String inputSpec) {
        if (inputSpec != null) {            
            this.inputSpec = inputSpec;
        } else {
            this.inputSpec = "default";
        }        
    }

    public String getInputSpec() {
        return inputSpec;
    }

    public void setbase64Encoding(String requiresBase64Encoding) {
        if (requiresBase64Encoding != null && requiresBase64Encoding.equalsIgnoreCase("yes")) {
            this.requiresBase64Encoding = true;
        }
    }

    public boolean requiresBase64Encoding() {
        return requiresBase64Encoding;
    }

    public void setPaddingLeft(String paddingLeft) {
        if (paddingLeft != null && paddingLeft.equalsIgnoreCase("yes")) {
            this.paddingLeft = true;
        }
    }

    public boolean requiresPaddingLeft() {
        return paddingLeft;
    }    

    public void setPadding(String requiresPadding) {
        
        if (requiresPadding != null && requiresPadding.equalsIgnoreCase("yes")) {
            this.requiresPadding = true;
        }
    }

    public boolean requiresPadding() {
        return requiresPadding;
    }    

    public void setPaddingChar(String paddingChar) {
        if (paddingChar != null) {
        this.paddingChar = stripQuotes(paddingChar);
        }
    }

    public String getPaddingChar() {
        return paddingChar;
    }

    private String stripQuotes(String value) {

        String result = value;
        int firstQuote = value.indexOf('\"');
        int lastQuote = value.lastIndexOf('\"');
        int strLength = value.length();
        if (firstQuote == 0 && lastQuote == strLength - 1) {
            result = value.substring(1, strLength - 1);
        }
        return result;
    }

    public String getLengthInputSpec() {
        return lengthInputSpec;
    }

    public void setLengthInputSpec(String lengthInputSpec) {
        if (lengthInputSpec == null || lengthInputSpec.isEmpty()) {
        this.lengthInputSpec = "";
        } else {
            this.lengthInputSpec =     lengthInputSpec;
        }
    }
}

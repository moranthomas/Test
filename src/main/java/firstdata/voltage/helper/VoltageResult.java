package firstdata.voltage.helper;

import java.util.List;
import java.util.Map;

public class VoltageResult {
    
    private String encryptedText = null;
    private String[] successArray = null;
    private String[] failedArray = null;
    private List<Map<String,String>> successListMap = null;
    private List<Map<String,String>> failedListMap = null;
    private Map<String,String> successMap = null;
    private Map<String,String> failedMap = null;
    private int keyNumber;
    public static final int SUCCESS = 1;
    public static final int FAILED = 0;    
    
    public String[] getArrayResult(int resultType) {
        String[] result;
        if (resultType == SUCCESS) {
            result = successArray;
        } else {
            result = failedArray;            
        }
        return result;
    }
    
    protected void setArrayResult(String[] encryptedArray) {
        this.successArray = encryptedArray;
    }  
    
    protected void setFailedArrayResult(String[] failedArray) {
        this.failedArray = failedArray;
    }

    public String getResult() {
        return encryptedText;
    }

    protected void setResult(final String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public List<Map<String,String>> getListResult(int resultType) {
        List<Map<String,String>> result = null;
        if (resultType == SUCCESS) {
            result = successListMap;
        } else {
            result = failedListMap;
        }
        return result;
    }
    
    public boolean anyFailed() {
           if ((null != failedArray && failedArray.length > 0) || (null != failedListMap && failedListMap.size() > 0)) {
            return true;
        }
        return false;
    }
    
    protected void setListResult(final List<Map<String,String>> encryptedListMap) {
        this.successListMap = encryptedListMap;
    }
     
    protected void setFailedListResult(List<Map<String,String>> failedListMap) {
        this.failedListMap = failedListMap;
    }
    
    protected void setSuccessMap(Map<String,String> successMap) {
        this.successMap = successMap;
    }
    
    public Map<String,String> getSuccessMap() {
        return successMap;
    }
    
    protected void setFailedMap(Map<String,String> failedMap) {
        this.failedMap = failedMap;
    }
    
    public Map<String,String> getFailedMap() {
        return failedMap;
    }

    public int getKeyNumber() {
        return keyNumber;
    }

    protected void setKeyNumber(final int keyNumber) {
        this.keyNumber = keyNumber;
    }    
}

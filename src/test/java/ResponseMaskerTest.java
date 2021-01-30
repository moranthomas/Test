import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResponseMaskerTest {

    private static final String ACCOUNT_FIELDS_MASK_STRING = "{\"addressContinuationLine1Text\":\"{\\\"pattern\\\":\\\"\\\",\\\"replacement\\\":\\\"\\\",\\\"complianceType\\\":\\\"CardholderAddress\\\"}\"," +
            "\"primaryCustomerSecondPhoneIdentifier\":\"{\\\"pattern\\\":\\\"\\\",\\\"replacement\\\":\\\"\\\",\\\"complianceType\\\":\\\"PhoneNumber\\\"}\"," +
            "\"presentationInstrumentIdentifier\":\"{\\\"pattern\\\":\\\"(.{6})(.*)(.{4})$\\\",\\\"replacement\\\":\\\"$1******$3\\\",\\\"complianceType\\\":\\\"PresentationInstrumentIdentifier\\\"}\"," +
            "\"socialSecurityIdentifier\":\"{\\\"pattern\\\":\\\"(.*)(.{4})$\\\",\\\"replacement\\\":\\\"***-**-$2\\\",\\\"complianceType\\\":\\\"SocialSecurityNumber\\\"}\"," +
            "\"accountIdentifier\":\"{\\\"pattern\\\":\\\"(.{6})(.*)(.{4})$\\\",\\\"replacement\\\":\\\"$1******$3\\\",\\\"complianceType\\\":\\\"PrimaryAccountNumber\\\"}\"}";
    private static final String OVERRIDE_FIELDS_MASK_STRING = "{\"accountIdentifier\":\"{\\\"pattern\\\":\\\"(.{6})(.*)(.{4})$\\\",\\\"replacement\\\":\\\"$1******$3\\\",\\\"complianceType\\\":\\\"PrimaryAccountNumber\\\"}\"}";
    private static final String DEFAULT_CLIENT = "AAAA1114";

    public static void mockVoltage() {
      /*  FormatPreservingEncryption mock = Mockito.mock(FormatPreservingEncryption.class);
        EncryptorContext.voltageOverride("alphanum", mock);
        when(mock.encryptWithCurrentKey(anyString())).thenAnswer((Answer<String>) invocationOnMock -> (String) invocationOnMock.getArguments()[0]);
        when(mock.decrypt(anyString())).thenAnswer((Answer<String>) invocationOnMock -> invocationOnMock.getArguments()[0].toString());*/
    }

    @Before
    public void setUp() throws Exception {
        /*mockVoltage();
        SecureConfigRetriever secureConfigRetriever = mock(SecureConfigRetriever.class);
        Field configRetrieverField = Whitebox.getField(ResponseMaskerUtil.class, "secureConfigRetriever");
        setFinalStatic(configRetrieverField, secureConfigRetriever);

        Map<String, String> defaultFirstDataMasking = ImmutableMap.of("pattern", "(.{6})(.*)(.{4})$", "replacement", "$1******$3");
        Map<String, Map<String, String>> maskingListForFirstData = ImmutableMap.of("PrimaryAccountNumber", defaultFirstDataMasking, "PresentationInstrumentIdentifier", defaultFirstDataMasking);
        when(secureConfigRetriever.getMaskingFormatForClient(eq("AAAA1114"))).thenReturn(maskingListForFirstData);

        Map<String, String> defaultKohlsMasking = ImmutableMap.of("pattern", "(.*)(.{4})$", "replacement", "************$2");
        Map<String, Map<String, String>> maskingListForKohls = ImmutableMap.of("PrimaryAccountNumber", defaultKohlsMasking, "PresentationInstrumentIdentifier", defaultKohlsMasking);
        when(secureConfigRetriever.getMaskingFormatForClient(eq("AAAA4151"))).thenReturn(maskingListForKohls);

        Map<String, String> defaultClient2318Masking = ImmutableMap.of("pattern", "(.{6})(.*)(.{4})$", "replacement", "$1******$3");
        Map<String, Map<String, String>> maskingListForClient2318 = ImmutableMap.of("PrimaryAccountNumber", defaultClient2318Masking, "PresentationInstrumentIdentifier", defaultClient2318Masking);
        when(secureConfigRetriever.getMaskingFormatForClient(eq("AAAA2318"))).thenReturn(maskingListForClient2318);
        when(secureConfigRetriever.getSingleRecord(eq("AAAA2318"), eq("com.firstdata.fs.masking"), eq("applyMasking"), any())).thenReturn(Boolean.TRUE);*/
    }


    @Test
    public void testConvertMaskStringToMapOverrides() {
       /* Map<String, ResponseMask> maskMap = ResponseMasker.convertToMaskMap(OVERRIDE_FIELDS_MASK_STRING);
        assertEquals(1, maskMap.size());
        assertMaskMapEntry(maskMap.get("accountIdentifier"), "(.{6})(.*)(.{4})$", "$1******$3", ComplianceTypes.PrimaryAccountNumber);*/
    }


}
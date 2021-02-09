package firstdata.voltage.helper;

import java.util.HashMap;
import java.util.Map;

public class VoltageCallbackCommandFactory {

    public static final String BUILD_AES = "BuildAES";
    private static VoltageCallbackCommand buildAesCommand = new VoltageBuildAesCommand();
    public static final String BUILD_FPE = "BuildFPE";
    private static VoltageCallbackCommand buildFpeCommand = new VoltageBuildFpeCommand();
    public static final String BUILD_LIBRARY_CONTEXT = "BuildLibarayContext";
    private static VoltageCallbackCommand buildLibraryContextCommand = new VoltageBuildLibContextCommand();
    public static final String FPE_PROTECT_ACCESS = "fpeProtect";
    private static VoltageCallbackCommand protectAccessCommand = new VoltageFpeProtectAccessCommand();
    public static final String FPE_PROTECT_WITH_KEY_NUMBER = "fpeProtectWithKeyNumber";
    //private static VoltageCallbackCommand fpeProtectWithKeyNumberCommand = new VoltageFpeProtectWithKeyNumber();

    private static Map<String, VoltageCallbackCommand> commandMap = new HashMap<String, VoltageCallbackCommand>();
    
    static {
        commandMap.put(BUILD_AES, buildAesCommand);
        commandMap.put(BUILD_FPE, buildFpeCommand);
        commandMap.put(BUILD_LIBRARY_CONTEXT, buildLibraryContextCommand);
        commandMap.put(FPE_PROTECT_ACCESS, protectAccessCommand);
        //commandMap.put(FPE_PROTECT_WITH_KEY_NUMBER, fpeProtectWithKeyNumberCommand);

    }

    public static VoltageCallbackCommand create(String type) {
        return commandMap.get(type);
    }
}

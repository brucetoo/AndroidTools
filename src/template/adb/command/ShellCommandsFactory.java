package template.adb.command;

/**
 * Created by brucetoo on 24/03/2017.
 */
public class ShellCommandsFactory {
    public static String startActivity(String packageName, String activityName, boolean attachDebugger) {
        String debugFlag = attachDebugger ? "-D" : "";
        return "am start " + debugFlag + "-n " + packageName + "/" + activityName;
    }
}

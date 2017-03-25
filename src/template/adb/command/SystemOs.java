package template.adb.command;

import java.io.File;

/**
 * Created by brucetoo on 26/03/2017.
 */
public class SystemOs {

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
    }

    public static String platformExecutableSuffixExe() {
        return isWindows() ? ".exe" : "";
    }

    public static String platformExecutableSuffixBat() {
        return isWindows() ? ".bat" : "";
    }
}

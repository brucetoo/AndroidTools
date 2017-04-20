package template.adb;

import com.intellij.openapi.project.Project;
import org.apache.commons.lang.StringUtils;
import template.adb.command.SystemOs;
import template.adb.command.receiver.GenericReceiver;
import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import template.ui.NotificationHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class AdbUtil {

    public static boolean isAppInstalled(IDevice device, String packageName) throws TimeoutException, AdbCommandRejectedException, ShellCommandUnresponsiveException, IOException {
        GenericReceiver receiver = new GenericReceiver();
        // "pm list packages com.my.package" will return one line per package installed that corresponds to this package.
        // if this list is empty, we know for sure that the app is not installed
        device.executeShellCommand("pm list packages " + packageName, receiver, 15L, TimeUnit.SECONDS);

        //TODO make sure that it is the exact package name and not a subset.
        // e.g. if our app is called com.example but there is another app called com.example.another.app, it will match and return a false positive
        return !receiver.getAdbOutputLines().isEmpty();
    }

    // The android debugger class is not available in Intellij 2016.1.
    // Nobody should use that version but it's still the minimum "supported" version since android studio 2.2
    // shares the same base version.
    public static Boolean isDebuggingAvailable() {
        try {
            Reflect.on("com.android.tools.idea.run.editor.AndroidDebugger").get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static final String SDK_DIR = "sdk.dir";
    public static final String PROPERTIES = "/local.properties";

    public static String getAdbPath(Project project) {
        StringBuffer adbPath = new StringBuffer();
        File adb = new File(project.getBasePath() + PROPERTIES);
        try {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream(adb);
            properties.load(inputStream);
            String property = properties.getProperty(SDK_DIR);
            adbPath.append(property);
            adbPath.append(File.separator);
            adbPath.append("platform-tools");
            adbPath.append(File.separator);
            adbPath.append("adb");
            adbPath.append(SystemOs.platformExecutableSuffixExe());
            NotificationHelper.info("local.properties:" + adb.getAbsolutePath() + " adbPath:" + adbPath);
        } catch (FileNotFoundException e) {
            NotificationHelper.error("Local.properties not include in your project!");
            e.printStackTrace();
        } catch (IOException e) {
            NotificationHelper.error("Get adb path failed!");
            e.printStackTrace();
        }

        return adbPath.toString();
    }
}

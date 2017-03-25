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
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
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

    public static final String ANDROID_HOME = "ANDROID_HOME";

    public static String getAdbPath(Project project) {
        StringBuffer adbPath = new StringBuffer();
        adbPath.append(getAndroidHome(project));
        adbPath.append(File.separator);
        adbPath.append("platform-tools");
        adbPath.append(File.separator);
        adbPath.append("adb");
        adbPath.append(SystemOs.platformExecutableSuffixExe());
        return adbPath.toString();
    }

    public static String getAndroidHome(Project project) {
        String androidHome = System.getenv(ANDROID_HOME);
        Map<String, String> map = System.getenv();
        StringBuffer builder = new StringBuffer();
        builder.append("Following is system environment params: \n");
        for (Iterator<String> i = map.keySet().iterator(); i.hasNext(); ) {
            String key = i.next();
            builder.append("Key = " + key + " value = " + map.get(key));
            builder.append("\n");
        }
        NotificationHelper.info(builder.toString());
        if (StringUtils.isEmpty(androidHome)) {
            NotificationHelper.infoInProject("SDK Location Path Not Found In template.adb.command.SystemOs", project);
            NotificationHelper.error("SDK Location Path Not Found In template.adb.command.SystemOs /n Please check out if ANDROID_HOME is set in system environment \n " +
                    "http://stackoverflow.com/questions/135688/setting-environment-variables-in-os-x/3756686#3756686 \n" +
                    "http://stackoverflow.com/questions/135688/setting-environment-variables-in-os-x/588442#588442");
        }
        return androidHome;
    }
}

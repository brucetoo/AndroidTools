package template.adb.command;

import template.adb.command.receiver.GenericReceiver;
import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;

import java.util.concurrent.TimeUnit;

import static template.adb.AdbUtil.isAppInstalled;
import static template.ui.NotificationHelper.info;
import static template.ui.NotificationHelper.error;


public class KillCommand implements Command {
    @Override
    public boolean run(Project project,String activityName, IDevice device, String packageName) {
        try {
            if (isAppInstalled(device, packageName)) {
                device.executeShellCommand("am force-stop " + packageName, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                info(String.format("<b>%s</b> forced-stop on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (Exception e1) {
            error("Kill fail... " + e1.getMessage());
        }
        return false;
    }
}

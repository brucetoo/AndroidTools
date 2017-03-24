package template.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;

import java.util.concurrent.TimeUnit;

import static template.adb.AdbUtil.isAppInstalled;

import template.adb.command.receiver.GenericReceiver;

import static template.ui.NotificationHelper.info;
import static template.ui.NotificationHelper.error;


public class ClearDataCommand implements Command {

    @Override
    public boolean run(Project project,String activityName, IDevice device, String packageName) {
        try {
            if (isAppInstalled(device, packageName)) {
                device.executeShellCommand("pm clear " + packageName, new GenericReceiver(), 15L, TimeUnit.SECONDS);
                info(String.format("<b>%s</b> cleared data for app on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (Exception e1) {
            error("Clear data failed... " + e1.getMessage());
        }

        return false;
    }

}

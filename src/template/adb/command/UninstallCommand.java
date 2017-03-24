package template.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.intellij.openapi.project.Project;

import static template.ui.NotificationHelper.info;
import static template.ui.NotificationHelper.error;

public class UninstallCommand implements Command {
    @Override
    public boolean run(Project project,String activityName, IDevice device, String packageName) {
        try {
            String errorCode = device.uninstallPackage(packageName);
            if (errorCode == null) {
                info(String.format("<b>%s</b> uninstalled on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> is not installed on %s", packageName, device.getName()));
            }
        } catch (InstallException e1) {
            error("Uninstall fail... " + e1.getMessage());
        }
        return false;
    }
}

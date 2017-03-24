package template.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;

public interface Command {
    /**
     *
     * @return true if the command executed properly
     */
    boolean run(Project project,String activityName, IDevice device, String packageName);
}

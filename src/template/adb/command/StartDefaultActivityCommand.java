package template.adb.command;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.MultiLineReceiver;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static template.ui.NotificationHelper.info;
import static template.ui.NotificationHelper.error;

public class StartDefaultActivityCommand implements Command {

    private final boolean withDebugger;

    public StartDefaultActivityCommand(boolean withDebugger) {
        this.withDebugger = withDebugger;
    }

    @Override
    public boolean run(Project project,String activityName, IDevice device, String packageName) {
        try {
            StartActivityReceiver receiver = new StartActivityReceiver();
            String shellCommand = ShellCommandsFactory.startActivity(packageName, activityName, withDebugger);

            device.executeShellCommand(shellCommand, receiver, 15L, TimeUnit.SECONDS);

//            if (withDebugger) {
//                new Debugger(project, device, packageName).attach();
//            }

            if (receiver.isSuccess()) {
                info(String.format("<b>%s</b> started on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> could not bet started on %s. \n\n<b>ADB Output:</b> \n%s", packageName, device.getName(), receiver.getMessage()));
            }
        } catch (Exception e) {
            error("Start fail... " + e.getMessage());
        }

        return false;
    }

    public static class StartActivityReceiver extends MultiLineReceiver {

        public String message = "Nothing Received";

        public List<String> currentLines = new ArrayList<String>();

        @Override
        public void processNewLines(String[] strings) {
            for (String s : strings) {
                if (!Strings.isNullOrEmpty(s)) {
                    currentLines.add(s);
                }
            }
            computeMessage();
        }

        private void computeMessage() {
            message = Joiner.on("\n").join(currentLines);
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return currentLines.size() > 0 && currentLines.size() < 3;
        }
    }

}

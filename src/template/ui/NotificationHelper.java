package template.ui;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class NotificationHelper {

    private static final NotificationGroup INFO = NotificationGroup.logOnlyGroup("AndroidTools (Logging)");
    private static final NotificationGroup ERRORS = NotificationGroup.balloonGroup("AndroidTools (Errors)");
    private static final NotificationListener NOOP_LISTENER = (notification, event) -> {
    };

    public static void info(String message) {
        sendNotification(message, NotificationType.INFORMATION, INFO);
    }

    public static void error(String message) {
        sendNotification(message, NotificationType.ERROR, ERRORS);
    }

    private static void sendNotification(String message, NotificationType notificationType, NotificationGroup notificationGroup) {
        notificationGroup.createNotification("AndroidTools", escapeString(message), notificationType, NOOP_LISTENER).notify(null);
    }


    private static String escapeString(String string) {
        return string.replaceAll("\n", "\n<br />");
    }

    public static void infoInProject(String message, Project project) {
        Messages.showWarningDialog(project,message,"Something Wrong!");
//        Messages.showMessageDialog(project, message, "Something Wrong!", Messages.getInformationIcon());
    }
}

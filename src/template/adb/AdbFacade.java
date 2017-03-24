package template.adb;

import template.adb.command.*;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.openapi.project.Project;
import template.ui.NotificationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AdbFacade {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Tools-%d").build());

    public static void uninstall(Project project,AppBean bean) {
        executeOnDevice(project,bean, new UninstallCommand());
    }

    public static void kill(Project project,AppBean bean) {
        executeOnDevice(project,bean, new KillCommand());
    }

    public static void startDefaultActivity(Project project,AppBean bean) {
        executeOnDevice(project,bean, new StartDefaultActivityCommand(false));
    }

    public static void startDefaultActivityWithDebugger(Project project,AppBean bean) {
        executeOnDevice(project,bean, new StartDefaultActivityCommand(true));
    }

    public static void restartDefaultActivity(Project project,AppBean bean) {
        executeOnDevice(project,bean, new RestartPackageCommand());
    }

    public static void restartDefaultActivityWithDebugger(Project project,AppBean bean) {
        executeOnDevice(project,bean, new CommandList(new KillCommand(), new StartDefaultActivityCommand(true)));
    }

    public static void clearData(Project project,AppBean bean) {
        executeOnDevice(project,bean, new ClearDataCommand());
    }

    public static void clearDataAndRestart(Project project,AppBean bean) {
        executeOnDevice(project,bean, new ClearDataAndRestartCommand());
    }

    private static void executeOnDevice(Project project,final AppBean bean, final Command runnable) {
        IDevice device;
        AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(
                bean.adbPath, false);
        waitForDevice(bridge);
        IDevice devices[] = bridge.getDevices();
        device = devices[0];

        if (device != null) {
            // "com.pp.assistant.activity.PPMainActivity"  "com.pp.assistant"
                EXECUTOR.submit((Runnable) () -> runnable.run(project,bean.activityName, device, bean.packageName));
        } else {
            NotificationHelper.error("No Device found");
        }
    }

    private static void waitForDevice(AndroidDebugBridge bridge) {
        int count = 0;
        while (!bridge.hasInitialDeviceList()) {
            try {
                Thread.sleep(100);
                count++;
            } catch (InterruptedException ignored) {
            }
            if (count > 300) {
                System.err.print("Time out");
                break;
            }
        }
    }
}

package template.adb;

import com.android.tools.idea.run.AndroidDevice;
import template.OnConnectCallBack;
import template.ToolsDialog;
import template.adb.command.*;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.intellij.openapi.project.Project;
import template.ui.NotificationHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AdbFacade {

    public static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("AndroidTools-%d").build());

    public static void uninstall(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new UninstallCommand(), callBack);
    }

    public static void kill(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new KillCommand(), callBack);
    }

    public static void startDefaultActivity(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new StartDefaultActivityCommand(false), callBack);
    }

    public static void startDefaultActivityWithDebugger(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new StartDefaultActivityCommand(true), callBack);
    }

    public static void restartDefaultActivity(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new RestartPackageCommand(), callBack);
    }

    public static void restartDefaultActivityWithDebugger(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new CommandList(new KillCommand(), new StartDefaultActivityCommand(true)), callBack);
    }

    public static void clearData(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new ClearDataCommand(), callBack);
    }

    public static void clearDataAndRestart(Project project, AppBean bean, OnConnectCallBack callBack) {
        executeOnDevice(project, bean, new ClearDataAndRestartCommand(), callBack);
    }

    public static void fetchDevices(Project project,AppBean bean,OnConnectCallBack callBack){
        executeOnDevice(project, bean, null, callBack);
    }

    private static void executeOnDevice(Project project, final AppBean bean, final Command runnable, OnConnectCallBack callBack) {
        AndroidDebugBridge.initIfNeeded(false);
        AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(
                bean.adbPath, false);
        waitForDevice(bridge);
        IDevice devices[] = bridge.getDevices();
        if (devices.length == 0) {
            callBack.connectCallBack(true,devices,runnable);
        } else {
            callBack.connectCallBack(false,devices,runnable);
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

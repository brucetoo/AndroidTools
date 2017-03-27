package template;

import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import template.adb.AdbFacade;
import template.adb.AdbUtil;
import template.adb.AppBean;
import com.intellij.openapi.project.Project;
import template.adb.command.Command;
import template.ui.NotificationHelper;

import javax.swing.*;
import java.awt.event.*;


public class ToolsDialog extends JDialog implements OnConnectCallBack {
    private JPanel contentPane;
    private JList list;
    private JRadioButton wdjButton;
    private JRadioButton ppButton;
    private AppBean bean = new AppBean();
    private Project project;

    public ToolsDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(false);
        setUndecorated(false);

        bean.activityName = "com.pp.assistant.activity.PPMainActivity";
        bean.packageName = "com.pp.assistant";
        bean.adbPath = AdbUtil.getAdbPath(project);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String selectedItem = (String) list.getSelectedValue();
                int index = list.getSelectedIndex();
                executeCommand(index);
            }
        };

        wdjButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    ppButton.setSelected(false);
                    bean.activityName = "com.pp.assistant.activity.PPMainActivity";
                    bean.packageName = "com.wandoujia.phoenix2";
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    ppButton.setSelected(true);
                    bean.activityName = "com.pp.assistant.activity.PPMainActivity";
                    bean.packageName = "com.pp.assistant";
                }
            }
        });

        ppButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    wdjButton.setSelected(false);
                    bean.activityName = "com.pp.assistant.activity.PPMainActivity";
                    bean.packageName = "com.pp.assistant";
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    wdjButton.setSelected(true);
                    bean.activityName = "com.pp.assistant.activity.PPMainActivity";
                    bean.packageName = "com.wandoujia.phoenix2";
                }
            }
        });
        list.addMouseListener(mouseListener);
    }

    private void executeCommand(int index) {
//        Adb Uninstall App
//        Adb Kill App
//        Adb Start App
//        Adb Restart App
//        Adb Clear and Start App
        switch (index) {
            case 0:
                AdbFacade.uninstall(project, bean, this);
                break;
            case 1:
                AdbFacade.kill(project, bean, this);
                break;
            case 2:
                AdbFacade.startDefaultActivity(project, bean, this);
                break;
            case 3:
                AdbFacade.restartDefaultActivity(project, bean, this);
                break;
            case 4:
                AdbFacade.clearDataAndRestart(project, bean, this);
                break;
        }
    }


    @Override
    public void connectCallBack(boolean noDevice, IDevice devices[], Command runnable) {
        if (noDevice) {
            ToolsDialog.this.setVisible(false);
            NotificationHelper.error("No Devices Found!");
        } else {
            IDevice device = devices[0];

            //TODO add devices chooser!!!
            if (device != null) {
                // "com.pp.assistant.activity.PPMainActivity"  "com.pp.assistant"
                AdbFacade.EXECUTOR.submit((Runnable) () -> runnable.run(project, bean.activityName, device, bean.packageName));
            }

            StringBuffer deviceInfo = new StringBuffer();
            deviceInfo.append("Following is Devices: \n");
            for (IDevice dev : devices) {
                deviceInfo.append("Device Name = " + dev.getName() + "  SerialNumber = " + dev.getSerialNumber());
                deviceInfo.append("\n");
            }
            NotificationHelper.info(deviceInfo.toString());

            StringBuffer clientInfo = new StringBuffer();
            clientInfo.append("Following is Clients: \n");
            for (Client client : device.getClients()) {
                clientInfo.append("ClientDescription = " + client.getClientData().getClientDescription()
                        + "  DebuggerListenPort() = " + client.getDebuggerListenPort());
                clientInfo.append("\n");
            }
            NotificationHelper.info(clientInfo.toString());
        }
    }
}

package template;

import com.android.ddmlib.IDevice;
import template.adb.AdbFacade;
import template.adb.AdbUtil;
import template.adb.AppBean;
import com.intellij.openapi.project.Project;
import template.adb.command.Command;
import template.ui.NotificationHelper;

import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;


public class ToolsDialog extends JDialog implements OnConnectCallBack {
    private JPanel contentPane;
    private JList list;
    private JRadioButton wdjButton;
    private JRadioButton ppButton;
    private JComboBox comboBox;
    private DefaultComboBoxModel comboBoxModel;
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

        comboBoxModel = new DefaultComboBoxModel();
        comboBox.setEnabled(true);
        comboBox.setModel(comboBoxModel);

        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                comboBox.setSelectedItem(e.getItem());
                if (ItemEvent.SELECTED == e.getStateChange()) {
                    NotificationHelper.info("New selected device :" + e.getItem());
                }
            }
        });
        AdbFacade.fetchDevices(project, bean, this);

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
        setVisible(false);
    }

    private HashMap<IDevice, String> mDevices = new HashMap<>();

    @Override
    public void onFirstCall(IDevice[] devices) {
        for (IDevice device : devices) {
            if (!mDevices.containsKey(device)) {
                String name = device.getName();
                name = name.substring(0, name.lastIndexOf("-")).toUpperCase();
                name += " "+ device.getProperty(IDevice.PROP_BUILD_VERSION);
                comboBoxModel.addElement(name);
                mDevices.put(device, name);
            }
        }
    }

    @Override
    public void onRunCommand(Command runnable) {
        if (mDevices.size() == 0) {
            ToolsDialog.this.setVisible(false);
            NotificationHelper.error("No Devices Found!");
        } else {
            String value = (String) comboBox.getSelectedItem();
            IDevice[] keys = mDevices.keySet().toArray(new IDevice[0]);
            for (int i = 0; i < keys.length; i++) {
                IDevice key = keys[i];
                if (mDevices.get(key).equals(value)) {
                    // "com.pp.assistant.activity.PPMainActivity"  "com.pp.assistant"
                    AdbFacade.EXECUTOR.submit((Runnable) () -> runnable.run(project, bean.activityName, key, bean.packageName));
                }
            }
        }
    }

    @Override
    public void onDeviceConnected(IDevice device) {
        if (!mDevices.containsKey(device)) {
            String name = device.getName();
            int indexOf = name.lastIndexOf("-");
            if (indexOf != -1) {
                name = name.substring(0, indexOf).toUpperCase();
            }
            name += " " + device.getProperty(IDevice.PROP_BUILD_VERSION);
            mDevices.put(device, name);
            comboBoxModel.addElement(name);
        }
    }

    @Override
    public void onDeviceDisconnected(IDevice iDevice) {
        if (mDevices.size() != 0 && mDevices.containsKey(iDevice)) {
            String value = mDevices.remove(iDevice);
            comboBoxModel.removeElement(value);
        }
    }

    @Override
    public void onDeviceChanged(IDevice iDevice, int changeMask) {

        //update name if needed
        if (mDevices.size() != 0 && mDevices.containsKey(iDevice)) {
            String value = mDevices.get(iDevice);
            if (value.contains("null")) {
                comboBoxModel.removeElement(value);
                String name = iDevice.getName();
                name = name.substring(0, name.lastIndexOf("-")).toUpperCase();
                name += " " + iDevice.getProperty(IDevice.PROP_BUILD_VERSION);
                comboBoxModel.addElement(name);
                mDevices.replace(iDevice, name);
            }
        }
    }
}

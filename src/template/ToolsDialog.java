package template;

import template.adb.AdbFacade;
import template.adb.AppBean;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.*;


public class ToolsDialog extends JDialog {
    private JPanel contentPane;
    private JList list;
    private JRadioButton wdjButton;
    private JRadioButton ppButton;
    private JButton buttonOK;
    private AppBean bean = new AppBean();
    private Project project;

    public ToolsDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(false);
        setUndecorated(false);
        getRootPane().setDefaultButton(buttonOK);

        bean.activityName = "com.pp.assistant.activity.PPMainActivity";
        bean.packageName = "com.pp.assistant";
        bean.adbPath = "/Users/brucetoo/Library/Android/sdk/platform-tools/adb";

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
                AdbFacade.uninstall(project, bean);
                break;
            case 1:
                AdbFacade.kill(project, bean);
                break;
            case 2:
                AdbFacade.startDefaultActivity(project, bean);
                break;
            case 3:
                AdbFacade.restartDefaultActivity(project, bean);
                break;
            case 4:
                AdbFacade.clearDataAndRestart(project, bean);
                break;
        }
    }

}

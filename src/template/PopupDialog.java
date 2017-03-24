package template;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

/**
 * Created by brucetoo on 24/03/2017.
 * Add this PopUp to handle show/hide of Dialog
 */
public class PopupDialog extends Popup implements WindowFocusListener {

    private final ToolsDialog dialog;

    public PopupDialog(Project project) {
        super();
        dialog = new ToolsDialog(project);
        dialog.setModal(false);
        dialog.setUndecorated(true);
        dialog.pack();
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
    }

    @Override
    public void show() {
        dialog.addWindowFocusListener(this);
        dialog.setVisible(true);
    }

    @Override
    public void hide() {
        dialog.setVisible(false);
        dialog.removeWindowFocusListener(this);
    }

    public void windowGainedFocus(WindowEvent e) {
        // NO-OP
    }

    public void windowLostFocus(WindowEvent e) {
        hide();
    }

}

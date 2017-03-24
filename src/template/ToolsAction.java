package template;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

/**
 * Created by brucetoo on 24/03/2017.
 */
public class ToolsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        final Project project = e.getData(PlatformDataKeys.PROJECT);
        PopupDialog dialog = new PopupDialog(project);
        dialog.show();

    }
}
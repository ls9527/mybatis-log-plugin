package mybatis.mylog.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import mybatis.mylog.action.gui.SqlText;
import mybatis.mylog.util.ConfigUtil;
import mybatis.mylog.util.StringConst;


/**
 * 插件入口
 *
 * @author ob
 */
public class ShowLogInConsoleAction extends AnAction {

    public ShowLogInConsoleAction(Project project) {
        super();
        ConfigUtil.active = true;
        ConfigUtil.init(project);
        ConfigUtil.sqlTextDialog = new SqlText(project);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;
    }

    public void showLogInConsole(final Project project) {
        if (project == null) return;
        ConfigUtil.setRunning(project, true);
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(StringConst.TOOL_WINDOS);
        if (toolWindow != null) {
            toolWindow.activate(null);
        }
    }

}

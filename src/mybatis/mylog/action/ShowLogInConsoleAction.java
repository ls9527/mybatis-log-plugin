package mybatis.mylog.action;

import mybatis.mylog.action.gui.SqlText;
import mybatis.mylog.util.ConfigUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;


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
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;
    }

    public void showLogInConsole(final Project project) {
        if (project == null) return;
        ConfigUtil.setRunning(project, true);
        SqlText sqlText = new SqlText(project);
        sqlText.show();
    }

}

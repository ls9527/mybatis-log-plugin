package mybatis.mylog.action.gui;

import mybatis.mylog.MyBatisLogFilter;
import mybatis.mylog.action.MybatisLogProjectService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge
 */
public class ExecuteAction extends AnAction {
    private MySqlForm theForm;

    public ExecuteAction(MySqlForm mySqlForm) {
        super("Execute","Execute sql", AllIcons.Actions.Execute);
        theForm = mySqlForm;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String theSqlText = theForm.getTheSqlText();
        Project project = anActionEvent.getProject();
        MyBatisLogFilter.writeAndNavigateToSqlFile(project, theSqlText, MybatisLogProjectService.getInstance(project));
    }
}

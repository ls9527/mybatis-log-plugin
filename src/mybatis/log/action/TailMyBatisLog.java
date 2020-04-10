package mybatis.log.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import mybatis.log.Icons;

/**
 * @author bruce ge
 */
public class TailMyBatisLog  extends DumbAwareAction {

    public TailMyBatisLog(){
        super("MyBatisCodeHelperLogPlugin","MyBatisLogPlugin", Icons.MyBatisIcon);
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = getEventProject(e);
        new ShowLogInConsoleAction(project).showLogInConsole(project);
    }
}

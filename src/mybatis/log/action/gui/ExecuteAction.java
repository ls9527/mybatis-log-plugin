package mybatis.log.action.gui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author bruce ge
 */
public class ExecuteAction extends AnAction {
    private MySqlForm theForm;

    public ExecuteAction(MySqlForm mySqlForm) {
        super("Execute","Execute sql",AllIcons.Actions.Execute);
        theForm = mySqlForm;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String theSqlText = theForm.getTheSqlText();
        System.out.println(theSqlText);
    }
}

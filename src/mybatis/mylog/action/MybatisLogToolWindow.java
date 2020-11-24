package mybatis.mylog.action;

import mybatis.mylog.Icons;
import mybatis.mylog.action.gui.MyListForm;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author bruce ge
 */
public class MybatisLogToolWindow implements ToolWindowFactory {
    private JPanel myToolWindowContent;


    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        myToolWindowContent = new MyListForm(project).getThePanel();
        ContentFactory instance = ContentFactory.SERVICE.getInstance();
        Content content = instance.createContent(myToolWindowContent, "", false);
        content.setIcon(Icons.MyBatisIcon);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setAutoHide(true);
    }

    @Override
    public void init(ToolWindow window) {
        window.setIcon(Icons.MyBatisIcon);
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }
}

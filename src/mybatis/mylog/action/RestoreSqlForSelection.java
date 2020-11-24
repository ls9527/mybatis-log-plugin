package mybatis.mylog.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import mybatis.mylog.Icons;
import mybatis.mylog.action.gui.MySqlForm;
import mybatis.mylog.util.ConfigUtil;
import mybatis.mylog.util.PrintUtil;
import mybatis.mylog.util.RestoreSqlUtil;
import mybatis.mylog.util.StringConst;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * restore sql from selection
 *
 * @author ob
 */
public class RestoreSqlForSelection extends AnAction {
    public static final int MIN_LINES = 2;

    public RestoreSqlForSelection() {
        super(Icons.MyBatisIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        CaretModel caretModel = e.getData(LangDataKeys.EDITOR).getCaretModel();
        Caret currentCaret = caretModel.getCurrentCaret();
        String sqlText = currentCaret.getSelectedText();
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(StringConst.TOOL_WINDOS);
        if (!ConfigUtil.active || !toolWindow.isAvailable()) {
            new ShowLogInConsoleAction(project).showLogInConsole(project);
        }
        //激活Restore Sql tab
        toolWindow.activate(null);
        final String preparing = ConfigUtil.getPreparing(project);
        final String parameters = ConfigUtil.getParameters(project);
        if (StringUtils.isBlank(sqlText)) {
            Messages.showErrorDialog(project, "selection text must not be empty", "Restore Sql Error");
            return;
        }
        if (!sqlText.contains(preparing)) {
            Messages.showErrorDialog(project, "preparing does not exists", "Restore Sql Error");
            return;
        }
        if (!sqlText.contains(parameters)) {
            Messages.showErrorDialog(project, "parameters does not exists", "Restore Sql Error");
            return;
        }
        String[] sqlArr = sqlText.split("\n");
        if (sqlArr.length < MIN_LINES) {
            Messages.showErrorDialog(project, "sql text must greater equal than 2 lines", "Restore Sql Error");
            return;
        }
        doRestore(project, preparing, parameters, sqlArr);
    }

    private class RestoreContext {
        private String preparingLine;
        private String parametersLine;
    }

    private void doRestore(Project project, String preparing, String parameters, String[] sqlArr) {
        List<RestoreContext> restoreContext = findPrepareAndParemeters(sqlArr, preparing, parameters);
        if (restoreContext.isEmpty()) {
            Messages.showErrorDialog(project, "Can't find candidate text", "Restore Sql Error");
        }
        for (RestoreContext context : restoreContext) {
            restoreByParams(project, context.preparingLine, context.parametersLine);
        }
    }

    private void restoreByParams(Project project, String preparingLine, String parametersLine) {
        if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine)) {
            int indexNum = ConfigUtil.getIndexNum(project);
            String preStr = "--  " + indexNum + "  restore sql from selection  - ==>";
            ConfigUtil.setIndexNum(project, ++indexNum);
            String restoreSql = RestoreSqlUtil.restoreSql(project, preparingLine, parametersLine);

            if (ConfigUtil.getSqlFormat(project)) {
                restoreSql = PrintUtil.format(restoreSql);
            }

            MybatisLogProjectService instance = MybatisLogProjectService.getInstance(project);
            JPanel theSqlPanel = instance.getTheSqlPanel();

            String finalRestoreSql = restoreSql;

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    MySqlForm mySqlForm = new MySqlForm(project, preStr, finalRestoreSql);
                    theSqlPanel.add(mySqlForm.getThePanel());
                    theSqlPanel.revalidate();
                    theSqlPanel.repaint();
                }
            });

        }
    }

    private List<RestoreContext> findPrepareAndParemeters(String[] sqlArr, String preparing, String parameters) {
        List<RestoreContext> restoreContexts = new ArrayList<>();
        String preparingLine = null;
        String parametersLine = null;
        for (int i = 0; i < sqlArr.length; ++i) {
            String currentLine = sqlArr[i];
            if (StringUtils.isBlank(currentLine)) {
                continue;
            }
            if (currentLine.contains(preparing)) {
                preparingLine = currentLine;
                continue;
            } else {
                currentLine += "\n";
            }
            if (StringUtils.isEmpty(preparingLine)) {
                continue;
            }
            if (currentLine.contains(parameters)) {
                parametersLine = currentLine;
            }

            if (parametersLine != null) {
                RestoreContext restoreContext = new RestoreContext();
                restoreContext.parametersLine = parametersLine;
                restoreContext.preparingLine = preparingLine;
                restoreContexts.add(restoreContext);

                parametersLine = null;
                preparingLine = null;
            }
        }

        return restoreContexts;
    }


}
